package top.mayiqin.ai_edu_platform.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 后台管理用户列表 VO
 * @author m'y'q
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "后台管理用户列表VO")
public class UserListVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 用户身份
     */
    @Schema(description = "用户身份", example = "学生")
    private String identity;

    /**
     * 期望薪资
     */
    @Schema(description = "期望薪资", example = "15000")
    private Integer salary;

    /**
     * 答题次数
     */
    @Schema(description = "答题次数", example = "10")
    private Integer answerTimes;

    /**
     * 平均分数
     */
    @Schema(description = "平均分数", example = "85")
    private Integer averageScore;

    /**
     * 状态（0-正常 1-禁用）
     */
    @Schema(description = "状态（0-正常 1-禁用）", example = "0")
    private String status;

    /**
     * 角色（user/admin）
     */
    @Schema(description = "角色（user-普通用户 admin-管理员）", example = "user")
    private String role;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2024-04-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-04-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
