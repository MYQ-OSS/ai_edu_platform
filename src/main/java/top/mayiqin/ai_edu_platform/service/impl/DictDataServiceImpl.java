package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mayiqin.ai_edu_platform.entity.po.DictData;
import top.mayiqin.ai_edu_platform.entity.vo.DictDataVO;
import top.mayiqin.ai_edu_platform.mapper.DictDataMapper;
import top.mayiqin.ai_edu_platform.service.DictDataService;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author m'y'q
* @description 针对表【t_dict_data(数据字典表（技术方向/学历/身份等基础配置）)】的数据库操作Service实现
* @createDate 2026-04-01 19:25:59
*/
@Service
@Slf4j
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData>
    implements DictDataService{

    @Override
    public List<DictDataVO> getTechDirections() {
        log.info("获取技术方向字典数据");
        
        // 查询技术方向字典数据，状态为正常（0），按排序号升序
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, "tech_direction")
               .eq(DictData::getStatus, "0")
               .orderByAsc(DictData::getSort);
        
        List<DictData> dictDataList = this.list(wrapper);
        
        // 转换为VO
        List<DictDataVO> result = dictDataList.stream()
                .map(dict -> DictDataVO.builder()
                        .dictCode(dict.getDictCode())
                        .dictName(dict.getDictName())
                        .build())
                .collect(Collectors.toList());
        
        log.info("技术方向字典数据获取成功: count={}", result.size());
        return result;
    }
}




