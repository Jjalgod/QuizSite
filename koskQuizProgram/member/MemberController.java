package com.example.koskQuizProgram.member;

import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@Controller //userId 전부 id로 교체
@RequestMapping("/member")
public class MemberController {

    private final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String memberLogin() {
        return "member/login";
    }

    @GetMapping("/join")
    public String mebmerJoin() {
        return "member/join";
    }

    /*
       회원가입 처리
     */
    @ResponseBody
    @PostMapping("/join")
    public String memberJoinPost(
            @Valid MemberVO memberVO, Errors errors, Model model
    ) throws Exception {

        if (errors.hasErrors()) {
            Map<String, String> validate = memberService.formValidation(errors);

            for (String key : validate.keySet()) {
                logger.info(key, validate.get(key));
                model.addAttribute(key, validate.get(key));
            }
        }

        boolean idCheck = memberService.duplicateId(memberVO.getId());
        boolean emailCheck = memberService.duplicateEmail(memberVO.getEmail());

        String message;
        if (!idCheck && !emailCheck) memberService.insertMember(memberVO);
        else {
            message = "<script>alert('이메일이 중복됩니다.');location.href='/member/join';</script>";
            return message;
        }

        message = "<script>alert('회원가입이 완료되었습니다.');location.href='/member/login';</script>";
        return message;
    }

    //    로그인처리
    @ResponseBody
    @PostMapping("/loginProcess")
    public String loginProcess(
            @RequestParam(value = "id", defaultValue = "") String id,
            @RequestParam(value = "password", defaultValue = "") String password,
            HttpServletRequest request,
            HttpSession session
    ) throws Exception {
        if (!id.equals("") && !password.equals("")) {
            MemberVO memberVO = new MemberVO();
            memberVO.setId(id);
            memberVO.setPassword(password);

            Boolean result = memberService.loginProcess(memberVO, request);
            String message = "<script>alert('로그인 정보가 일치하지 않습니다.');location.href='/member/login';</script>";
            if (!result) {
                return message;
            }
            message = "<script>location.href='/quizMenu';</script>";
            return message;
        }
        String message = "<script>alert('로그인 정보가 일치하지 않습니다.');location.href='/member/login';</script>";
        return message;
    }

    /*
     * 아이디 찾기 페이지
     * */
    @GetMapping("/find_id")
    public String findId() {
        return "member/find_id";
    }

    /*
     * 비밀번호 찾기 페이지
     * */
    @RequestMapping("/find_pw")
    public String findPw() {
        return "member/find_pw";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        return "redirect:/main";
    }

    @RequestMapping("/userPage")
    public String userPage(HttpSession session){
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        return "user/userPage";
    }

    @RequestMapping("/editPerson")
    public String editPerson(HttpSession session){
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        return "user/editPerson";
    }

    @RequestMapping("/editId")
    public String editId(HttpSession session){
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        return "user/editId";
    }

    @RequestMapping("/editPass")
    public String editPass(HttpSession session){
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        return "user/editPass";
    }

}