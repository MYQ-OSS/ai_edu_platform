package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mayiqin.ai_edu_platform.ai.tool.QuizScoreTool;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.entity.po.QuizRecord;
import top.mayiqin.ai_edu_platform.entity.vo.QuizReportVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizSubmitVO;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.mapper.QuizRecordMapper;
import top.mayiqin.ai_edu_platform.service.QuizRecordService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @author m'y'q
* @description 针对表【t_quiz_record(用户答题记录表)】的数据库操作Service实现
* @createDate 2026-03-31 20:55:09
*/
@Service
@Slf4j
public class QuizRecordServiceImpl extends ServiceImpl<QuizRecordMapper, QuizRecord>
    implements QuizRecordService{

    private final ChatClient chatClient;
    private final QuizScoreTool quizScoreTool;
    private final QuestionMapper questionMapper;

    public QuizRecordServiceImpl(ChatClient chatClient, QuizScoreTool quizScoreTool, QuestionMapper questionMapper) {
        this.chatClient = chatClient;
        this.quizScoreTool = quizScoreTool;
        this.questionMapper = questionMapper;
    }

    /**
     * 提交答题结果
     * 调用AI进行判分，保存答题记录
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param userOptions 用户选择的选项（JSON格式）
     * @param userAnswer 用户文本答案
     * @return 答题提交结果VO（包含recordId、score、accuracy）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuizSubmitVO submitQuiz(Long userId, Long questionId, String userOptions, String userAnswer) {
        log.info("开始提交答题结果: userId={}, questionId={}", userId, questionId);

        try {
            // a) 查询题目信息
            Question question = questionMapper.selectById(questionId);
            if (question == null) {
                log.error("题目不存在: questionId={}", questionId);
                throw new BusinessException(400, "题目不存在");
            }
            log.debug("查询到题目信息: questionName={}", question.getQuestionName());

            // b) 调用AI判分
            log.info("开始调用AI进行判分...");
            String aiPrompt = quizScoreTool.scoreQuiz(
                    question.getQuestionName(),
                    question.getQuestionDesc(),
                    question.getOptions(),
                    null, // trueOptions由AI判断
                    question.getAnalysis(),
                    userOptions,
                    userAnswer
            );

            // 使用ChatClient调用AI
            String aiResponse = chatClient.prompt()
                    .user(aiPrompt)
                    .call()
                    .content();

            if (aiResponse == null || aiResponse.isBlank()) {
                log.error("AI返回内容为空");
                throw new BusinessException(500, "AI判分失败：返回内容为空");
            }

            log.debug("AI返回的原始JSON: {}", aiResponse);

            // c) 解析AI返回结果
            var jsonNode = quizScoreTool.validateAndParseJson(aiResponse);
            
            Integer score = jsonNode.get("score").asInt();
            String comment = jsonNode.get("comment").asText();
            String suggest = jsonNode.get("suggest").asText();
            String reason = jsonNode.get("reason").asText();
            String trueOptions = jsonNode.get("trueOptions").toString();
            String analysis = jsonNode.get("analysis").asText();
            BigDecimal accuracy = new BigDecimal(jsonNode.get("accuracy").asText());

            log.info("AI判分完成: score={}, accuracy={}", score, accuracy);

            // d) 构建QuizRecord对象
            QuizRecord quizRecord = QuizRecord.builder()
                    .userId(userId)
                    .questionId(questionId)
                    .userOptions(userOptions)
                    .userAnswer(userAnswer)
                    .score(score)
                    .comment(comment)
                    .suggest(suggest)
                    .reason(reason)
                    .trueOptions(trueOptions)
                    .analysis(analysis)
                    .accuracy(accuracy)
                    .createTime(new Date())
                    .build();

            // e) 保存到数据库
            boolean saved = this.save(quizRecord);
            if (!saved) {
                log.error("答题记录保存失败: userId={}, questionId={}", userId, questionId);
                throw new BusinessException(500, "答题记录保存失败");
            }

            log.info("答题记录保存成功: recordId={}", quizRecord.getId());

            // f) 构建返回VO
            QuizSubmitVO result = QuizSubmitVO.builder()
                    .recordId(quizRecord.getId())
                    .score(score)
                    .accuracy(accuracy)
                    .build();

            log.info("答题结果提交成功: recordId={}, score={}, accuracy={}", 
                    result.getRecordId(), result.getScore(), result.getAccuracy());

            return result;

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("提交答题结果失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI判分失败: " + e.getMessage());
        }
    }

    /**
     * 获取答题报告
     * 根据答题记录ID获取详细答题报告
     *
     * @param recordId 答题记录ID
     * @return 答题报告VO（包含完整的答题信息和AI判分结果）
     */
    @Override
    public QuizReportVO getQuizReport(Long recordId) {
        log.info("获取答题报告: recordId={}", recordId);
        
        // 1. 查询答题记录
        QuizRecord quizRecord = this.getById(recordId);
        if (quizRecord == null) {
            log.error("答题记录不存在: recordId={}", recordId);
            throw new BusinessException(404, "答题记录不存在");
        }
        
        log.debug("查询到答题记录: userId={}, questionId={}, score={}", 
                quizRecord.getUserId(), quizRecord.getQuestionId(), quizRecord.getScore());
        
        // 2. 构建返回VO
        QuizReportVO reportVO = QuizReportVO.builder()
                .recordId(quizRecord.getId())
                .userId(quizRecord.getUserId())
                .questionId(quizRecord.getQuestionId())
                .userOptions(quizRecord.getUserOptions())
                .userAnswer(quizRecord.getUserAnswer())
                .score(quizRecord.getScore())
                .comment(quizRecord.getComment())
                .suggest(quizRecord.getSuggest())
                .reason(quizRecord.getReason())
                .trueOptions(quizRecord.getTrueOptions())
                .analysis(quizRecord.getAnalysis())
                .accuracy(quizRecord.getAccuracy())
                .createTime(formatDate(quizRecord.getCreateTime()))
                .build();
        
        log.info("答题报告获取成功: recordId={}, score={}", reportVO.getRecordId(), reportVO.getScore());
        
        return reportVO;
    }

    /**
     * 格式化日期为字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串（yyyy-MM-dd HH:mm:ss）
     */
    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
