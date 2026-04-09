package top.mayiqin.ai_edu_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.vo.DictDataVO;
import top.mayiqin.ai_edu_platform.exception.Result;
import top.mayiqin.ai_edu_platform.service.DictDataService;

import java.util.List;

/**
 * 字典数据管理
 */
@RestController
@RequestMapping("/dict")
@Slf4j
@Tag(name = "字典数据管理")
public class DictController {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 获取技术方向字典数据
     *
     * @return 技术方向列表
     */
    @Operation(summary = "获取技术方向字典数据")
    @GetMapping("/tech-directions")
    public Result<List<DictDataVO>> getTechDirections() {
        log.info("获取技术方向字典数据请求");
        
        // 调用服务层获取技术方向字典数据
        List<DictDataVO> techDirections = dictDataService.getTechDirections();
        
        log.info("技术方向字典数据获取成功: count={}", techDirections.size());
        return Result.success(MessageConstant.GET_TECH_DIRECTIONS_SUCCESS, techDirections);
    }
}
