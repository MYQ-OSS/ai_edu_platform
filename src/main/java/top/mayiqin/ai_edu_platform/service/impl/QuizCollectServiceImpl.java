package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.entity.po.QuizCollect;
import top.mayiqin.ai_edu_platform.entity.vo.QuizCollectVO;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import top.mayiqin.ai_edu_platform.mapper.QuizCollectMapper;
import top.mayiqin.ai_edu_platform.service.QuizCollectService;
import top.mayiqin.ai_edu_platform.utils.UserContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author m'y'q
* @description 针对表【t_quiz_collect(用户题目收藏表)】的数据库操作Service实现
* @createDate 2026-04-09
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
     * 获取当前用户的收藏题目列表
     *
     * @return 收藏题目列表
     */
    @Override
    public List<QuizCollectVO> getCollectList() {
        // 1. 获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }

        log.info("获取用户收藏列表: userId={}", userId);

        // 2. 查询用户的收藏记录（is_collect='1'）
        LambdaQueryWrapper<QuizCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuizCollect::getUserId, userId)
               .eq(QuizCollect::getIsCollect, "1")
               .orderByDesc(QuizCollect::getCreateTime);
        List<QuizCollect> collectList = this.list(wrapper);

        // 3. 关联查询题目信息并转换为VO
        List<QuizCollectVO> result = collectList.stream().map(collect -> {
            // 查询题目信息
            Question question = questionMapper.selectById(collect.getQuestionId());
            if (question == null) {
                log.warn("题目不存在: questionId={}", collect.getQuestionId());
                return null;
            }

            // 构建VO
            return QuizCollectVO.builder()
                    .recordId(collect.getId())
                    .questionId(question.getId())
                    .questionName(question.getQuestionName())
                    .questionDesc(question.getQuestionDesc())
                    .direction(question.getDirection())
                    .targetSalary(question.getTargetSalary())
                    .options(question.getOptions())
                    .collectTime(formatDate(collect.getCreateTime()))
                    .build();
        }).filter(vo -> vo != null).collect(Collectors.toList());

        log.info("获取收藏列表成功: userId={}, count={}", userId, result.size());

        return result;
    }

    /**
     * 切换收藏状态
     * 如果已收藏则取消，如果未收藏则添加
     *
     * @param questionId 题目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleCollect(Long questionId) {
        // 1. 获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, MessageConstant.USER_NOT_LOGIN);
        }

        // 2. 验证题目是否存在
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            log.error("题目不存在: questionId={}", questionId);
            throw new BusinessException(404, MessageConstant.QUESTION_NOT_EXIST);
        }

        log.info("切换收藏状态: userId={}, questionId={}", userId, questionId);

        // 3. 查询是否已有收藏记录
        LambdaQueryWrapper<QuizCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuizCollect::getUserId, userId)
               .eq(QuizCollect::getQuestionId, questionId);
        QuizCollect existCollect = this.getOne(wrapper);

        if (existCollect != null) {
            // 4. 已有记录，切换状态
            String newStatus = "1".equals(existCollect.getIsCollect()) ? "0" : "1";
            existCollect.setIsCollect(newStatus);
            existCollect.setUpdateTime(new Date());
            this.updateById(existCollect);
            log.info("更新收藏状态: recordId={}, newStatus={}", existCollect.getId(), newStatus);
        } else {
            // 5. 无记录，新增收藏
            QuizCollect newCollect = QuizCollect.builder()
                    .userId(userId)
                    .questionId(questionId)
                    .isCollect("1")
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            this.save(newCollect);
            log.info("新增收藏记录: recordId={}", newCollect.getId());
        }
    }

    /**
     * 格式化日期为字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串（yyyy-MM-dd HH:mm:ss）
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
