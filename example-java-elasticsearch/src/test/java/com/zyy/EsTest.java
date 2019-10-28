package com.zyy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyy.bean.Detect;
import com.zyy.utils.EsUtil;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class EsTest {
    @Test
    public void insert(){
        Map<String, String> source = new HashMap<>();
        source.put("key", "testKey");
        source.put("value", "testValue");
        EsUtil.insertData("test_index","history","testId", source);
    }

    @Test
    public void createIndex(){
        EsUtil.createIndex("test_index");
    }

    @Test
    public void delete(){
        EsUtil.deleteData("test_index", "history", "testId");
    }

    @Test
    public void deleteIndex(){
        EsUtil.deleteIndex("test_index");
    }

    @Test
    public void updateData() throws IOException {
        EsUtil.updateData("test_index", "history", "testId","key","testKey2");
    }

    @Test
    public void updateDataMap() throws IOException {
        Map<String, Object> source = new HashMap<>();
        source.put("key", "testKey3");
        source.put("value", "testValue3");
        EsUtil.updateData("test_index", "history", "testId",source);
    }

    @Test
    public void queryById(){
        String result = EsUtil.queryById("test_index", "history", "testId");
        System.out.println(result);
    }

    @Test
    public void statCount(){
        long count = EsUtil.statCount("detect_index");
        System.out.println(count);
    }

    @Test
    public void queryPage(){
        List<Detect> detects = EsUtil.queryPage("detect_index", "history", Detect.class);
        detects.forEach(detect -> System.out.println(detect.toString()));
    }

    @Test
    public void queryByCondition(){
        Map<String, Object> condition = new HashMap<>();
        /*Integer[] integers=new Integer[]{0,3};
        condition.put("taskId", integers);*/
        SortBuilder sortBuilder = SortBuilders.fieldSort("eventTime").order(SortOrder.DESC);
        List<Detect> detects = EsUtil.queryByCondition("detect_index", "history", condition, sortBuilder, Detect.class);
        detects.forEach(detect -> System.out.println(detect.toString()));
    }

    @Test
    public void queryByAggregation(){
        //里面的size设置聚合后每组的数据量默认3条，外面的size设置有多少组默认10组
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("taskId").field("taskId.keyword")
                .subAggregation(AggregationBuilders.topHits("event").size(5)).size(50);
        Map<String, List<Detect>> result = EsUtil.queryByAggregation("detect_index", "history", aggregationBuilder, "event", Detect.class);
        result.forEach((key, value) ->{
            System.out.println(key);
            value.forEach(e -> {
                System.out.println(e.toString());
            });
        });
    }

    /**
     * 多重聚合
     */
    @Test
    public void queryByMultiAggregation(){
        //第一个size设置cameraSerials聚合的数量，第二个size设置多重聚合后返回的数据条数，第三个size设置taskId聚合的数量
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("taskId").field("taskId.keyword")
                .subAggregation(AggregationBuilders.terms("cameraSerials").field("camera.serial.keyword").size(10)
                .subAggregation(AggregationBuilders.topHits("event")
                        .sort("capturedTime", SortOrder.DESC).size(5))).size(50);
        Map<String, Aggregation> aggregationMap = EsUtil.queryByAggregation("detect_index", "history", aggregationBuilder);
        //根据name获取聚合后的全部数据
        StringTerms stringTerms = (StringTerms)aggregationMap.get("taskId");
        //获取桶中的数据用于封装
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        Map<String, Map<String, List<Detect>>> result = new HashMap<>();
        Map<String, List<Detect>> listMap;
        List<Detect> lists;
        while (iterator.hasNext()){
            StringTerms.Bucket bucket = iterator.next();
            //获取第二重聚合的数据
            stringTerms = (StringTerms) bucket.getAggregations().asMap().get("cameraSerials");
            Iterator<StringTerms.Bucket> bucketIterator = stringTerms.getBuckets().iterator();
            listMap = new HashMap<>();
            while (bucketIterator.hasNext()){
                StringTerms.Bucket next = bucketIterator.next();
                //根据topHitsName获取存放数据的SearchHit数组
                InternalTopHits internalTopHits = (InternalTopHits)next.getAggregations().asMap().get("event");
                SearchHit[] searchHits = internalTopHits.getHits().getHits();
                lists = new ArrayList<>();
                //遍历数组封装返回值
                if (Objects.nonNull(searchHits) && searchHits.length > 0){
                    for (SearchHit searchHit:searchHits) {
                        lists.add(JSON.parseObject(searchHit.getSourceAsString(), Detect.class));
                    }
                }
                listMap.put(next.getKeyAsString(), lists);
            }
            result.put(bucket.getKeyAsString(), listMap);
        }
        result.forEach((key, value) ->{
            value.forEach((k , v) ->{
                System.out.println(key + "-" + k);
                v.forEach(e -> {
                    System.out.println(e.toString());
                });
            });
        });
    }

}
