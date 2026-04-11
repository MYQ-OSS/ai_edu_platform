package top.mayiqin.ai_edu_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mayiqin.ai_edu_platform.constant.MessageConstant;
import top.mayiqin.ai_edu_platform.entity.dto.DictDataAddDTO;
import top.mayiqin.ai_edu_platform.entity.dto.DictDataUpdateDTO;
import top.mayiqin.ai_edu_platform.entity.dto.DictListQueryDTO;
import top.mayiqin.ai_edu_platform.entity.po.DictData;
import top.mayiqin.ai_edu_platform.entity.vo.DictDataVO;
import top.mayiqin.ai_edu_platform.exception.BusinessException;
import top.mayiqin.ai_edu_platform.mapper.DictDataMapper;
import top.mayiqin.ai_edu_platform.service.DictDataService;

import java.util.Date;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDictData(DictDataAddDTO dto) {
        log.info("管理员新增字典数据: dictType={}, dictCode={}, dictName={}", 
                dto.getDictType(), dto.getDictCode(), dto.getDictName());
        
        // 检查字典编码是否已存在（同一类型下唯一）
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dto.getDictType())
               .eq(DictData::getDictCode, dto.getDictCode());
        
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(400, "字典编码已存在：" + dto.getDictCode());
        }
        
        // 构建字典数据实体
        DictData dictData = DictData.builder()
                .dictType(dto.getDictType())
                .dictCode(dto.getDictCode())
                .dictName(dto.getDictName())
                .sort(dto.getSort())
                .status(dto.getStatus() != null ? dto.getStatus() : "0")
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        
        // 保存到数据库
        boolean saved = this.save(dictData);
        if (!saved || dictData.getId() == null) {
            log.error("字典数据保存失败: {}", dictData);
            throw new BusinessException(500, MessageConstant.SAVE_FAILED);
        }
        
        log.info("字典数据新增成功: id={}, dictCode={}", dictData.getId(), dictData.getDictCode());
        return dictData.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(DictDataUpdateDTO dto) {
        log.info("管理员更新字典数据: id={}, dictType={}, dictCode={}, dictName={}", 
                dto.getId(), dto.getDictType(), dto.getDictCode(), dto.getDictName());
        
        // 检查字典是否存在
        DictData existDict = this.getById(dto.getId());
        if (existDict == null) {
            throw new BusinessException(404, "字典数据不存在");
        }
        
        // 检查字典编码是否已被其他记录使用（同一类型下唯一）
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dto.getDictType())
               .eq(DictData::getDictCode, dto.getDictCode())
               .ne(DictData::getId, dto.getId());
        
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(400, "字典编码已存在：" + dto.getDictCode());
        }
        
        // 构建更新实体
        DictData updateDict = DictData.builder()
                .id(dto.getId())
                .dictType(dto.getDictType())
                .dictCode(dto.getDictCode())
                .dictName(dto.getDictName())
                .sort(dto.getSort())
                .status(dto.getStatus())
                .updateTime(new Date())
                .build();
        
        // 执行更新
        boolean updated = this.updateById(updateDict);
        if (!updated) {
            log.error("字典数据更新失败: {}", updateDict);
            throw new BusinessException(500, MessageConstant.OPERATION_FAILED);
        }
        
        log.info("字典数据更新成功: id={}, dictCode={}", updateDict.getId(), updateDict.getDictCode());
    }

    @Override
    public Page<DictData> getDictList(DictListQueryDTO queryDTO) {
        log.info("查询字典列表: pageNum={}, pageSize={}, dictName={}",
                queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getDictName());
        
        Page<DictData> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        
        // 固定查询技术方向类型
        wrapper.eq(DictData::getDictType, "tech_direction");
        
        // 字典名称模糊查询
        if (queryDTO.getDictName() != null && !queryDTO.getDictName().isEmpty()) {
            wrapper.like(DictData::getDictName, queryDTO.getDictName());
        }
        
        // 按排序号升序
        wrapper.orderByAsc(DictData::getSort);
        
        Page<DictData> resultPage = this.page(page, wrapper);
        
        log.info("字典列表查询成功: total={}", resultPage.getTotal());
        return resultPage;
    }
}
