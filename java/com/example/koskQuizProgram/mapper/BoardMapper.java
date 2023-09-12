package com.example.koskQuizProgram.mapper;

import com.example.koskQuizProgram.model.BoardVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardVO> selectBoardVO();
    List<BoardVO> selectMyBoardVO(String id);
    BoardVO selectBoardVOById(int id) throws Exception;
    void updateCount(int id) throws Exception;
    void deleteById(int id) throws Exception;
    void insertBoardVO(BoardVO boardVO) throws Exception;
    void updateBoardVO(BoardVO boardVO) throws Exception;
}