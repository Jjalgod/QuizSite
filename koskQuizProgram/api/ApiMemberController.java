package com.example.koskQuizProgram.api;

import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class ApiMemberController {
    private final MemberService memberService;

    public ApiMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /*아이디 중복체크*/
    @GetMapping("/checkId")
    public Boolean existId(
            @RequestParam("userId") String userId
    ) throws Exception {
        Boolean result = memberService.duplicateId(userId);

        //true로 리턴되면 아이디가 선정되어있다.
        if (result) {
            return false;
        }
        return true;
    }

    /*아이디찾기*/
    @GetMapping("/findId")
    public String findId(
            @RequestParam("name") String name, @RequestParam("email") String email
    ) throws Exception {

        String result = "";

        if (!name.isEmpty() && !email.isEmpty()) {
            MemberVO memberVO = new MemberVO();
            memberVO.setName(name);
            memberVO.setEmail(email);

            String id = memberService.findUserId(memberVO);

            if (id == null) {
                result = "false";
            } else {
                result = id;
            }
        }

        return result;
    }

    /*비번찾기*/
    @GetMapping("/findPw")
    public String findPwPost(
            @RequestParam(value = "id", defaultValue = "") String id,
            @RequestParam(value = "email", defaultValue = "") String email
    ) throws Exception {
        MemberVO memberVO = new MemberVO();
        memberVO.setId(id);
        memberVO.setEmail(email);

        String pass = memberService.findPassword(memberVO);

        return pass;
    }

    /*회원탈퇴*/
    @GetMapping("/delete")
    public String deleteUser(
            @SessionAttribute("memberVO") MemberVO memberVO,
            HttpServletRequest request
    ) throws Exception {

        if(memberVO == null) {
            return "main/loginRequired";
        }
        boolean deleted = memberService.deleteUserInfo(memberVO);

        if (deleted) {
            memberService.deleteUserInfo(memberVO);
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            return "<script>alert('삭제되었습니다.');location.href='/main';</script>";
        } else {
            return "<script>alert('삭제실패했습니다.');location.href='/member/userPage';</script>";
        }
    }

    /*이메일, 이름 수정*/
    @PostMapping("/modify")
    public String userPostModify(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String description,
            @SessionAttribute("memberVO") MemberVO memberVO
    ) throws Exception {

        if(memberVO == null) {
            return "main/loginRequired";
        }
        boolean duplicatedEmail = memberService.duplicateEmail(email);

        if(name.equals("")){
            name = memberVO.getName();
        }
        if(email.equals("")){
            email = memberVO.getEmail();
        }
        if(description.equals("")){
            description = memberVO.getDescription();
        }

        memberVO.setName(name);
        memberVO.setDescription(description);


        if(!duplicatedEmail) {
            memberVO.setEmail(email);
            boolean modified = memberService.updateUserInfo(memberVO);
            if (modified) {
                memberService.updateUserInfo(memberVO);
                return "<script>alert('수정되었습니다.');location.href='/member/userPage';</script>";
            } else {
                return "<script>alert('수정실패했습니다..');location.href='/member/userPage';</script>";
            }
        }
        return "<script>alert('이메일 중복입니다.');location.href='/member/userPage';</script>";
    }

    /*아이디 수정*/
    @PostMapping("/modifyID")
    public String userIModifyId(
            @RequestParam String id,
            @SessionAttribute("memberVO") MemberVO memberVO
    ) throws Exception {

            if(memberVO == null) {
                return "main/loginRequired";
            }

            memberVO.setId(id);
            boolean modifiedID = memberService.updateID(memberVO);
            if (modifiedID) {
                memberService.updateID(memberVO);
                return "<script>alert('수정되었습니다.');location.href='/member/userPage';</script>";
            } else {
                return "<script>alert('수정실패했습니다.');location.href='/member/userPage';</script>";
            }
    }

    /*비밀번호 수정*/
    @PostMapping("/modifyPW")
    public String userModifyPW(
            @RequestParam String password,
            @SessionAttribute("memberVO") MemberVO memberVO
    ) throws Exception {

        if(memberVO == null) {
            return "main/loginRequired";
        }

        memberVO.setPassword(password);

        boolean modifiedPW = memberService.updatePassword(memberVO);
        if (modifiedPW) {
            memberService.updatePassword(memberVO);
            return "<script>alert('수정되었습니다.');location.href='/member/userPage';</script>";
        } else {
            return "<script>alert('수정실패했습니다.');location.href='/member/userPage';</script>";
        }
    }

    /*한줄소개 수정*/


}

