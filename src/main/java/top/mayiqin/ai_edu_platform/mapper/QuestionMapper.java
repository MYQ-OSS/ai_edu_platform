package top.mayiqin.ai_edu_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.mayiqin.ai_edu_platform.entity.po.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author m'y'q
* @description 针对表【t_question(AI生成题库表)】的数据库操作Mapper
* @createDate 2026-03-31 20:55:09
* @Entity top.mayiqin.ai_edu_platform.entity.po.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询用户未回答过的题目
     *
     * @param userId 用户ID
     * @param direction 技术方向
     * @param targetSalary 目标薪资
     * @return 符合条件的题目，如果没有则返回null
     */
    @Select("SELECT q.* FROM t_question q " +
            "WHERE q.direction = #{direction} " +
            "AND ABS(q.target_salary - #{targetSalary}) <= 5000 " +
            "AND q.is_deleted = '0' " +
            "AND q.id NOT IN (SELECT question_id FROM t_quiz_record WHERE user_id = #{userId}) " +
            "ORDER BY q.create_time DESC " +
            "LIMIT 1")
    Question selectUnansweredQuestion(@Param("userId") Long userId,
                                      @Param("direction") String direction,
                                      @Param("targetSalary") Integer targetSalary);
}




