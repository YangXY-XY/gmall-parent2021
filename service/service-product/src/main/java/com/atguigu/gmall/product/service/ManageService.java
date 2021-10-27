package com.atguigu.gmall.product.service;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    //--------------------item feign-------------------------
    //商品详情页，获取skuinfo和skuImages
    SkuInfo getSkuInfo(Long skuId);
    //商品详情页，获取skuinfo对应的分类信息，这里建立了视图
    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);
    //商品详情页，获取skuinfo对应的价格
    BigDecimal getSkuPrice(Long skuId);
    //商品详情页，获取spu销售属性和sku销售属性
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);
    //商品详情页，向前台提供销售属性和skuid键值json，以便可以进行跳转
    Map getSkuValueIdsMap(Long spuId);
    //首页，获取商品的层级信息
    List<JSONObject> getBaseCategoryList();
}
