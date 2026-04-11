package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mayiqin.ai_edu_platform.ai.tool.SalaryEvaluateTool;
import top.mayiqin.ai_edu_platform.entity.po.SalaryReport;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryEvaluateVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryHistoryVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryReportVO;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.SalaryReportMapper;
import top.mayiqin.ai_edu_platform.service.SalaryReportService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author m'y'q
* @description 针对表【t_salary_report(薪资评估报告表)】的数据库操作Service实现
* @createDate 2026-03-31 20:55:09
*/
@Service
@Slf4j
public class SalaryReportServiceImpl extends ServiceImpl<SalaryReportMapper, SalaryReport>
    implements SalaryReportService{

    private final SalaryEvaluateTool salaryEvaluateTool;

    public SalaryReportServiceImpl(ChatClient chatClient, SalaryEvaluateTool salaryEvaluateTool) {
        this.salaryEvaluateTool = salaryEvaluateTool;
    }

    /**
     * 提交薪资评估请求
     * 调用AI进行薪资分析，生成评估报告
     *
     * @param userId 用户ID
     * @param direction 技术方向
     * @param city 目标城市
     * @param experience 项目/工作经历
     * @param education 学历
     * @param identity 身份
     * @return 薪资评估报告VO（包含reportId、salaryRange、aiSuggestion、createTime）
     * @throws BusinessException 当AI分析失败时抛出500异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SalaryEvaluateVO evaluateSalary(Long userId, String direction, String city, String experience, String education, String identity) {
        log.info("开始薪资评估请求: userId={}, direction={}, city={}", userId, direction, city);

        try {
            // b) 调用AI进行薪资评估
            log.info("开始调用AI进行薪资评估...");
            String aiResponse = salaryEvaluateTool.evaluateSalary(direction, city, experience, education, identity);
            log.debug("AI返回的原始JSON: {}", aiResponse);

            // c) 解析AI返回结果
            var jsonNode = salaryEvaluateTool.validateAndParseJson(aiResponse);
            String salaryRange = jsonNode.get("salaryRange").asText();
            String aiSuggestion = jsonNode.get("aiSuggestion").asText();

            log.info("AI薪资评估完成: salaryRange={}", salaryRange);

            // d) 构建SalaryReport对象
            SalaryReport salaryReport = SalaryReport.builder()
                    .userId(userId)
                    .direction(direction)
                    .city(city)
                    .experience(experience)
                    .salaryRange(salaryRange)
                    .aiSuggestion(aiSuggestion)
                    .createTime(new Date())
                    .build();

            // e) 保存到数据库
            boolean saved = this.save(salaryReport);
            if (!saved) {
                log.error("薪资报告保存失败: userId={}, direction={}", userId, direction);
                throw new BusinessException(500, MessageConstant.SAVE_SALARY_REPORT_FAILED);
            }

            log.info("薪资报告保存成功: reportId={}", salaryReport.getId());

            // f) 构建返回VO
            SalaryEvaluateVO result = SalaryEvaluateVO.builder()
                    .reportId(salaryReport.getId())
                    .salaryRange(salaryRange)
                    .aiSuggestion(aiSuggestion)
                    .createTime(formatDate(salaryReport.getCreateTime()))
                    .build();

            log.info("薪资评估报告生成成功: reportId={}, salaryRange={}", result.getReportId(), result.getSalaryRange());

            return result;

        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("AI薪资评估失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI薪资评估失败: " + e.getMessage());
        }
    }

    /**
     * 获取薪资评估报告详情
     * 根据报告ID获取详细的薪资评估报告
     *
     * @param reportId 报告ID
     * @return 薪资评估报告详细VO
     */
    @Override
    public SalaryReportVO getReportDetail(Long reportId) {
        log.info("获取薪资评估报告详情: reportId={}", reportId);
        
        // 1. 查询报告记录
        SalaryReport salaryReport = this.getById(reportId);
        if (salaryReport == null) {
            log.error("薪资评估报告不存在: reportId={}", reportId);
            throw new BusinessException(404, MessageConstant.REPORT_NOT_EXIST);
        }
        
        log.debug("查询到报告记录: userId={}, direction={}, salaryRange={}", 
                salaryReport.getUserId(), salaryReport.getDirection(), salaryReport.getSalaryRange());
        
        // 2. 构建返回VO
        SalaryReportVO reportVO = SalaryReportVO.builder()
                .id(salaryReport.getId())
                .userId(salaryReport.getUserId())
                .direction(salaryReport.getDirection())
                .city(salaryReport.getCity())
                .experience(salaryReport.getExperience())
                .salaryRange(salaryReport.getSalaryRange())
                .aiSuggestion(salaryReport.getAiSuggestion())
                .createTime(formatDate(salaryReport.getCreateTime()))
                .build();
        
        log.info("薪资评估报告获取成功: reportId={}, salaryRange={}", reportVO.getId(), reportVO.getSalaryRange());
        
        return reportVO;
    }

    /**
     * 获取用户薪资评估历史记录
     * 查询指定用户的所有薪资评估报告（按时间倒序）
     *
     * @param userId 用户ID
     * @return 薪资评估历史记录列表VO
     */
    @Override
    public List<SalaryHistoryVO> getHistoryList(Long userId) {
        log.info("获取用户薪资评估历史记录: userId={}", userId);
        
        // 1. 构建查询条件：按用户ID查询，按创建时间倒序
        LambdaQueryWrapper<SalaryReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryReport::getUserId, userId)
                    .orderByDesc(SalaryReport::getCreateTime);
        
        // 2. 查询数据库
        List<SalaryReport> reportList = this.list(queryWrapper);
        
        if (reportList == null || reportList.isEmpty()) {
            log.debug("用户暂无薪资评估记录: userId={}", userId);
            return new ArrayList<>();
        }
        
        log.debug("查询到{}条薪资评估记录: userId={}", reportList.size(), userId);
        
        // 3. 转换为VO列表
        List<SalaryHistoryVO> historyList = reportList.stream()
                .map(report -> SalaryHistoryVO.builder()
                        .id(report.getId())
                        .salaryRange(report.getSalaryRange())
                        .createTime(formatDate(report.getCreateTime()))
                        .build())
                .collect(Collectors.toList());
        
        log.info("薪资评估历史记录获取成功: userId={}, count={}", userId, historyList.size());
        
        return historyList;
    }

    /**
     * 格式化日期为字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串（yyyy-MM-dd HH:mm:ss）
     */
    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}




