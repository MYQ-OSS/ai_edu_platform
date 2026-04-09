package top.mayiqin.ai_edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuizCollectDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuizSubmitDTO;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.service.QuizCollectService;
import top.mayiqin.ai_edu_platform.service.QuizRecordService;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizReportVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizSubmitVO;

/**
 * 题目管理控制器
 * 提供AI生成题目的相关接口
 * @author m'y'q
 */
@RestController
@Slf4j
@RequestMapping("/quiz")
@Tag(name = "题目管理", description = "AI智能生成题目相关接口")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizRecordService quizRecordService;
    private final QuizCollectService quizCollectService;

    public QuestionController(QuestionService questionService, QuizRecordService quizRecordService, QuizCollectService quizCollectService) {
        this.questionService = questionService;
        this.quizRecordService = quizRecordService;
        this.quizCollectService = quizCollectService;
    }

    /**
     * 生成AI题目
     * 根据用户提供的技术方向、期望薪资等信息，AI智能生成对应的面试题目
     *
     * @param request 题目生成请求参数（包含技术方向、期望薪资、用户身份、就业城市、答题限时）
     * @return 生成的题目信息（包含题目ID、题目名称、题目描述、选项、目标薪资、技术方向）
     */
    @Operation(
        summary = "生成AI题目",
        description = "根据用户提供的技术方向、期望薪资等信息，AI智能生成对应的面试题目。需要携带有效的JWT Token进行身份验证。"
    )
    @PostMapping("/generate")
    public Result<QuestionVO> generateQuiz(@Valid @RequestBody QuestionGenerateDTO request) {
        log.info("生成题目请求: direction={}, targetSalary={}, identity={}, city={}, timeLimit={}",
                request.getDirection(),
                request.getTargetSalary(),
                request.getIdentity(),
                request.getCity(),
                request.getTimeLimit());
        
        // 调用服务层生成题目
        QuestionVO data = questionService.generateQuestion(request);
        
        log.info("题目生成成功: questionId={}, questionName={}", data.getQuestionId(), data.getQuestionName());
        return Result.success(MessageConstant.QUESTION_GENERATE_SUCCESS, data);
    }

    /**
     * 提交答题结果
     * 用户提交答题结果，后端调用AI判分并生成解析
     *
     * @param request 答题提交请求参数（包含userId、questionId、userOptions、userAnswer）
     * @return 答题提交结果（包含recordId、score、accuracy）
     */
    @Operation(
        summary = "提交答题结果",
        description = "用户提交答题结果，后端调用AI判分并生成解析。需要携带有效的JWT Token进行身份验证。"
    )
    @PostMapping("/submit")
    public Result<QuizSubmitVO> submitQuiz(@Valid @RequestBody QuizSubmitDTO request) {
        log.info("提交答题结果请求: userId={}, questionId={}", 
                request.getUserId(), request.getQuestionId());
        
        // 调用服务层提交答题结果
        QuizSubmitVO data = quizRecordService.submitQuiz(
                request.getUserId(),
                request.getQuestionId(),
                request.getUserOptions(),
                request.getUserAnswer()
        );
        
        log.info("答题结果提交成功: recordId={}, score={}, accuracy={}", 
                data.getRecordId(), data.getScore(), data.getAccuracy());
        return Result.success(MessageConstant.QUIZ_SUBMIT_SUCCESS, data);
    }

    /**
     * 获取答题报告
     * 根据答题记录ID获取详细答题报告（分数、解析、建议等）
     *
     * @param recordId 答题记录ID（路径参数）
     * @return 答题报告详细信息
     */
    @Operation(
        summary = "获取答题报告",
        description = "根据答题记录ID获取详细答题报告，包含分数、解析、建议等信息。需要携带有效的JWT Token进行身份验证。"
    )
    @GetMapping("/report/{recordId}")
    public Result<QuizReportVO> getQuizReport(@PathVariable Long recordId) {
        log.info("获取答题报告请求: recordId={}", recordId);
        
        // 调用服务层获取答题报告
        QuizReportVO data = quizRecordService.getQuizReport(recordId);
        
        log.info("答题报告获取成功: recordId={}, score={}", data.getRecordId(), data.getScore());
        return Result.success(MessageConstant.GET_QUIZ_REPORT_SUCCESS, data);
    }

    /**
     * 收藏/取消收藏题目
     * 用户收藏或取消收藏指定题目
     *
     * @param request 收藏请求参数（包含userId、questionId、isCollect）
     * @return 操作结果
     */
    @Operation(
        summary = "收藏/取消收藏题目",
        description = "用户收藏或取消收藏指定题目。需要携带有效的JWT Token进行身份验证。"
    )
    @PostMapping("/collect")
    public Result<Void> collectQuiz(@Valid @RequestBody QuizCollectDTO request) {
        log.info("收藏题目请求: userId={}, questionId={}, isCollect={}", 
                request.getUserId(), request.getQuestionId(), request.getIsCollect());
        
        // 调用服务层处理收藏逻辑
        quizCollectService.collectQuiz(
                request.getUserId(),
                request.getQuestionId(),
                request.getIsCollect()
        );
        
        String message = request.getIsCollect() ? "收藏成功" : "取消收藏成功";
        log.info("收藏操作成功: userId={}, questionId={}, message={}", 
                request.getUserId(), request.getQuestionId(), message);
        
        return Result.success(message, null);
    }
}
