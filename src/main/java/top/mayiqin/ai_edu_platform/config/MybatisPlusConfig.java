package top.mayiqin.ai_edu_platform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Plus 配置类
 * 配置分页插件、逻辑删除和 Mapper 扫描路径
 * @author m'y'q
 */
@Configuration
@EnableTransactionManagement
@MapperScan("top.mayiqin.ai_edu_platform.mapper")
public class MybatisPlusConfig {

    /**
     * 添加分页插件和乐观锁插件
     * 配置分页拦截器，设置数据库类型为 MySQL，并限制单页最大记录数为 500
     * 防止恶意请求导致性能问题或内存溢出
     *
     * @return MybatisPlusInterceptor 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 创建分页拦截器，指定数据库类型为 MySQL
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        
        // 设置单页最大记录数限制，防止查询过多数据导致性能问题
        paginationInterceptor.setMaxLimit(500L);
        
        // 设置溢出总页数处理策略：true=返回第一页，false=返回空（默认）
        paginationInterceptor.setOverflow(false);
        
        // 添加分页拦截器到链中
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 添加乐观锁插件（可选）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        return interceptor;
    }
}