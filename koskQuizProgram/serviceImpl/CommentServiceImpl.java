package com.example.koskQuizProgram.serviceImpl;

import com.example.koskQuizProgram.mapper.CommentMapper;
import com.example.koskQuizProgram.model.CommentVO;
import com.example.koskQuizProgram.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public void insertCommentVO(CommentVO commentVO) throws Exception {
        commentMapper.insertCommentVO(commentVO);
    }

    @Override
    public List<CommentVO> selectCommentVOById(int id) throws Exception{
        return commentMapper.selectCommentVOById(id);
    }

    @Override
    public void deleteCommentById(int id) throws Exception {
        commentMapper.deleteCommentById(id);
    }

    @Override
    public void updateCommentVO(CommentVO commentVO) throws Exception {
        commentMapper.updateCommentVO(commentVO);
    }
}
