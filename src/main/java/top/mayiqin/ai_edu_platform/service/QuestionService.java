package top.mayiqin.ai_edu_platform.service;

import top.mayiqin.ai_edu_platform.entity.po.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.dto.question.QuestionGenerateDTO;
import top.mayiqin.ai_edu_platform.vo.QuestionVO;

/**
* @author m'y'q
* @description 针对表【t_question(AI生成题库表)】的数据库操作Service
* @createDate 2026-03-31 20:55:09
*/
public interface QuestionService extends IService<Question> {

    QuestionVO generateQuestion(QuestionGenerateDTO request);
}
