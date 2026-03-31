package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import top.mayiqin.ai_edu_platform.service.QuestionService;
import top.mayiqin.ai_edu_platform.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author m'y'q
* @description 针对表【t_question(AI生成题库表)】的数据库操作Service实现
* @createDate 2026-03-31 20:55:08
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




