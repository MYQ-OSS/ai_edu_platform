package top.mayiqin.ai_edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.mayiqin.ai_edu_platform.entity.dto.SalaryEvaluateDTO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryEvaluateVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryHistoryVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryReportVO;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.SalaryReportService;

import java.util.List;

/**
 * 薪资管理控制器
 * 提供AI薪资评估相关接口
 * @author m'y'q
 */
@RestController
@Slf4j
@RequestMapping("/salary")
@Tag(name = "薪资管理", description = "AI智能薪资评估相关接口")
public class SalaryController {

    private final SalaryReportService salaryReportService;

    public SalaryController(SalaryReportService salaryReportService) {
        this.salaryReportService = salaryReportService;
    }

    /**
     * 薪资评估
     * 用户提交技术栈、项目经历等数据，AI生成薪资评估报告
     *
     * @param request 薪资评估请求参数（包含userId、direction、city、experience、education、identity）
     * @return 薪资评估报告
     */
    @Operation(
        summary = "薪资评估",
        description = "用户提交技术栈、项目经历等数据，AI生成薪资评估报告。需要携带有效的JWT Token进行身份验证。"
    )
    @PostMapping("/evaluate")
    public Result<SalaryEvaluateVO> evaluateSalary(@Valid @RequestBody SalaryEvaluateDTO request) {
        log.info("薪资评估请求: userId={}, direction={}, city={}", 
                request.getUserId(), request.getDirection(), request.getCity());
        
        // 调用服务层进行薪资评估
        SalaryEvaluateVO data = salaryReportService.evaluateSalary(
                request.getUserId(),
                request.getDirection(),
                request.getCity(),
                request.getExperience(),
                request.getEducation(),
                request.getIdentity()
        );
        
        log.info("薪资评估报告生成成功: reportId={}, salaryRange={}", 
                data.getReportId(), data.getSalaryRange());
        
        return Result.success(MessageConstant.SALARY_EVALUATE_SUCCESS, data);
    }

    /**
     * 获取薪资评估报告详情
     * 根据报告ID获取详细的薪资评估报告
     *
     * @param reportId 报告ID（路径参数）
     * @return 薪资评估报告详细信息
     */
    @Operation(
        summary = "获取薪资评估报告详情",
        description = "根据报告ID获取详细的薪资评估报告。需要携带有效的JWT Token进行身份验证。"
    )
    @GetMapping("/report/{reportId}")
    public Result<SalaryReportVO> getReportDetail(@PathVariable Long reportId) {
        log.info("获取薪资评估报告请求: reportId={}", reportId);
        
        // 调用服务层获取报告详情
        SalaryReportVO data = salaryReportService.getReportDetail(reportId);
        
        log.info("薪资评估报告获取成功: reportId={}, salaryRange={}", data.getId(), data.getSalaryRange());
        return Result.success(MessageConstant.GET_SALARY_REPORT_SUCCESS, data);
    }

    /**
     * 获取薪资评估历史记录
     * 获取当前用户的所有薪资评估历史记录
     *
     * @param userId 用户ID（从Token中获取或作为请求参数）
     * @return 薪资评估历史记录列表
     */
    @Operation(
        summary = "获取薪资评估历史记录",
        description = "获取用户的所有薪资评估历史记录，按时间倒序排列。需要携带有效的JWT Token进行身份验证。"
    )
    @GetMapping("/history")
    public Result<List<SalaryHistoryVO>> getHistoryList(@RequestParam Long userId) {
        log.info("获取薪资评估历史记录请求: userId={}", userId);
        
        // 调用服务层获取历史记录
        List<SalaryHistoryVO> data = salaryReportService.getHistoryList(userId);
        
        log.info("薪资评估历史记录获取成功: userId={}, count={}", userId, data.size());
        return Result.success(MessageConstant.GET_SALARY_HISTORY_SUCCESS, data);
    }
}
