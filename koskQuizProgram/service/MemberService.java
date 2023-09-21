package com.example.koskQuizProgram.service;

import com.example.koskQuizProgram.model.MemberVO;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface MemberService {

    Boolean duplicateId(String id) throws Exception;

    Boolean duplicateEmail(String email) throws Exception;

    void insertMember(MemberVO memberVO) throws Exception;
    Boolean loginProcess(MemberVO memberVO, HttpServletRequest request) throws Exception;

    Map<String, String> formValidation(Errors errors);
    String findUserId(MemberVO memberVO) throws Exception;
    String findPassword(MemberVO memberVO) throws Exception;
    boolean updatePassword(MemberVO memberVO) throws Exception;
    List<MemberVO> getRankList();
    void updateHighScore(MemberVO memberVO) throws Exception;
    int findHighScore(MemberVO memberVO) throws Exception;
    boolean deleteUserInfo(MemberVO memberVO) throws Exception;
    boolean updateUserInfo(MemberVO memberVO) throws Exception;
    boolean updateID(MemberVO memberVO) throws Exception;
    void updateUserDescription(MemberVO memberVO) throws Exception;
}