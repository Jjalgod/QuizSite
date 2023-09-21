package com.example.koskQuizProgram.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberVO {
    String name;
    String id;
    String password;
    String email;
    int score;
    String description;
}