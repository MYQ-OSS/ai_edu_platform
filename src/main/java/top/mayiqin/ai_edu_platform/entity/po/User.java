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
 * @author m'y'q
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
     * 状态（0-正常 1-禁用）
     */
    @TableField(value = "status")
    private String status;

    /**
     * 角色（user-普通用户 admin-管理员）
     */
    @TableField(value = "role")
    private String role;

    /**
     * 注册时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 存储JWT令牌，仅用于前端返回
     * 数据库不存在此字段
     */
    @TableField(exist = false)
    private String token;
}