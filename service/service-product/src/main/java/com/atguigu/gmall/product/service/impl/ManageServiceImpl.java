package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

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

    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfo spuInfo) {
        QueryWrapper queryWrapper=new QueryWrapper<SpuInfo>();
        queryWrapper.eq("category3_id",spuInfo.getCategory3Id());
        queryWrapper.orderByDesc("id");
        return spuInfoMapper.selectPage(pageParam,queryWrapper);

    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrs;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(spuImageList !=null && spuImageList.size()>0){
            for(SpuImage spuImage:spuImageList){
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(spuSaleAttrList !=null && spuSaleAttrList.size()>0){
            for(SpuSaleAttr spuSaleAttr:spuSaleAttrList){
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(spuSaleAttrValueList !=null && spuSaleAttrValueList.size()>0){
                    for(SpuSaleAttrValue spuSaleAttrValue:spuSaleAttrValueList){
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }

    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(queryWrapper);
        return spuImageList;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(skuImageList!=null && skuImageList.size()>0){
            for(SkuImage skuImage:skuImageList){
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(skuAttrValueList!=null && skuAttrValueList.size()>0){
            for(SkuAttrValue skuAttrValue:skuAttrValueList){
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(skuSaleAttrValueList!=null &&skuSaleAttrValueList.size()>0){
            for(SkuSaleAttrValue skuSaleAttrValue:skuSaleAttrValueList){
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    @Override
    public IPage<SkuInfo> getPage(Page<SkuInfo> pageParam) {
        QueryWrapper<SkuInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(pageParam, queryWrapper);
        return skuInfoIPage;
    }

    @Transactional
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }
    @Transactional
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);

    }

    //获取商品详情页skuInfo信息
    @GmallCache(prefix = RedisConst.SKUKEY_PREFIX)
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return getSkuInfoDB(skuId);
        //使用redis作分布式锁
        //return getSkuInfoRedis(skuId);
        //使用redisson作分布式锁
        //return getSkuInfoRedisson(skuId);
    }
    //使用redis作分布式锁
    private SkuInfo getSkuInfoRedis(Long skuId){
        SkuInfo skuInfo=null;
        try {
            //首先查找缓存
            String skuKey= RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            skuInfo =(SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //如果缓存为空
            if(skuInfo==null){
                //获取分布式锁，避免缓存击穿
                String lockKey=RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                String uuid= UUID.randomUUID().toString().replace("-","");
                Boolean isExist = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, uuid, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                //拿到了分布式锁
                if(isExist){
                    //查取数据库
                    skuInfo=getSkuInfoDB(skuId);
                    //如果数据库为空，在缓存中放入null值，避免穿透
                    if(skuInfo==null){
                        SkuInfo skuInfo1=new SkuInfo();
                        redisTemplate.opsForValue()
                                .set(skuKey,skuInfo1,RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                        return skuInfo1;
                    }
                    //数据库不为空，放入缓存中
                    redisTemplate.opsForValue()
                            .set(skuKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                    //使用lua脚本解除分布式锁，保证原子性
                    String script="if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    redisScript.setScriptText(script);
                    redisScript.setResultType(Long.class);
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey),uuid);
                    //返回查到的值
                    return skuInfo;
                }else {
                    //没有拿到分布式锁，等待1秒，继续取值
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            }else {
                //缓存不为空，直接返回查到的值
                return skuInfo;
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //避免缓存宕机了，最后可以查取数据库
        return getSkuInfoDB(skuId);
    }
    //使用redisson作分布式锁
    private SkuInfo getSkuInfoRedisson(Long skuId){
        SkuInfo skuInfo=null;
        try {
            //首先，在缓存中获取
            String skuKey=RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //缓存中没有
            if (skuInfo==null){
                //避免直接查询数据库，造成缓存击穿，获取分布式锁
                String lockKey=RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                RLock lock = redissonClient.getLock(lockKey);
                boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                //成功获取分布式锁
                if(res){
                    try {
                        //查询数据库获取数据
                        skuInfo=getSkuInfoDB(skuId);
                        //数据库中没有，在缓存在存入null值
                        if(skuInfo==null){
                            SkuInfo skuInfo1=new SkuInfo();
                            redisTemplate.opsForValue()
                                    .set(skuKey,skuInfo1,RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            return skuInfo1;
                        }
                        //数据库中有，放入缓存，并返回
                        redisTemplate.opsForValue().set(skuKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                        return skuInfo;
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        //解除分布式锁
                        lock.unlock();
                    }
                }else {
                    //没有获取分布式锁，等待1秒后，尝试
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            }else {
                //缓存中有数据，返回数据
                return skuInfo;
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //避免redisson宕机，查询数据库作为兜底
        return getSkuInfoDB(skuId);
    }
    //在数据库中查取skuInfo和相应的Images
    private SkuInfo getSkuInfoDB(Long skuId){
        SkuInfo skuInfo=skuInfoMapper.selectById(skuId);
        if(skuInfo!=null){
            QueryWrapper<SkuImage> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("sku_id",skuId);
            List<SkuImage> skuImages = skuImageMapper.selectList(queryWrapper);
            skuInfo.setSkuImageList(skuImages);

        }
        return skuInfo;
    }

    @GmallCache(prefix = "categoryViewByCategory3Id:")
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }
    @GmallCache(prefix = "skuPrice:")
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo!=null){
            return skuInfo.getPrice();
        }
        return new BigDecimal(0);
    }
    @GmallCache(prefix = "spuSaleAttrListCheckBySku:")
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttr> spuSaleAttrs=spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrs;
    }
    @GmallCache(prefix = "saleAttrValuesBySpu:")
    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        Map<Object,Object> map=new HashMap<>();
        List<Map> maps = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if(maps!=null &&maps.size()>0){
            for(Map skuMap:maps){
                map.put(skuMap.get("value_ids"),skuMap.get("sku_id"));
            }
        }
        return map;
    }

    @Override
    @GmallCache(prefix = "category")
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list=new ArrayList<>();
        //获取所有信息
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //根据category1Id进行分组
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream()
                .collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //第一级分类的标识
        int index=1;
        //遍历每个一级分类
        for(Map.Entry<Long,List<BaseCategoryView>> entry1:category1Map.entrySet()){
            //获取一级分类Id
            Long category1Id = entry1.getKey();
            //获取一级分类下所有的数据
            List<BaseCategoryView> category2List = entry1.getValue();
            //组装一级分类数据
            JSONObject category1=new JSONObject();
            category1.put("index",index);
            category1.put("categoryId",category1Id);
            category1.put("categoryName",category2List.get(0).getCategory1Name());
            index++;
            //组装一级下的child，根据category2Id分组
            Map<Long, List<BaseCategoryView>> category2Map = category2List.stream()
                    .collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //用来存在一级的child
            List<JSONObject> category2Child=new ArrayList<>();
            //遍历每个二级
            for(Map.Entry<Long,List<BaseCategoryView>> entry2:category2Map.entrySet()){
                //获取二级Id
                Long category2Id=entry2.getKey();
                //三级数据
                List<BaseCategoryView> category3List=entry2.getValue();
                //组装二级数据
                JSONObject category2=new JSONObject();
                category2.put("categoryId",category2Id);
                category2.put("categoryName",category3List.get(0).getCategory2Name());
                //一级的child，即二级数据
                category2Child.add(category2);
                //存放二级的child
                List<JSONObject> category3Child=new ArrayList<>();
                //三级数据
                category3List.stream().forEach(category3View->{
                    JSONObject category3=new JSONObject();
                    category3.put("categoryId",category3View.getCategory3Id());
                    category3.put("categoryName",category3View.getCategory3Name());
                    category3Child.add(category3);
                });
                category2.put("categoryChild",category3Child);
            }
            category1.put("categoryChild",category2Child);
            list.add(category1);
        }
        return list;
    }

    private List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper queryWrapper=new QueryWrapper<BaseAttrValue>();
        queryWrapper.eq("attr_id",attrId);
        return baseAttrValueMapper.selectList(queryWrapper);
    }
}
