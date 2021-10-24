package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;

    //获取品牌列表页
    @RequestMapping("{page}/{limit}")
    public Result getBaseTrademarkPage(@PathVariable("page") Long page,
                                       @PathVariable("limit") Long size){
        Page<BaseTrademark> pageParam=new Page<>(page,size);
        IPage<BaseTrademark> pageList = baseTrademarkService.getPage(pageParam);
        return Result.ok(pageList);
    }
    //按id获取品牌
    @RequestMapping("get/{id}")
    public Result get(@PathVariable("id") String id){
        BaseTrademark baseInfo = baseTrademarkService.getById(id);
        return Result.ok(baseInfo);
    }
    //保存品牌
    @RequestMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }
    //更新品牌
    @RequestMapping("update")
    public Result updateById(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }
    //按id删除品牌
    @RequestMapping("remove/{id}")
    public Result remove(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }
    //添加spu时，获取所有品牌信息
    @RequestMapping("getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> baseTrademarks=baseTrademarkService.list(null);
        return Result.ok(baseTrademarks);
    }

}
