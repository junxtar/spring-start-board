package com.make.board.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
public class BoardFileEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storeFileName;

    //참조관계 설정하기
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFileName, String storeFileName){
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoreFileName(storeFileName);
        boardFileEntity.setBoardEntity(boardEntity);
        return boardFileEntity;
    }

}
