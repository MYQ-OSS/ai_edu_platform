package top.mayiqin.ai_edu_platform.service;

import top.mayiqin.ai_edu_platform.entity.po.QuizRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.entity.vo.QuizReportVO;
import top.mayiqin.ai_edu_platform.entity.vo.QuizSubmitVO;

/**
* @author m'y'q
* @description 针对表【t_quiz_record(用户答题记录表)】的数据库操作Service
* @createDate 2026-03-31 20:55:09
*/
public interface QuizRecordService extends IService<QuizRecord> {

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
    QuizSubmitVO submitQuiz(Long userId, Long questionId, String userOptions, String userAnswer);

    /**
     * 获取答题报告
     * 根据答题记录ID获取详细答题报告
     *
     * @param recordId 答题记录ID
     * @return 答题报告VO（包含完整的答题信息和AI判分结果）
     * @throws top.mayiqin.ai_edu_platform.exception.BusinessException 当记录不存在时抛出404异常
     */
    QuizReportVO getQuizReport(Long recordId);
}
