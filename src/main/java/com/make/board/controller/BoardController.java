package com.make.board.controller;

import com.make.board.dto.BoardDTO;
import com.make.board.entity.BoardEntity;
import com.make.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/save")
    public String saveForm(){
        return "save";
    }

    //데이터를 받을 때 dto클래스를 활용
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO){
        boardService.save(boardDTO);
        return "index";
    }
    @GetMapping("/")
    public String findAll(Model model){
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable){
        /*
        * 해당 게시글의 조회수를 하나 올리고
        * 게시글 데이터를 가져와서 detail.html에 출력
        * */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
    }
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model){
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate",boardDTO);
        return "update";
    }
    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model){
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model){
        boardService.delete(id);
        return "redirect:/board/";
    }
    //board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1)Pageable pageable, Model model){
         pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // if-> page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 *3* 4 5

        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개

        model.addAttribute("boardList",boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage",endPage);
        return "paging";
    }
}
