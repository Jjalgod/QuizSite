package com.example.koskQuizProgram.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentVO {
    private int id;
    private int boardId;
    private String comment;
    private String regId;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
