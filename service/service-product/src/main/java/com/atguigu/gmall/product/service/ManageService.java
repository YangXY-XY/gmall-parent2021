package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;

import java.util.List;

public interface ManageService {
    //获取一级菜单
    List<BaseCategory1> getCategory1();
    //获取三级菜单
    List<BaseCategory3> getCategory3(Long BaseCategory2Id);
    //获取二级菜单
    List<BaseCategory2> getCategory2(Long BaseCategory1Id);
    //根据菜单id，获取基本属性
    List<BaseAttrInfo> getBaseAttrInfo(Long BaseCategory1Id,Long BaseCategory2Id,Long BaseCategory3Id);
    //保存平台属性
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    //获取平台属性，为了修改平台属性
    BaseAttrInfo getAttrInfo(Long attrId);
}
