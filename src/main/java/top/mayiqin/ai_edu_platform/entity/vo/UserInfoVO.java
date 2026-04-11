package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息响应DTO
 * 用于返回用户个人信息，不包含敏感字段
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户信息响应")
public class UserInfoVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 登录账号
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 用户身份（如学生/初级开发者等）
     */
    @Schema(description = "用户身份", example = "学生")
    private String identity;

    /**
     * 期望/当前薪资
     */
    @Schema(description = "期望/当前薪资", example = "15000")
    private Integer salary;

    /**
     * 项目/工作经历
     */
    @Schema(description = "项目/工作经历", example = "有2年Java开发经验")
    private String experience;

    /**
     * 答题数量
     */
    @Schema(description = "答题数量", example = "10")
    private Integer answerTimes;

    /**
     * 答题平均分
     */
    @Schema(description = "答题平均分", example = "85")
    private Integer averageScore;

    /**
     * 用户状态（0-正常 1-禁用）
     */
    @Schema(description = "用户状态", example = "0")
    private String status;

    /**
     * 用户角色（user-普通用户 admin-管理员）
     */
    @Schema(description = "用户角色", example = "admin")
    private String role;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2026-04-07 10:30:00")
    private Date createTime;
}
