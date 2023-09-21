package com.example.koskQuizProgram.mapper;

import com.example.koskQuizProgram.model.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void insertCommentVO(CommentVO commentVO) throws Exception;
    List<CommentVO> selectCommentVOById(int id) throws Exception;
    void deleteCommentById(int id) throws Exception;
    void updateCommentVO(CommentVO commentVO) throws Exception;
}
