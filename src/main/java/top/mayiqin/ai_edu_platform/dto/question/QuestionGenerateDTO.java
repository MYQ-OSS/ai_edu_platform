package top.mayiqin.ai_edu_platform.dto.question;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionGenerateDTO {

    @NotBlank(message = "技术方向不能为空")
    @Size(min = 1, max = 100, message = "技术方向长度需在1-100位之间")
    private String direction;

    @NotNull(message = "期望薪资不能为空")
    @Positive(message = "期望薪资必须为正整数")
    private Integer targetSalary;

    @Size(max = 23, message = "用户身份长度不能超过23位")
    private String identity;

    @Size(max = 50, message = "就业城市长度不能超过50位")
    private String city;

    @Min(value = 1, message = "答题限时必须为正整数")
    private Integer timeLimit = 30;
}
