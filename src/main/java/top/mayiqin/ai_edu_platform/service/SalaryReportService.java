package top.mayiqin.ai_edu_platform.service;

import top.mayiqin.ai_edu_platform.entity.po.SalaryReport;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryEvaluateVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryHistoryVO;
import top.mayiqin.ai_edu_platform.entity.vo.SalaryReportVO;

/**
* @author m'y'q
* @description 针对表【t_salary_report(薪资评估报告表)】的数据库操作Service
* @createDate 2026-03-31 20:55:09
*/
public interface SalaryReportService extends IService<SalaryReport> {

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
     * @throws top.mayiqin.ai_edu_platform.exception.BusinessException 当AI分析失败时抛出500异常
     */
    SalaryEvaluateVO evaluateSalary(Long userId, String direction, String city, String experience, String education, String identity);

    /**
     * 获取薪资评估报告详情
     * 根据报告ID获取详细的薪资评估报告
     *
     * @param reportId 报告ID
     * @return 薪资评估报告详细VO
     * @throws top.mayiqin.ai_edu_platform.exception.BusinessException 当报告不存在时抛出404异常
     */
    SalaryReportVO getReportDetail(Long reportId);

    /**
     * 获取用户薪资评估历史记录
     * 查询指定用户的所有薪资评估报告（按时间倒序）
     *
     * @param userId 用户ID
     * @return 薪资评估历史记录列表VO
     */
    java.util.List<SalaryHistoryVO> getHistoryList(Long userId);
}
