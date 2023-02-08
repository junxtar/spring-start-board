package com.make.board.service;

import com.make.board.dto.BoardDTO;
import com.make.board.entity.BaseEntity;
import com.make.board.entity.BoardEntity;
import com.make.board.entity.BoardFileEntity;
import com.make.board.repository.BoardFileRepository;
import com.make.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Jpa의 save 메서드는 엔티티 객체를 매개변수로 사용을 하는데 우리는 그 작업을 하기 위해서
//DTO -> Entity (Entity Class)
//or
//Entity -> DTO (DTO Class)
//로 변환 하는 과정이 필요하다.
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository  boardRepository;
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        //파일 첨부 여부에 따라 로직 분리
        if(boardDTO.getBoardFile().isEmpty()){
            //첨부 파일 없음.
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        }else{
            //첨부 파일 있음.
            /*
            * 1. DTO에 담긴 파일을 꺼냄
            * 2. 파일의 이름 가져옴
            * 3. 서버 저장용 이름을 만듬
            * 4. 저장 경로 설정
            * 5. 해당 경로에 파일 저장
            * 6. board_table에 해당 데이터 save처리
            * 7. board_file_table에 해당 데이터 save처리
            */
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long saveId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(saveId).get();
            for(MultipartFile boardFile: boardDTO.getBoardFile()) {
//                MultipartFile boardFile = boardDTO.getBoardFile(); // 1
                String originalFilename = boardFile.getOriginalFilename(); // 2
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3
                String savePath = "/Users/choi-junyoung/board/springboot_img/" + storedFileName; // 4
                boardFile.transferTo(new File(savePath)); // 5
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }
        }

    }
    public List<BoardDTO>findAll(){
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for(BoardEntity boardEntity : boardEntityList){
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }
    @Transactional
    public BoardDTO findById(Long id){
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if(optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        }else{
            return null;
        }
    }
    @Transactional //jpa data가 관리하는 sql문이 아닌 메서드는 이 어노테이션을 붙여야함
    public void updateHits(Long id){
        boardRepository.updateHits(id);
    }

    public BoardDTO update(BoardDTO boardDTO){
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }
    public void delete(Long id){
        boardRepository.deleteById(id);
    }
    public Page<BoardDTO> paging(Pageable pageable){
        int page = pageable.getPageNumber() - 1;    //한 페이지에 보여줄 글 갯수
        int pageLimit = 3;  //한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        //page 위치에 있는 값은 0부터 시작
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));
        return boardDTOS;
    }
}
