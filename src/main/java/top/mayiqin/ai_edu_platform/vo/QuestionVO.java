package top.mayiqin.ai_edu_platform.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionVO {

    private Long questionId;

    private String questionName;

    private String questionDesc;

    private String options;

    private Integer targetSalary;

    private String direction;
}
