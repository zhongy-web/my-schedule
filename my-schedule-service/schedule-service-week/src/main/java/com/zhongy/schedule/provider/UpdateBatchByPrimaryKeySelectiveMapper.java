package com.zhongy.schedule.provider;

/**
 * 通用mapper没有自己的批量更新，所以得自定义
 */
import com.zhongy.schedule.provider.BatchExampleProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 批量update
 *
 * @param <T> 不能为空
 */
@tk.mybatis.mapper.annotation.RegisterMapper
public interface UpdateBatchByPrimaryKeySelectiveMapper<T> {

    /**
     * 根据Example条件批量更新实体`record`包含的不是null的属性值
     *
     * @return
     */
    @UpdateProvider(type = BatchExampleProvider.class, method = "dynamicSQL")
    int updateBatchByPrimaryKeySelective(List<? extends T> recordList);

}
