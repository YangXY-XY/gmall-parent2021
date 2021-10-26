package com.atguigu.gmall.item.service.Impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String,Object> result=new HashMap<>();
        SkuInfo skuInfo = productFeignClient.getAttrValueList(skuId);

        List<SpuSaleAttr> spuSaleAttrListCheckBySku =
                productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());

        Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());

        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);

        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        result.put("categoryView",categoryView);
        String s = JSON.toJSONString(skuValueIdsMap);
        result.put("valuesSkuJson",s);
        result.put("price",skuPrice);
        result.put("spuSaleAttrList",spuSaleAttrListCheckBySku);
        result.put("skuInfo",skuInfo);
        return result;
    }
}
