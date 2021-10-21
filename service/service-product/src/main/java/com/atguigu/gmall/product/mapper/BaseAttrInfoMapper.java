package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<BaseAttrInfo> selectBaseAttrInfoList(@Param("baseCategory1Id") Long baseCategory1Id,
                                              @Param("baseCategory2Id") Long baseCategory2Id,
                                              @Param("baseCategory3Id") Long baseCategory3Id);
}
