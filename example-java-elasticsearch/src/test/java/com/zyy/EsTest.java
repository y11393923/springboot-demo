package com.zyy;

import com.alibaba.fastjson.JSON;
import com.zyy.bean.Detect;
import com.zyy.config.EsClient;
import com.zyy.utils.EsUtil;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsNodes;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

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


    /**
     * 深分页
     */
    @Test
    public void scrollPages(){
        SearchResponse searchResponse = EsClient.getClient().prepareSearch("detect_index")
                .setTypes("history")
                .setQuery(QueryBuilders.termQuery("taskId",8))
                .addSort("receivedTime", SortOrder.DESC)
                .setSize(10)
                .setScroll(new TimeValue(1000))
                .get();
        //获取总数量
        long totalCount = searchResponse.getHits().getTotalHits();
        //计算总页数
        int totalPage = totalCount % 10 == 0 ? (int)totalCount / 10 : ((int)totalCount / 10) + 1;

        System.out.println("总数据：" + totalCount + "----------页数：" + totalPage);
        System.out.println("----------当前页：" + 1 + "-----------------");
        for (SearchHit hit:searchResponse.getHits()) {
            System.out.println( hit.getSourceAsMap().get("receivedTime") + "---" + hit.getSourceAsString());
        }

        for (int i = 2; i <= totalPage ;i++){
            System.out.println("----------当前页：" + i + "-----------------");
            searchResponse = EsClient.getClient()
                    //再次发送请求,并使用上次搜索结果的ScrollId
                    .prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(1000)).get();

            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit:hits) {
                System.out.println( hit.getSourceAsMap().get("receivedTime") + "---" + hit.getSourceAsString());
            }
        }

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


    /**
     * 根据时间聚合
     */
    @Test
    public void queryByDateAggregation() throws ParseException {
        // 根据日期聚合
        DateHistogramAggregationBuilder field = AggregationBuilders.dateHistogram("alarmCount").field("receivedTime")
                .subAggregation(AggregationBuilders.cardinality("countEventId").field("eventId.keyword"));
        field.dateHistogramInterval(DateHistogramInterval.DAY);
        // 指定东八时区
        field.timeZone(DateTimeZone.forID("Asia/Shanghai"));
        // 格式化日期
        field.format("yyyy-MM-dd");
        // 只返回文档数量DocCount 大于50000的
        //field.minDocCount(10000);
        // 指定时间间隔 不在查询范围内会补0
        field.extendedBounds(new ExtendedBounds("2019-10-20", "2019-10-25"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //只查询 26 - 30 之间的数据
        boolQueryBuilder.must(QueryBuilders.rangeQuery("receivedTime").from(sdf.parse("2019-10-26 00:00:00").getTime())
                .to(sdf.parse("2019-10-31 00:00:00").getTime()).includeLower(true).includeUpper(false));

        SearchRequestBuilder searchRequestBuilder = EsClient.getClient().prepareSearch("detect_index")
                .setTypes("history")
                .setQuery(boolQueryBuilder)
                .addAggregation(field)
                .setSize(0);

        SearchResponse searchResponse = searchRequestBuilder.get();
        // date -> count : yyyy-MM-dd -> num
        Map<String, Long> alarmCount = new LinkedHashMap<>();
        if (searchResponse.status() == RestStatus.OK){
            Histogram histogram = searchResponse.getAggregations().get("alarmCount");
            // 获取date -> alarmNum 的 集合
            for (Histogram.Bucket entry : histogram.getBuckets()) {
                Cardinality value = entry.getAggregations().get("countEventId");
                // 每天的日期，yyyy-MM-dd
                String date = entry.getKeyAsString();
                // eventId count
                long alarmNum = value.getValue();
                long docCount = entry.getDocCount();
                alarmCount.put(date, alarmNum);
            }
        }
        alarmCount.forEach((key, value) -> System.out.println(key + "-" + value));
    }

    /**
     * 根据时间聚合
     */
    @Test
    public void queryByDateAggregation2() throws ParseException {
        AggregationBuilder field = AggregationBuilders.terms("alarmCount").field("camera.serial.keyword")
                .subAggregation(AggregationBuilders.count("countEventId").field("eventId.keyword"));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        SearchRequestBuilder searchRequestBuilder = EsClient.getClient().prepareSearch("detect_index")
                .setTypes("history")
                .setQuery(boolQueryBuilder)
                .addAggregation(field)
                .setSize(0);

        SearchResponse searchResponse = searchRequestBuilder.get();
        // date -> count : yyyy-MM-dd -> num
        Map<String, Long> alarmCount = new LinkedHashMap<>();
        if (searchResponse.status() == RestStatus.OK){
            StringTerms stringTerms = searchResponse.getAggregations().get("alarmCount");
            // 获取date -> alarmNum 的 集合
            for (Terms.Bucket entry : stringTerms.getBuckets()) {
                InternalValueCount value = entry.getAggregations().get("countEventId");
                // 每天的日期，yyyy-MM-dd
                String date = entry.getKeyAsString();
                // eventId count
                long alarmNum = value.getValue();
                long docCount = entry.getDocCount();
                alarmCount.put(date, alarmNum);
            }
        }
        alarmCount.forEach((key, value) -> System.out.println(key + "-" + value));
    }

    @Test
    public void queryByMaxAggregation(){
        // date -> alarmTime
        Map<String, Long> alarmDuaration = new LinkedHashMap();
        DateHistogramAggregationBuilder field = AggregationBuilders.dateHistogram("alarmDuration").field("receivedTime")
                // 根据eventId聚合
                .subAggregation(AggregationBuilders.terms("groupByEventId").field("eventId.keyword").size(10000)
                        // 拿eventTime的最大值即每个任务的时间
                        .subAggregation(AggregationBuilders.max("maxEventTime").field("eventTime")));

        field.dateHistogramInterval(DateHistogramInterval.DAY);
        // 指定东八时区
        field.timeZone(DateTimeZone.forID("Asia/Shanghai"));
        // 格式化日期
        field.format("yyyy-MM-dd");

        SearchRequestBuilder searchRequestBuilder = EsClient.getClient().prepareSearch("detect_index")
                .setTypes("history")
                .addAggregation(field)
                .setSize(0);

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        Histogram histogram = response.getAggregations().get("alarmDuration");

        for (Histogram.Bucket entry : histogram.getBuckets()) {
            double score = 0;
            // 得到每个eventId的bucket
            Terms groupByEventId = entry.getAggregations().get("groupByEventId");
            for (Terms.Bucket bucketMax : groupByEventId.getBuckets()) {
                // 拿最大的eventTime
                Max eventTime = bucketMax.getAggregations().get("maxEventTime");
                score += eventTime.getValue();
            }
            String date = entry.getKeyAsString();
            Long count = new Double(score).longValue();
            alarmDuaration.put(date, count);
        }

        alarmDuaration.forEach((key, value) -> System.out.println(key + "-" + value));
    }

    /**
     * 日期转换
     */
    @Test
    public void testDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("当前时间转换" + localDateTime.format(formatter));

        LocalDateTime dateTime = LocalDateTime.parse("2019-10-30 10:00:00", formatter);
        System.out.println(dateTime.format(formatter));


        System.out.println(Instant.now().atOffset(ZoneOffset.ofHours(8)).toEpochSecond() == System.currentTimeMillis()/ 1000);
        System.out.println(Instant.now(Clock.systemDefaultZone()).getEpochSecond());
        System.out.println(System.currentTimeMillis()/ 1000);

        Instant instant = Instant.ofEpochMilli(Instant.now(Clock.systemDefaultZone()).toEpochMilli());
        System.out.println(instant.getEpochSecond());


    }


    @Test
    public void sss(){
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("taskId").field("taskId.keyword")
                .subAggregation(AggregationBuilders.cardinality("alarmCount").field("eventId.keyword")).size(10000);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termsQuery("taskId.keyword",new Integer[]{1,2}));
        builder.must(QueryBuilders.existsQuery("eventId.keyword"));
        builder.must(QueryBuilders.termQuery("isAlarm", "1"));
        builder.must(QueryBuilders.termQuery("platform.keyword", "SCA"));
        SearchResponse searchResponse = EsClient.getClient()
                .prepareSearch("detect")
                .setTypes("history")
                .setQuery(builder)
                .addAggregation(aggregationBuilder)
                .get();
        Map<Long, Long> taskAlarmMap = new HashMap();
        Terms terms = searchResponse.getAggregations().get("taskId");
        Iterator<? extends Terms.Bucket> iterator = terms.getBuckets().iterator();
        while (iterator.hasNext()){
            Terms.Bucket bucket = iterator.next();
            Cardinality cardinality = bucket.getAggregations().get("alarmCount");
            long alarmCount = cardinality.getValue();
            taskAlarmMap.put(Long.parseLong(bucket.getKey().toString()), alarmCount);
        }
        System.out.println(taskAlarmMap);
    }

}
