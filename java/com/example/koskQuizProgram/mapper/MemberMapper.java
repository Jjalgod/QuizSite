package com.example.koskQuizProgram.mapper;

import com.example.koskQuizProgram.model.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {

    Boolean duplicateId(String id) throws Exception;

    Boolean duplicateEmail(String email) throws Exception;

    void insertMember(MemberVO memberVO) throws Exception;

    MemberVO loginProcess(MemberVO memberVO) throws Exception;

    String findUserId(MemberVO memberVO) throws Exception;

    String findPassword(MemberVO memberVO) throws Exception;

    void updatePassword(MemberVO memberVO) throws Exception;

    List<MemberVO> getRankList();

    void updateHighScore(MemberVO memberVO) throws Exception;
    int findHighScore(MemberVO memberVO) throws Exception;
    void deleteUserInfo(MemberVO memberVO) throws Exception;
    void updateUserInfo(MemberVO memberVO) throws Exception;
    void updateID(MemberVO memberVO) throws Exception;
    void updateUserDescription(MemberVO memberVO) throws Exception;
}