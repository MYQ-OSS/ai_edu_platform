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
 * 用户题目收藏表
 * @author m'y'q
 * @TableName t_quiz_collect
 */
@TableName(value ="t_quiz_collect")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class QuizCollect {
    /**
     * 收藏记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户唯一ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 题目唯一ID
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 是否收藏（'1'=收藏，'0'=取消）
     */
    @TableField(value = "is_collect")
    private String isCollect;

    /**
     * 收藏时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}
