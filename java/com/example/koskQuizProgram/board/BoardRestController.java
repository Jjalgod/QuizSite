package com.example.koskQuizProgram.board;


import com.example.koskQuizProgram.model.BoardVO;
import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.service.BoardService;
import com.example.koskQuizProgram.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/board")
public class BoardRestController {

    private MemberService memberService;
    private BoardService boardService;

    public BoardRestController(MemberService memberService, BoardService boardService) {
        this.memberService = memberService;
        this.boardService = boardService;
    }
    @PostMapping("/save")
    public void boardSave(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            HttpServletRequest request,
            HttpSession session,
            Model model
    ) throws Exception {


        Boolean flag = (Boolean) session.getAttribute("newPost");
        BoardVO boardVO = new BoardVO();

        if (imageFile != null){
            // 파일의 오리지널 네임
            String originalFileName = imageFile.getOriginalFilename();
            // 파일의 확장자
            String ext = originalFileName.substring(originalFileName.indexOf("."));
            // 서버에 저장될 때 중복된 파일 이름인 경우를 방지하기 위해 UUID에 확장자를 붙여 새로운 파일 이름을 생성
            String newFileName = UUID.randomUUID() + ext;

            String savePath = "C:/Users/17Z990-VA50K/Desktop/koskQuizProgram/koskQuizProgram/src/main/resources/static/uploadImage/"+newFileName;

            File file = new File(savePath);
            imageFile.transferTo(file);
            boardVO.setUploadName(newFileName);
        }
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        BoardVO boardVO1 = (BoardVO) session.getAttribute("boardVO");

        boardVO.setCode(1000);
        boardVO.setContent(content);
        boardVO.setTitle(title);

        if (memberVO != null) {
            String userId = memberVO.getId();
            boardVO.setRegId(userId);

            if (!flag) {
                int id = boardVO1.getId();
                boardVO.setId(id);
                log.info("Edit!");
                boardService.updateBoardVO(boardVO);
            } else {
                //저장
                log.info("Register");
                boardService.insertBoardVO(boardVO);
            }
        } else {
            session = request.getSession(false);
            if (session != null) session.invalidate();
        }
    }

    @GetMapping("/imageURL")
    public ResponseEntity<ByteArrayResource> getImage(@RequestParam(value = "id", defaultValue = "") int id, HttpServletResponse response,HttpSession session) throws Exception{

        Optional<BoardVO> boardVO = Optional.ofNullable(boardService.selectBoardVOById(id));
        String uploadName = boardVO.get().getUploadName();

        String savePath = "C:/Users/17Z990-VA50K/Desktop/koskQuizProgram/koskQuizProgram/src/main/resources/static/uploadImage/";

        Path imagePath = Paths.get(savePath,uploadName);
        byte[] image = Files.readAllBytes(imagePath);

        ByteArrayResource resource = new ByteArrayResource(image);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", Files.probeContentType(imagePath));
        headers.setCacheControl("no-cache");
        return ResponseEntity.ok().headers(headers).contentLength(image.length).body(resource);
    }

}
