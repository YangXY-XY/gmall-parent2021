package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long BaseCategory2Id) {
        QueryWrapper queryWrapper=new QueryWrapper<BaseCategory3>();
        queryWrapper.eq("category2_id",BaseCategory2Id);
        return baseCategory3Mapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long BaseCategory1Id) {
        QueryWrapper queryWrapper=new QueryWrapper<BaseCategory2>();
        queryWrapper.eq("category1_id",BaseCategory1Id);
        return baseCategory2Mapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long BaseCategory1Id, Long BaseCategory2Id, Long BaseCategory3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(BaseCategory1Id,BaseCategory2Id,BaseCategory3Id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //更新或是删除
        if(baseAttrInfo.getId()!=null){
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }else{
            baseAttrInfoMapper.insert(baseAttrInfo);
        }
        //先删除属性值列表
        QueryWrapper<BaseAttrValue> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("attr_id",baseAttrInfo.getId());
        baseAttrValueMapper.delete(queryWrapper);
        //添加属性值列表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if(attrValueList!=null  &&  attrValueList.size()>0){
            for(BaseAttrValue baseAttrValue:attrValueList){
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }


    }

    @Override
    public BaseAttrInfo getAttrInfo(Long attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        //获取对应的属性列表
        baseAttrInfo.setAttrValueList(getAttrValueList(attrId));
        return baseAttrInfo;
    }

    private List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper queryWrapper=new QueryWrapper<BaseAttrValue>();
        queryWrapper.eq("attr_id",attrId);
        return baseAttrValueMapper.selectList(queryWrapper);
    }
}
