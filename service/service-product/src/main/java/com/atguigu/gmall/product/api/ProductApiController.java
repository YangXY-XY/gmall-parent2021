package com.atguigu.gmall.product.api;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {
    @Autowired
    private ManageService manageService;

    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        return skuInfo;
    }
    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView categoryViewByCategory3Id = manageService.getCategoryViewByCategory3Id(category3Id);
        return categoryViewByCategory3Id;
    }
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal skuPrice = manageService.getSkuPrice(skuId);
        return skuPrice;
    }
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId")Long skuId,
                                                          @PathVariable("spuId")Long spuId){
        List<SpuSaleAttr> spuSaleAttrs= manageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrs;
    }
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        Map skuValueIdsMap = manageService.getSkuValueIdsMap(spuId);
        return skuValueIdsMap;
    }
}
