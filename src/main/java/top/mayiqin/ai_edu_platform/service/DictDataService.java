package top.mayiqin.ai_edu_platform.service;

import top.mayiqin.ai_edu_platform.entity.po.DictData;
import com.baomidou.mybatisplus.extension.service.IService;
import top.mayiqin.ai_edu_platform.entity.vo.DictDataVO;

import java.util.List;

/**
* @author m'y'q
* @description 针对表【t_dict_data(数据字典表（技术方向/学历/身份等基础配置）)】的数据库操作Service
* @createDate 2026-04-01 19:25:59
*/
public interface DictDataService extends IService<DictData> {

    /**
     * 获取技术方向字典数据
     * @return 技术方向列表
     */
    List<DictDataVO> getTechDirections();
}
