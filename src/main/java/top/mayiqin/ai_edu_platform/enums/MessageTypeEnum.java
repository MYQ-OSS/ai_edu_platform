package top.mayiqin.ai_edu_platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author m'y'q
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    TEXT("TEXT", "普通文本"),
    ERROR("ERROR", "错误消息"),
    SYSTEM("SYSTEM", "系统消息");

    private final String code;
    private final String description;
}
