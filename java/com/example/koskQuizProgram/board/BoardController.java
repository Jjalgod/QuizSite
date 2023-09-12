package com.example.koskQuizProgram.board;

import com.example.koskQuizProgram.model.BoardVO;
import com.example.koskQuizProgram.model.CommentVO;
import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.model.Message;
import com.example.koskQuizProgram.service.BoardService;
import com.example.koskQuizProgram.service.CommentService;
import com.example.koskQuizProgram.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/board")
public class BoardController {
    private MemberService memberService;
    private BoardService boardService;
    private CommentService commentService;

    public BoardController(MemberService memberService, BoardService boardService, CommentService commentService) {
        this.memberService = memberService;
        this.boardService = boardService;
        this.commentService = commentService;
    }

    /*랭킹 게시판*/

    @RequestMapping("/rank")
    public String rankList(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        List<MemberVO> members = memberService.getRankList();
        model.addAttribute("members", members);
        model.addAttribute("listSize", members.size());
        return "/board/rankList";
    }


    @RequestMapping("/list")
    public String boardList(
            Model model,
            @RequestParam(name="start",required = false,defaultValue ="0") int start,
            HttpSession session,
            HttpServletRequest request
    ) {

        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        List<BoardVO> result = boardService.selectBoardVO();

        int totalCount = result.size(); // 모든 게시물 숫자
        int totalPage = totalCount / 10; // 총 페이지 수
        if (totalCount % 10 != 0){
            totalPage++;
        }
        List<BoardVO> currentList = new ArrayList<>();
        if (start == 0){
            for(int i=0;i<10;i++){
                if (i >= totalCount){
                    break;
                }else{
                    currentList.add(result.get(i));
                }
            }
            start = 1;
        }else{
            int startPoint = (start-1) * 10;
            int endPoint = start * 10 -1;

            for(int i=startPoint;i<=endPoint;i++){
                if (i >= totalCount){
                    break;
                }else{
                    currentList.add(result.get(i));
                }
            }
        }
        // start 1이면 0~9  // start 2면 10~19 // start 3이면 20~29
        model.addAttribute("result", currentList);
        model.addAttribute("totalCount",totalCount);
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("currentPage",start);

        return "/board/list";
    }

    /*내 게시글 리스트*/

    @RequestMapping("/myPost")
    public String boardMyList(
            Model model,
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(name="start",required = false,defaultValue ="0") int start
    ) {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        else {
            List<BoardVO> result = boardService.selectMyBoardVO(memberVO.getId());
            LocalDateTime recentlyDate;
            if (!result.isEmpty()){
                recentlyDate = result.get(0).getRegDate();
            }else{
                recentlyDate = null;
            }
            int totalCount = result.size(); // 모든 게시물 숫자
            int totalPage = totalCount / 10; // 총 페이지 수
            if (totalCount % 10 != 0){
                totalPage++;
            }
            List<BoardVO> currentList = new ArrayList<>();
            if (start == 0){
                for(int i=0;i<10;i++){
                    if (i >= totalCount){
                        break;
                    }else{
                        currentList.add(result.get(i));
                    }
                }
                start = 1;
            }else{
                int startPoint = (start-1) * 10;
                int endPoint = start * 10 -1;

                for(int i=startPoint;i<=endPoint;i++){
                    if (i >= totalCount){
                        break;
                    }else{
                        currentList.add(result.get(i));
                    }
                }
            }
            // start 1이면 0~9  // start 2면 10~19 // start 3이면 20~29
            model.addAttribute("result", currentList);
            model.addAttribute("totalCount",totalCount);
            model.addAttribute("totalPage",totalPage);
            model.addAttribute("currentPage",start);
            model.addAttribute("recentlyDate",recentlyDate);
        }
        return "/board/myPost";
    }

    /*게시글 보기*/

    @RequestMapping("/view")
    public String boardView(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "id", defaultValue = "") int id
    ) throws Exception {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        if (id > 0) {
            Optional<BoardVO> boardVO = Optional.ofNullable(boardService.selectBoardVOById(id));
            model.addAttribute("boardVO", boardVO.get());
            session.setAttribute("boardVO", boardVO.get());

            List<CommentVO> comments = commentService.selectCommentVOById(id);
            model.addAttribute("comments", comments);
        } else {
            return "<script>alert('게시물이 없습니다..');location.href='/board/list';</script>";
        }
        return "/board/view";
    }

    /*게시글 작성*/


    @RequestMapping("/write")
    public String boardWrite(
            Model model,
            HttpServletRequest request,
            HttpSession session
    ) {

        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");


        if (memberVO != null) {
            BoardVO boardVO = new BoardVO();
            boardVO.setCode(1000);
            Boolean newPost = true;
            session.setAttribute("newPost",newPost);
            model.addAttribute("boardVO", boardVO);
        } else {
            return "main/loginRequired";
        }
        return "/board/write";
    }

    /*게시물 삭제*/

    @PostMapping("/delete")
    public String delete(
            @RequestParam(value = "del[]", defaultValue = "") int[] del,
            Model model,
            HttpSession session
    ) throws Exception {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");

        Message message = new Message();
        message.setHref("/board/list");

        boolean flag = false; // 권한이 없는 게시물이 있는지 여부 플래그

        if (memberVO == null) {
            return "main/loginRequired";
        } else {
            for (int postId : del) {
                BoardVO post = boardService.selectBoardVOById(postId);
                if (post == null) {
                    // 게시물이 없을 경우 처리 (예: 예외 던지거나 메시지 설정)
                    continue;
                }
                String memberId = memberVO.getId();
                String id = post.getRegId();

                if (!memberId.equals(id)) {
                    flag = true;
                }
            }
            if (flag) {
                message.setMessage("권한이 없습니다. 자신의 게시글만 삭제가능합니다.");
            } else {
                for(int postId : del) {
                    boardService.deleteById(postId);
                }
                message.setMessage("삭제되었습니다.");
            }
        }

        model.addAttribute("data", message);
        return "message/message";
    }

    @PostMapping("/deleteInView")
    public String deleteInView(
            Model model,
            @SessionAttribute("boardVO") BoardVO boardVO,
            HttpServletRequest request,
            HttpSession session
    ) throws Exception {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");

        Message message = new Message();
        message.setHref("/board/list");

        int id = boardVO.getId();

        if (memberVO == null) {
            return "main/loginRequired";
        } else {
            String regId = memberVO.getId();
            if (regId.equals(boardVO.getRegId())) {
                boardService.deleteById(id);
                if (id > 0) {
                    message.setMessage("삭제 되었습니다.");
                } else {
                    message.setMessage("삭제 실패했습니다.");
                }
                model.addAttribute("data", message);
                log.info("DeleteMessage2 -{}", message);
            } else {
                message.setMessage("권한이 없습니다.");
                model.addAttribute("data", message);
                log.info("DeleteMessage -{}", message);
            }
        }
        log.info("DeleteMessage -{}", message);
        model.addAttribute("data", message);
        return "/message/message";
    }

    /*게시물 수정*/


    @RequestMapping("/modify")
    public String boardModify(
            @RequestParam(value = "id", defaultValue = "") int id,
            @SessionAttribute("boardVO") BoardVO boardVO1,
            HttpServletRequest request,
            HttpSession session,
            Model model
    ) throws Exception {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");

        Message message = new Message();
        message.setHref("/board/list");

        if (memberVO == null) {
            return "main/loginRequired";
        } else {
            String regId = memberVO.getId();
            if (regId.equals(boardVO1.getRegId())) {
                if (id > 0) {
                    //게시물 조회
                    Optional<BoardVO> boardVO = Optional.ofNullable(boardService.selectBoardVOById(id));
                    model.addAttribute("boardVO", boardVO.get());
                    boolean newPost = false;
                    session.setAttribute("newPost",newPost);
                } else {
                    message.setMessage("게시물이 없습니다.");
                    model.addAttribute("data", message);
                    return "/message/message";
                }
                model.addAttribute("data",message);
            } else {
                message.setMessage("권한이 없습니다.");
                model.addAttribute("data", message);
                return "/message/message";
            }
        }
        return "/board/write";
    }

    /*댓글 추가*/
    @PostMapping("/comment")
    public String commentSave(
            @RequestParam int boardId,
            @RequestParam String content,
            Model model,
            HttpSession session
    ) throws Exception {
        CommentVO commentVO = new CommentVO();
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        Message message = new Message();
        message.setHref("/board/view?id=" + boardId);
        log.info("Comment Message123 -{}", message);

        commentVO.setBoardId(boardId);
        commentVO.setComment(content);
        commentVO.setRegId(memberVO.getId());

        log.info("CommentVO -{}", commentVO);
        commentService.insertCommentVO(commentVO);
        message.setMessage("게시되었습니다.");

        model.addAttribute("data", message);
        log.info("Comment Message -{}", message);
        return "/message/message";
    }

    /*댓글 수정*/
    public String commentModify()
    {
        return null;
    }

    /*댓글 삭제*/
    public String commentDelete()
    {
        return null;
    }
}