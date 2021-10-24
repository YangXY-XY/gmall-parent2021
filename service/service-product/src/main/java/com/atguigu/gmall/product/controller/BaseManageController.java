package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品基础属性接口")
@RestController
@RequestMapping("admin/product")
public class BaseManageController {
    @Autowired
    private ManageService manageService;

    @GetMapping("getCategory1")
    public Result<List<BaseCategory1>> getCategory1(){
        List<BaseCategory1> category1 = manageService.getCategory1();
        return Result.ok(category1);
    }
    @GetMapping("getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable("category1Id") Long BaseCategory1Id){
        List<BaseCategory2> category2 = manageService.getCategory2(BaseCategory1Id);
        return Result.ok(category2);
    }
    @GetMapping("getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable("category2Id") Long BaseCategory2Id){
        List<BaseCategory3> category3 = manageService.getCategory3(BaseCategory2Id);
        return Result.ok(category3);
    }


    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> getCategory1(@PathVariable("category1Id") Long category1Id,
                                                    @PathVariable("category2Id") Long category2Id,
                                                    @PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfo = manageService.getBaseAttrInfo(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfo);
    }

    @GetMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }
    @GetMapping("getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable("attrId") Long attrId){
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
        return Result.ok(attrValueList);
    }
    @GetMapping("/list/{page}/{limit}")
    public Result index(@PathVariable Long page,@PathVariable Long limit){
        Page<SkuInfo> pageParam=new Page<>(page,limit);
        IPage<SkuInfo> pageModel= manageService.getPage(pageParam);
        return Result.ok(pageModel);
    }
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        manageService.onSale(skuId);
        return Result.ok();
    }
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        manageService.cancelSale(skuId);
        return Result.ok();
    }

}
