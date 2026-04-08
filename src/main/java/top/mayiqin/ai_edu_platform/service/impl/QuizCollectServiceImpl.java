package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.entity.po.QuizCollect;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.mapper.QuizCollectMapper;
import top.mayiqin.ai_edu_platform.service.QuizCollectService;

import java.util.Date;

/**
 * @author m'y'q
 * @description 针对表【t_quiz_collect(用户题目收藏表)】的数据库操作Service实现
 */
@Service
@Slf4j
public class QuizCollectServiceImpl extends ServiceImpl<QuizCollectMapper, QuizCollect>
        implements QuizCollectService {

    private final QuestionMapper questionMapper;

    public QuizCollectServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    /**
     * 收藏/取消收藏题目
     *
     * @param userId      用户ID
     * @param questionId  题目ID
     * @param isCollect   是否收藏（true=收藏，false=取消）
     * @throws BusinessException 当题目不存在时抛出404异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectQuiz(Long userId, Long questionId, Boolean isCollect) {
        log.info("开始处理收藏请求: userId={}, questionId={}, isCollect={}", userId, questionId, isCollect);

        try {
            // a) 验证题目是否存在
            Question question = questionMapper.selectById(questionId);
            if (question == null) {
                log.error("题目不存在: questionId={}", questionId);
                throw new BusinessException(404, "题目不存在");
            }
            log.debug("题目验证通过: questionName={}", question.getQuestionName());

            // b) 查询是否已有收藏记录
            LambdaQueryWrapper<QuizCollect> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(QuizCollect::getUserId, userId)
                    .eq(QuizCollect::getQuestionId, questionId);
            QuizCollect existingRecord = this.getOne(queryWrapper);

            if (existingRecord != null) {
                // 存在记录，更新状态
                String newStatus = isCollect ? "1" : "0";
                existingRecord.setIsCollect(newStatus);
                existingRecord.setUpdateTime(new Date());
                
                boolean updated = this.updateById(existingRecord);
                if (!updated) {
                    log.error("更新收藏状态失败: userId={}, questionId={}", userId, questionId);
                    throw new BusinessException(500, "更新收藏状态失败");
                }
                
                log.info("更新收藏状态成功: userId={}, questionId={}, isCollect={}", userId, questionId, isCollect);
            } else {
                if (isCollect) {
                    // 不存在记录且是收藏操作，新增记录
                    QuizCollect newRecord = QuizCollect.builder()
                            .userId(userId)
                            .questionId(questionId)
                            .isCollect("1")
                            .createTime(new Date())
                            .updateTime(new Date())
                            .build();
                    
                    boolean saved = this.save(newRecord);
                    if (!saved) {
                        log.error("新增收藏记录失败: userId={}, questionId={}", userId, questionId);
                        throw new BusinessException(500, "新增收藏记录失败");
                    }
                    
                    log.info("新增收藏成功: userId={}, questionId={}", userId, questionId);
                } else {
                    // 不存在记录且是取消操作，无需处理
                    log.debug("未找到收藏记录，无需取消: userId={}, questionId={}", userId, questionId);
                }
            }

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("处理收藏请求失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "收藏操作失败: " + e.getMessage());
        }
    }
}
