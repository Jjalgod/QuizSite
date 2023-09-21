package com.example.koskQuizProgram.controller;

import com.example.koskQuizProgram.model.ApiVO;
import com.example.koskQuizProgram.model.MemberVO;
import com.example.koskQuizProgram.service.MemberService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
//@RestController
//@RequestMapping("/api")
@Controller
public class ApiController {

    private final MemberService memberService;
    private List<ApiVO> quizItems;
    private HashMap<Integer, ApiVO> correctQuizItems = new HashMap<>();
    private HashMap<Integer, ApiVO> wrongQuizItems = new HashMap<>();
    int currentIndex = 0;
    int correctIndex = 0;
    int score = 0;

    public ApiController(MemberService memberService) {
        this.memberService = memberService;
    }


    @RequestMapping("/quizMenu")
    public String quizMenu(HttpSession session, HttpServletRequest request) {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        return "/quiz/quizMenu";
    }

    @PostMapping("/submitAnswer")
    public String submitAnswer(
            Model model,
            @RequestParam String userAnswer,
            HttpSession session
    ) throws Exception {

        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");

        if (memberVO == null){
            return "main/loginRequired";
        }
        evaluateUserAnswer(userAnswer);
        int problemCount = (int) session.getAttribute("problemCount");

        if (currentIndex < problemCount/*quizItems.size()*/) {
            model.addAttribute("quizItem", quizItems.get(currentIndex));
            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            log.info("현재 점수 -{} ", score);

            model.addAttribute("score", score);
            log.info("High Score -{}", score);

            log.info("quizItems.size() -{}", quizItems.size());
            log.info("correctIndex -{} ", correctIndex);
            log.info("currentIndex -{} ", currentIndex);

            return "quiz/quizTemplate";
        } else {
            // 모든 문제를 푼 후에 다른 페이지로 이동하도록 설정합니다.
            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            model.addAttribute("correctQuizItem", correctQuizItems);
            log.info("correctQuizItems!!!!! -{}", correctQuizItems);
            model.addAttribute("wrongQuizItem", wrongQuizItems);
            log.info("wrongQuizItems!!!!! -{}", wrongQuizItems);

            model.addAttribute("score", score);

            int HighScore = memberService.findHighScore(memberVO);
            log.info("HighScore -{}", HighScore);
            log.info("score -{}", score);
            if(HighScore < score) {
                memberVO.setScore(score);
                memberService.updateHighScore(memberVO);
            }

            log.info("High Score -{}", score);

            return "quiz/resultPage";
        }
    }

    private void evaluateUserAnswer(
            String userAnswer
    ) {
        // 여기서 사용자의 답을 평가하고 다음 문제로 이동하는 로직을 작성합니다.
        // 예시로 간단히 정답인 경우에만 currentIndex를 증가시킵니다.
        if (quizItems.get(correctIndex).getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
            log.info("currentIndex123 -{}", currentIndex);
            log.info("correctIndex123 -{}", correctIndex);

            correctQuizItems.put(currentIndex, quizItems.get(currentIndex));
            log.info("correctQuizItems123 -{}", correctQuizItems);

            if(quizItems.get(currentIndex).getDifficulty().equals("easy"))
            {
                score += 3;
            }
            else if(quizItems.get(currentIndex).getDifficulty().equals("medium"))
            {
                score += 5;
            }
            else {
                score += 50;
            }

            correctIndex++;
        } else {
            wrongQuizItems.put(currentIndex, quizItems.get(currentIndex));
            log.info("wrongQuizItems123 -{}", wrongQuizItems);
        }
        currentIndex++;
    }

    @GetMapping("/quizStart")
    public String quizStart(HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }

        return "/quiz/quizTemplate";
    }

    @PostMapping("/quizStart")
    public String quizStartPost(
            @RequestParam(value = "difficulty", defaultValue = "") String difficulty,
            @RequestParam(value = "problemType", defaultValue = "") String problemType,
            @RequestParam(value = "problemCount", defaultValue = "") int problemCount,
            Model model,
            HttpSession session
    ){
        MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");
        if(memberVO == null) {
            return "main/loginRequired";
        }
        score = 0;
        session.setAttribute("problemCount", problemCount);
        session.setAttribute("difficulty",difficulty);
        session.setAttribute("problemType",problemType);
//        Boolean result = (Boolean) session.getAttribute("result");
//        if(!result)
//        {
//            return "redirect:/main";
//        }

        if(difficulty.equals("easy") && problemType.equals("boolean"))
        {
            initializeQuizData();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("medium") && problemType.equals("boolean"))
        {
            initializeQuizData1();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("hard") && problemType.equals("boolean"))
        {
            initializeQuizData2();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("easy") && problemType.equals("multiple"))
        {
            initializeQuizData3();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("medium") && problemType.equals("multiple"))
        {
            initializeQuizData4();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("hard") && problemType.equals("multiple"))
        {
            initializeQuizData5();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("random") && problemType.equals("boolean"))
        {
            initializeQuizData6();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("random") && problemType.equals("multiple"))
        {
            initializeQuizData7();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("easy") && problemType.equals("random"))
        {
            initializeQuizData8();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("medium") && problemType.equals("random"))
        {
            initializeQuizData9();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("hard") && problemType.equals("random"))
        {
            initializeQuizData10();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        else if(difficulty.equals("random") && problemType.equals("random"))
        {
            initializeQuizData11();
            log.info("quizitems -{}", quizItems);

            model.addAttribute("currentIndex", currentIndex);
            model.addAttribute("correctIndex", correctIndex);

            correctQuizItems = new HashMap<>();
            wrongQuizItems = new HashMap<>();

            log.info("StartQuizCorrect -{}", correctQuizItems);
            log.info("StartQuizWrong -{}", wrongQuizItems);

            model.addAttribute("quizItem", quizItems.get(currentIndex));
            return "/quiz/quizTemplate";
        }

        return "/main/main";
    }

    /*
     * api 받아온거
     * */
    private void initializeQuizData() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=easy&type=boolean");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData1() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=medium&type=boolean");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData2() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=hard&type=boolean");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData3() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=easy&type=multiple");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData4() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=medium&type=multiple");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData5() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=hard&type=multiple");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData6() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&type=boolean");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData7() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&type=multiple");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData8() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=easy");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData9() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=medium");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData10() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50&difficulty=hard");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

    private void initializeQuizData11() {
        quizItems = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        try {
            StringBuilder urlBuilder = new StringBuilder("https://opentdb.com/api.php?amount=50");
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line + "\n");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString()); // line -> result 변경
            quizItems = objectMapper.convertValue(
                    jsonNode.get("results"),
                    new TypeReference<List<ApiVO>>() {}
            );

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentIndex = 0;
        correctIndex = 0;
    }

}