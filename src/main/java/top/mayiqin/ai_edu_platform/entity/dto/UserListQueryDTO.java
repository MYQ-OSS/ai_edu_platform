package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户列表查询请求 DTO
 * @author m'y'q
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户列表查询请求DTO")
public class UserListQueryDTO {

    /**
     * 页码，默认1，最小值1
     */
    @Schema(description = "页码", example = "1", defaultValue = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10，范围1-500
     */
    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 500, message = "每页条数不能超过500")
    private Integer pageSize = 10;

    /**
     * 用户名模糊查询（可选）
     */
    @Schema(description = "用户名（模糊查询）", example = "zhang")
    private String username;

    /**
     * 用户状态筛选（可选，0或1）
     */
    @Schema(description = "用户状态（0-正常 1-禁用）", example = "0", allowableValues = {"0", "1"})
    private String status;

    /**
     * 用户角色筛选（可选，user或admin）
     */
    @Schema(description = "用户角色（user-普通用户 admin-管理员）", example = "user", allowableValues = {"user", "admin"})
    private String role;
}
