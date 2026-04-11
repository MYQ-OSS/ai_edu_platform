package top.mayiqin.ai_edu_platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionAddDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionListQueryDTO;
import top.mayiqin.ai_edu_platform.entity.dto.QuestionUpdateDTO;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionListVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuestionVO;

/**
* @author m'y'q
* @description 针对表【t_question(AI生成题库表)】的数据库操作Service
* @createDate 2026-03-31 20:55:09
*/
public interface QuestionService extends IService<Question> {

    QuestionVO generateQuestion(QuestionGenerateDTO request);

    /**
     * 管理员手动新增题目
     * @param dto 题目信息
     * @return 题目ID
     */
    Long addQuestion(QuestionAddDTO dto);

    /**
     * 管理员编辑题目
     * @param dto 题目信息
     */
    void updateQuestion(QuestionUpdateDTO dto);

    /**
     * 管理员删除题目（逻辑删除）
     * @param questionId 题目ID
     */
    void deleteQuestion(Long questionId);

    /**
     * 分页查询题目列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<QuestionListVO> getQuestionList(QuestionListQueryDTO queryDTO);
}
