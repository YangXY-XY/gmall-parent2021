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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String,Object> result=new HashMap<>();

        CompletableFuture<SkuInfo> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getAttrValueList(skuId);
            result.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrListCheckBySku =
                    productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
        }, threadPoolExecutor);

        CompletableFuture<Void> skuValueIdsMapCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfo -> {
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String s = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", s);
        }, threadPoolExecutor);

        CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            result.put("price", skuPrice);
        }, threadPoolExecutor);

        CompletableFuture<Void> categoryViewCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfo -> {
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
        }, threadPoolExecutor);

        CompletableFuture.allOf(skuCompletableFuture,spuSaleAttrCompletableFuture,
                skuValueIdsMapCompletableFuture,skuPriceCompletableFuture,categoryViewCompletableFuture).join();

        return result;
    }
}
