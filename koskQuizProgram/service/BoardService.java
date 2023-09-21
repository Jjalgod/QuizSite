package com.example.koskQuizProgram.service;

import com.example.koskQuizProgram.model.BoardVO;

import java.util.List;

public interface BoardService {
    List<BoardVO> selectBoardVO();
    List<BoardVO> selectMyBoardVO(String id);

    BoardVO selectBoardVOById(int id) throws Exception;
    void insertBoardVO(BoardVO boardVO) throws Exception;
    void updateBoardVO(BoardVO boardVO) throws Exception;
    void deleteById(int id) throws Exception;
}