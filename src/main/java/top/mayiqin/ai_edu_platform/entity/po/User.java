package top.mayiqin.ai_edu_platform.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户基础信息表
 * @TableName t_user
 */
@TableName(value ="t_user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {
    /**
     * 用户唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    @TableField(value = "username")
    private String username;

    /**
     * 加密后的密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户身份（如学生/初级开发者等）
     */
    @TableField(value = "identity")
    private String identity;

    /**
     * 期望/当前薪资
     */
    @TableField(value = "salary")
    private Integer salary;

    /**
     * 项目/工作经历
     */
    @TableField(value = "experience")
    private String experience;

    /**
     * 答题数量
     */
    @TableField(value = "answer_times")
    private Integer answerTimes;

    /**
     * 答题平均分
     */
    @TableField(value = "average_score")
    private Integer averageScore;

    /**
     * 注册时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}