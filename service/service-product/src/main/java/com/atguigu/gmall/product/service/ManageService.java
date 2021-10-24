package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
    //获取指定category3id的spuInfo
    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam,SpuInfo spuInfo);
    //查询销售属性数据
    List<BaseSaleAttr> getBaseSaleAttrList();
    //保存层级id下的spu
    void saveSpuInfo(SpuInfo spuInfo);
    //添加sku时获取图片
    List<SpuImage> getSpuImageList(Long spuId);
    //添加sku时获取spu销售属性值列表
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);
    //添加sku
    void saveSkuInfo(SkuInfo skuInfo);
    //获取sku列表
    IPage<SkuInfo> getPage(Page<SkuInfo> pageParam);
    //商品上架
    void onSale(Long skuId);
    //商品下架
    void cancelSale(Long skuId);
}
