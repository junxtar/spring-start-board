package com.make.board.repository;

import com.make.board.entity.BoardEntity;
import com.make.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity>findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);

}
