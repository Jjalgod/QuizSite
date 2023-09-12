package com.example.koskQuizProgram.serviceImpl;

import com.example.koskQuizProgram.mapper.BoardMapper;
import com.example.koskQuizProgram.model.BoardVO;
import com.example.koskQuizProgram.service.BoardService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    private BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public List<BoardVO> selectBoardVO() {
        return boardMapper.selectBoardVO();
    }

    @Override
    public List<BoardVO> selectMyBoardVO(String id) {
        return boardMapper.selectMyBoardVO(id);
    }


    // 이전에 조회한 게시글과 그 시간을 저장하는 맵
    private Map<Integer, Long> lastViewedMap = new HashMap<>();

    @Override
    public BoardVO selectBoardVOById(int id) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        long lastViewedTime = lastViewedMap.getOrDefault(id, 0L);

        // 현재 시간과 이전 조회 시간의 차이가 1분 이상일 경우에만 조회수 증가
        if (currentTimeMillis - lastViewedTime >= 60000) {
            boardMapper.updateCount(id);

            // 조회 시간 기록 업데이트
            lastViewedMap.put(id, currentTimeMillis);
        }

        return boardMapper.selectBoardVOById(id);
    }

    @Override
    public void insertBoardVO(BoardVO boardVO) throws Exception {
        boardMapper.insertBoardVO(boardVO);
    }

    @Override
    public void updateBoardVO(BoardVO boardVO) throws Exception {
        boardMapper.updateBoardVO(boardVO);
    }

    @Override
    public void deleteById(int id) throws Exception {
        boardMapper.deleteById(id);
    }

}