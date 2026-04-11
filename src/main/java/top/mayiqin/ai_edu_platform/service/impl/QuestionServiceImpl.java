package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.mayiqin.ai_edu_platform.ai.tool.QuestionGenerateTool;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionAddDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionListQueryDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionUpdateDTO;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionListVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionVO;
import top.mayiqin.ai_edu_platform.utils.UserContext;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 * 使用 Spring AI 调用大语言模型生成面试题目
 * @author m'y'q
 * @description 针对表【t_question(AI生成题库表)】的数据库操作Service实现
 * @createDate 2026-03-31 20:55:08
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final ChatClient chatClient;
    private final QuestionGenerateTool questionGenerateTool;
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(ChatClient chatClient, QuestionGenerateTool questionGenerateTool, QuestionMapper questionMapper) {
        this.chatClient = chatClient;
        this.questionGenerateTool = questionGenerateTool;
        this.questionMapper = questionMapper;
    }

    @Override
    public QuestionVO generateQuestion(QuestionGenerateDTO request) {
        // 参数校验
        if (request == null) {
            throw new BusinessException(400, MessageConstant.REQUEST_PARAM_EMPTY);
        }
        
        try {
            // 调用 AI 生成题目
            String aiJson = callAiGenerateQuestion(request);
            
            // 使用 Tool 验证并解析 AI 返回的 JSON
            JsonNode root = questionGenerateTool.validateAndParseJson(aiJson);
            String questionName = root.get("questionName").asText();
            String questionDesc = root.get("questionDesc").asText();
            JsonNode optionsNode = root.get("options");
            String options = questionGenerateTool.getObjectMapper().writeValueAsString(optionsNode);

            // 构建题目实体并保存到数据库
            Question question = Question.builder()
                    .questionName(questionName)
                    .questionDesc(questionDesc)
                    .options(options)
                    .targetSalary(request.getTargetSalary())
                    .direction(request.getDirection())
                    .createTime(new Date())
                    .build();

            boolean saved = this.save(question);
            if (!saved || question.getId() == null) {
                log.error("AI生成题目保存失败: questionName={}, direction={}", question.getQuestionName(), question.getDirection());
                throw new BusinessException(500, MessageConstant.SAVE_QUESTION_FAILED);
            }

            log.info("题目生成并保存成功: questionId={}, questionName={}", question.getId(), question.getQuestionName());

            // 返回 VO
            return QuestionVO.builder()
                    .questionId(question.getId())
                    .questionName(question.getQuestionName())
                    .questionDesc(question.getQuestionDesc())
                    .options(question.getOptions())
                    .targetSalary(question.getTargetSalary())
                    .direction(question.getDirection())
                    .build();
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("题目生成失败: {}", e.getMessage(), e);
            
            // 尝试降级方案：从题库中查找符合要求的题目
            QuestionVO fallbackQuestion = getFallbackQuestion(request);
            if (fallbackQuestion != null) {
                log.info("AI生成题目失败，已使用降级方案从题库中找到题目: questionId={}", fallbackQuestion.getQuestionId());
                return fallbackQuestion;
            }
            
            // 降级方案也失败，抛出异常
            throw new BusinessException(500, MessageConstant.NO_SUITABLE_QUESTION_FOUND);
        }
    }

    /**
     * 调用 Spring AI ChatClient 生成题目
     *
     * @param request 题目生成请求参数
     * @return AI 返回的 JSON 字符串
     */
    private String callAiGenerateQuestion(QuestionGenerateDTO request) {
        // 使用 Tool 构建 Prompt
        String prompt = questionGenerateTool.generateQuestion(
                request.getDirection(),
                request.getTargetSalary(),
                request.getIdentity(),
                request.getCity(),
                request.getTimeLimit()
        );
        
        log.trace("调用AI生成题目，prompt: {}", prompt);

        try {
            // 使用 Spring AI ChatClient 调用大模型
            log.debug("开始调用 AI 接口...");
            String result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (result == null || result.isBlank()) {
                log.error("AI返回内容为空");
                throw new BusinessException(500, MessageConstant.AI_RESPONSE_EMPTY);
            }

            log.debug("AI返回内容长度: {}", result.length());

            return result;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (org.springframework.ai.retry.NonTransientAiException e) {
            // AI 服务非临时性异常（如 404、401、403 等）
            log.error("AI 服务调用失败: {}", e.getMessage());
            log.error("请检查：1) API Key 是否有效 2) 网络连接是否正常 3) 模型名称是否正确");
            log.debug("详细异常信息", e);
            throw new BusinessException(500, MessageConstant.AI_SERVICE_CALL_FAILED);
        } catch (Exception e) {
            log.error("调用AI接口失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "调用AI接口失败: " + e.getMessage());
        }
    }

    /**
     * 降级方案：从题库中查询用户未回答过的题目
     *
     * @param request 题目生成请求参数
     * @return 符合条件的题目VO，如果没有则返回null
     */
    private QuestionVO getFallbackQuestion(QuestionGenerateDTO request) {
        try {
            // 从 UserContext 获取当前用户ID
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                log.warn("无法获取当前用户ID，降级方案失败");
                return null;
            }

            // 调用 questionMapper.selectUnansweredQuestion()
            Question question = questionMapper.selectUnansweredQuestion(
                    userId,
                    request.getDirection(),
                    request.getTargetSalary()
            );

            // 如果找到题目，转换为 QuestionVO 返回
            if (question != null) {
                log.info("降级方案成功：找到未回答的题目 questionId={}", question.getId());
                return QuestionVO.builder()
                        .questionId(question.getId())
                        .questionName(question.getQuestionName())
                        .questionDesc(question.getQuestionDesc())
                        .options(question.getOptions())
                        .targetSalary(question.getTargetSalary())
                        .direction(question.getDirection())
                        .fromFallback(true)
                        .build();
            }

            log.warn("降级方案失败：未找到符合要求的题目");
            return null;
        } catch (Exception e) {
            log.error("降级方案执行失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addQuestion(QuestionAddDTO dto) {
        log.info("管理员新增题目: direction={}, targetSalary={}", dto.getDirection(), dto.getTargetSalary());
        
        // 构建题目实体
        Question question = Question.builder()
                .questionName(dto.getQuestionName())
                .questionDesc(dto.getQuestionDesc())
                .options(dto.getOptions())
                .targetSalary(dto.getTargetSalary())
                .direction(dto.getDirection())
                .analysis(dto.getAnalysis())
                .isDeleted("0")
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        
        // 保存到数据库
        boolean saved = this.save(question);
        if (!saved || question.getId() == null) {
            log.error("管理员新增题目保存失败: questionName={}, direction={}", dto.getQuestionName(), dto.getDirection());
            throw new BusinessException(500, MessageConstant.SAVE_QUESTION_FAILED);
        }
        
        log.info("题目新增成功: questionId={}, name={}", question.getId(), question.getQuestionName());
        return question.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuestion(QuestionUpdateDTO dto) {
        log.info("管理员编辑题目: id={}, direction={}", dto.getId(), dto.getDirection());
        
        // 查询题目是否存在
        Question existQuestion = this.getById(dto.getId());
        if (existQuestion == null || "1".equals(existQuestion.getIsDeleted())) {
            throw new BusinessException(404, MessageConstant.QUESTION_NOT_EXIST);
        }
        
        // 更新题目信息
        Question question = Question.builder()
                .id(dto.getId())
                .questionName(dto.getQuestionName())
                .questionDesc(dto.getQuestionDesc())
                .options(dto.getOptions())
                .targetSalary(dto.getTargetSalary())
                .direction(dto.getDirection())
                .analysis(dto.getAnalysis())
                .isDeleted(existQuestion.getIsDeleted())
                .createTime(existQuestion.getCreateTime())
                .updateTime(new Date())
                .build();
        
        // 执行更新
        boolean updated = this.updateById(question);
        if (!updated) {
            log.error("题目更新失败: id={}", dto.getId());
            throw new BusinessException(500, "题目更新失败");
        }
        
        log.info("题目更新成功: id={}, name={}", question.getId(), question.getQuestionName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long questionId) {
        log.info("管理员删除题目: id={}", questionId);
        
        // 查询题目是否存在
        Question existQuestion = this.getById(questionId);
        if (existQuestion == null || "1".equals(existQuestion.getIsDeleted())) {
            throw new BusinessException(404, MessageConstant.QUESTION_NOT_EXIST);
        }
        
        // 使用MyBatis-Plus的逻辑删除功能（自动设置 is_deleted='1'）
        boolean deleted = this.removeById(questionId);
        if (!deleted) {
            log.error("题目删除失败: id={}", questionId);
            throw new BusinessException(500, "题目删除失败");
        }
        
        log.info("题目删除成功: id={}", questionId);
    }

    @Override
    public Page<QuestionListVO> getQuestionList(QuestionListQueryDTO queryDTO) {
        log.info("查询题目列表: pageNum={}, pageSize={}, questionName={}, direction={}",
                queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getQuestionName(), queryDTO.getDirection());
        
        // 构建分页对象
        Page<Question> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        
        // 题目名称模糊查询
        if (StringUtils.hasText(queryDTO.getQuestionName())) {
            wrapper.like(Question::getQuestionName, queryDTO.getQuestionName());
        }
        
        // 技术方向精确查询
        if (StringUtils.hasText(queryDTO.getDirection())) {
            wrapper.eq(Question::getDirection, queryDTO.getDirection());
        }
        
        // 目标薪资查询（允许±5000误差）
        if (queryDTO.getTargetSalary() != null) {
            int minSalary = queryDTO.getTargetSalary() - 5000;
            int maxSalary = queryDTO.getTargetSalary() + 5000;
            wrapper.between(Question::getTargetSalary, minSalary, maxSalary);
        }
        
        // 只查询未删除的题目，按创建时间倒序
        wrapper.eq(Question::getIsDeleted, "0")
               .orderByDesc(Question::getCreateTime);
        
        // 执行查询
        Page<Question> questionPage = this.page(page, wrapper);
        
        // 转换为VO
        List<QuestionListVO> voList = questionPage.getRecords().stream()
                .map(question -> QuestionListVO.builder()
                        .id(question.getId())
                        .questionName(question.getQuestionName())
                        .questionDesc(question.getQuestionDesc())
                        .options(question.getOptions())
                        .targetSalary(question.getTargetSalary())
                        .direction(question.getDirection())
                        .analysis(question.getAnalysis())
                        .createTime(question.getCreateTime())
                        .updateTime(question.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
        
        // 构建返回结果
        Page<QuestionListVO> resultPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        resultPage.setRecords(voList);
        
        log.info("题目列表查询成功: total={}, current={}", resultPage.getTotal(), resultPage.getCurrent());
        return resultPage;
    }


}
