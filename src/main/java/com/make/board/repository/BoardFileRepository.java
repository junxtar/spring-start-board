package com.make.board.repository;

import com.make.board.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository extends JpaRepository <BoardFileEntity, Long> {

}
