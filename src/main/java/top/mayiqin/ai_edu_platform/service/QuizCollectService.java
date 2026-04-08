package top.mayiqin.ai_edu_platform.service;

import top.mayiqin.ai_edu_platform.entity.po.QuizCollect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author m'y'q
 * @description 针对表【t_quiz_collect(用户题目收藏表)】的数据库操作Service
 */
public interface QuizCollectService extends IService<QuizCollect> {

    /**
     * 收藏/取消收藏题目
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param isCollect 是否收藏（true=收藏，false=取消）
     * @throws top.mayiqin.ai_edu_platform.exception.BusinessException 当题目不存在时抛出404异常
     */
    void collectQuiz(Long userId, Long questionId, Boolean isCollect);
}
