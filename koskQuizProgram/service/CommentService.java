package com.example.koskQuizProgram.service;

import com.example.koskQuizProgram.model.CommentVO;

import java.util.List;

public interface CommentService {
    void insertCommentVO(CommentVO commentVO) throws Exception;
    List<CommentVO> selectCommentVOById(int id) throws Exception;
    void deleteCommentById(int id) throws Exception;
    void updateCommentVO(CommentVO commentVO) throws Exception;

}
