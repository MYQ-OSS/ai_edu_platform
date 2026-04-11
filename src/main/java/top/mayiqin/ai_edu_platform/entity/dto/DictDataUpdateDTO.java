package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员更新字典数据DTO
 * @author m'y'q
 */
@Data
@Schema(description = "更新字典数据请求DTO")
public class DictDataUpdateDTO {

    /**
     * 字典ID
     */
    @NotNull(message = "字典ID不能为空")
    @Schema(description = "字典ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典类型（如tech_direction、education、identity等）
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100位")
    @Schema(description = "字典类型", example = "tech_direction", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /**
     * 字典编码（唯一标识，如java_backend）
     */
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 50, message = "字典编码长度不能超过50位")
    @Schema(description = "字典编码", example = "java_backend", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictCode;

    /**
     * 字典名称（如Java后端开发）
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100位")
    @Schema(description = "字典名称", example = "Java后端开发", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    /**
     * 排序号（用于前端展示排序）
     */
    @NotNull(message = "排序号不能为空")
    @Schema(description = "排序号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    /**
     * 状态（0-正常 1-禁用）
     */
    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态（0-正常 1-禁用）", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
}
