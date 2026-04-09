package top.mayiqin.ai_edu_platform.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典数据响应VO
 * 用于返回字典数据给前端下拉框展示
 * @author m'y'q
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "字典数据响应")
public class DictDataVO {
    
    /**
     * 字典编码（唯一标识，如java_backend）
     */
    @Schema(description = "字典编码", example = "java_backend")
    private String dictCode;
    
    /**
     * 字典名称（如Java后端开发）
     */
    @Schema(description = "字典名称", example = "Java后端开发")
    private String dictName;
}
