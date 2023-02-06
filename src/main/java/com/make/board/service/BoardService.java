package com.make.board.service;

import com.make.board.dto.BoardDTO;
import com.make.board.entity.BaseEntity;
import com.make.board.entity.BoardEntity;
import com.make.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//Jpa의 save 메서드는 엔티티 객체를 매개변수로 사용을 하는데 우리는 그 작업을 하기 위해서
//DTO -> Entity (Entity Class)
//or
//Entity -> DTO (DTO Class)
//로 변환 하는 과정이 필요하다.
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO){
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }
}
