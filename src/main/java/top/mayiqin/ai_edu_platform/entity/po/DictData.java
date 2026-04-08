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
 * 数据字典表（技术方向/学历/身份等基础配置）
 * @author m'y'q
 * @TableName t_dict_data
 */
@TableName(value ="t_dict_data")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DictData {
    /**
     * 字典数据主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型（技术方向固定为tech_direction）
     */
    @TableField(value = "dict_type")
    private String dictType;

    /**
     * 字典编码（唯一标识，如java_backend）
     */
    @TableField(value = "dict_code")
    private String dictCode;

    /**
     * 字典名称（如Java后端开发）
     */
    @TableField(value = "dict_name")
    private String dictName;

    /**
     * 排序号（用于前端展示排序）
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 状态（0-正常 1-禁用）
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}