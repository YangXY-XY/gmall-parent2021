package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class SpuManageController {
    @Autowired
    private ManageService manageService;
    //获取spu列表，这里有含有了层级id
    @RequestMapping("{page}/{size}")
    public Result getSpuInfoPage(@PathVariable("page")Long page,
                                 @PathVariable("size")Long size,
                                 SpuInfo spuInfo){
        Page<SpuInfo> pageParam=new Page<>(page,size);
        IPage<SpuInfo> spuInfoPage = manageService.getSpuInfoPage(pageParam, spuInfo);
        return Result.ok(spuInfoPage);
    }
    //获取基本销售属性列表
    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }
    //在层级id下，添加spu
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }
    //添加sku中获取销售属性值列表
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrList= manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }
}
