package com.example.koskQuizProgram.serviceImpl;

import com.example.koskQuizProgram.mapper.MemberMapper;
import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    private MemberMapper memberMapper;
    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    //아이디 중복체크
    @Override
    public Boolean duplicateId(String id) throws Exception {
        Boolean res = memberMapper.duplicateId(id);
        return res ? true : false;
    }

    //이메일 중복체크
    @Override
    public Boolean duplicateEmail(String email) throws Exception {
        Boolean res = memberMapper.duplicateEmail(email);
        return res ? true : false;
    }

    //회원 가입 처리
    @Override
    public void insertMember(MemberVO memberVO) throws Exception {
        memberMapper.insertMember(memberVO);
    }

    /*
     * 로그인 처리
     * */


    @Override
    public Boolean loginProcess(MemberVO memberVO, HttpServletRequest request) throws Exception {
        MemberVO result = memberMapper.loginProcess(memberVO);

        if (result != null) {
            //세션 정보 생성
            HttpSession session = request.getSession();
            session.setAttribute("memberVO", result);
            session.setMaxInactiveInterval(3600);
            return true;
        }
        return false;
    }

    //회원가입 폼 검증 (비번 영문, 숫자 자리수 등등)
    @Override
    public Map<String, String> formValidation(Errors errors) {
        Map<String, String> result = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            result.put(validKeyName, error.getDefaultMessage());
        }
        return result;
    }

    /*
     * 아이디 찾기
     */
    @Override
    public String findUserId(MemberVO memberVO) throws Exception {
        return memberMapper.findUserId(memberVO);
    }
    /*
     * 비밀번호 찾기
     * */
    @Override
    public String findPassword(MemberVO memberVO) throws Exception {
        return memberMapper.findPassword(memberVO);
    }

    /*
     * 비밀번호 변경
     * */
    @Override
    public boolean updatePassword(MemberVO memberVO) throws Exception {
        memberMapper.updatePassword(memberVO);
        return true;
    }

    /*
     * 랭킹리스트*/
    @Override
    public List<MemberVO> getRankList() {
        return memberMapper.getRankList();
    }

    /*
     * 하이스코어 갱신*/
    @Override
    public void updateHighScore(MemberVO memberVO) throws Exception {
        memberMapper.updateHighScore(memberVO);
    }

    @Override
    public int findHighScore(MemberVO memberVO) throws Exception {
        return memberMapper.findHighScore(memberVO);
    }

    /*
     * 회원정보 삭제*/
    @Override
    public boolean deleteUserInfo(MemberVO memberVO) throws Exception {
        memberMapper.deleteUserInfo(memberVO);
        return true;
    }

    /*이름, 이메일 수정*/
    @Override
    public boolean updateUserInfo(MemberVO memberVO) throws Exception {
        memberMapper.updateUserInfo(memberVO);
        return true;
    }

    @Override
    public boolean updateID(MemberVO memberVO) throws Exception {
        memberMapper.updateID(memberVO);
        return true;
    }

    @Override
    public void updateUserDescription(MemberVO memberVO) throws Exception {
        memberMapper.updateUserDescription(memberVO);
    }
}