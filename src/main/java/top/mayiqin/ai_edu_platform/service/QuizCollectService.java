package top.mayiqin.ai_edu_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.entity.po.QuizCollect;
import top.mayiqin.ai_edu_platform.entity.vo.QuizCollectVO;

import java.util.List;

/**
* @author m'y'q
* @description 针对表【t_quiz_collect(用户题目收藏表)】的数据库操作Service
* @createDate 2026-04-09
*/
public interface QuizCollectService extends IService<QuizCollect> {

    /**
     * 获取当前用户的收藏题目列表
     *
     * @return 收藏题目列表
     */
    List<QuizCollectVO> getCollectList();

    /**
     * 切换收藏状态
     * 如果已收藏则取消，如果未收藏则添加
     *
     * @param questionId 题目ID
     */
    void toggleCollect(Long questionId);
}
