package top.mayiqin.ai_edu_platform.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典列表查询DTO
 * @author m'y'q
 */
@Data
@Schema(description = "字典列表查询请求DTO")
public class DictListQueryDTO {

    /**
     * 页码，默认1
     */
    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10
     */
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 500, message = "每页条数不能超过500")
    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    /**
     * 字典名称（模糊查询）
     */
    @Size(max = 100, message = "字典名称长度不能超过100位")
    @Schema(description = "字典名称（模糊查询）", example = "Java")
    private String dictName;
}
