package top.mayiqin.ai_edu_platform.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mayiqin.ai_edu_platform.dto.question.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.vo.QuestionVO;

import java.util.Locale;

@RestController
@RequestMapping("/api/quiz")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/generate")
    public Result<QuestionVO> generateQuiz(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader(name = "Content-Type", required = false) String contentType,
            @Valid @RequestBody QuestionGenerateDTO request) {
        validateHeaders(authorization, contentType);
        QuestionVO data = questionService.generateQuestion(request);
        return Result.success("题目生成成功", data);
    }

    private void validateHeaders(String authorization, String contentType) {
        if (authorization != null && !authorization.isBlank() && !authorization.startsWith("Bearer ")) {
            throw new BusinessException(400, "Authorization格式错误，应为Bearer {token}");
        }
        if (contentType != null && !contentType.isBlank() &&
                !contentType.toLowerCase(Locale.ROOT).startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            throw new BusinessException(400, "Content-Type必须为application/json");
        }
    }
}
