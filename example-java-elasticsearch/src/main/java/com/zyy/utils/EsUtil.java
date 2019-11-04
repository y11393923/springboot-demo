package com.zyy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyy.config.EsClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;
import org.elasticsearch.search.sort.SortBuilder;

import java.io.IOException;
import java.util.*;

public class EsUtil {

    /**
     * 新增数据
     * @param index
     * @param type
     * @param id
     * @param source
     * @return
     */
    public static boolean insertData(String index, String type, String id, Map<String,?> source){
        IndexResponse response = EsClient.getClient().prepareIndex(index, type)
                .setId(id)
                .setSource(source)
                .get();
        return response.isFragment();
    }

    /**
     * 批量新增数据
     * @param index
     * @param type
     * @param data
     * @return
     */
    public static boolean batchInsertData(String index, String type, List<Map<String, ?>> data){
        BulkRequestBuilder bulkRequestBuilder = EsClient.getClient().prepareBulk();
        data.forEach(item -> bulkRequestBuilder
                .add(EsClient.getClient()
                .prepareIndex(index, type)
                .setSource(item)));
        return bulkRequestBuilder.get().hasFailures();
    }



    /**
     * 新增数据 json格式
     * @param index
     * @param type
     * @param id
     * @param jsonSource
     * @return
     */
    public static boolean insertData(String index, String type, String id, String jsonSource){
        IndexResponse response = EsClient.getClient().prepareIndex(index, type)
                .setId(id)
                .setSource(jsonSource, XContentType.JSON)
                .get();
        return response.isFragment();
    }

    /**
     * 批量新增数据 json格式
     * @param index
     * @param type
     * @param jsonData
     * @return
     */
    public static boolean batchInsertData(String index, String type, Collection<String> jsonData){
        BulkRequestBuilder bulkRequestBuilder = EsClient.getClient().prepareBulk();
        jsonData.forEach(item -> bulkRequestBuilder
                .add(EsClient.getClient()
                        .prepareIndex(index, type)
                        .setSource(item, XContentType.JSON)));
        return bulkRequestBuilder.get().hasFailures();
    }

    /**
     * 删除数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    public static boolean deleteData(String index, String type, String id){
        DeleteResponse response = EsClient.getClient()
                .prepareDelete(index, type, id)
                .get();
        return response.isFragment();
    }

    /**
     * 根据条件删除数据  返回删除时间
     * @param index
     * @param condition
     * @return
     */
    public static long deleteByCondition(String index, Map<String, Object> condition){
        BulkByScrollResponse scrollResponse = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(EsClient.getClient())
                .filter(constructCondition(condition))
                .source(index)
                .get();
        return scrollResponse.getDeleted();
    }

    /**
     * 删除索引库
     * @param index
     * @return
     */
    public static boolean deleteIndex(String index){
        AcknowledgedResponse response = EsClient.getClient()
                .admin()
                .indices()
                .prepareDelete(index)
                .get();
        return response.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     */
    public static boolean isIndexExists(String indexName){
        IndicesExistsResponse response = EsClient.getClient()
                .admin()
                .indices()
                .exists(new IndicesExistsRequest(indexName))
                .actionGet();
        return response.isExists();
    }

    /**
     * 创建索引库
     * @param indexName
     */
    public static boolean createIndex(String indexName) {
        if (isIndexExists(indexName)){
            return false;
        }
        CreateIndexResponse response = EsClient.getClient()
                .admin()
                .indices()
                .create(new CreateIndexRequest(indexName))
                .actionGet();
        return response.isAcknowledged();
    }

    /**
     * 更新数据
     * @param index
     * @param type
     * @param id
     * @param field
     * @param value
     * @return
     * @throws IOException
     */
    public static boolean updateData(String index, String type, String id, String field, Object value) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(
                XContentFactory
                        .jsonBuilder()
                        .startObject()
                        .field(field, value)
                        .endObject());
        UpdateResponse response = EsClient.getClient().update(updateRequest).actionGet();
        return response.isFragment();
    }

    /**
     * 更新数据
     * @param index
     * @param type
     * @param id
     * @param params
     * @return
     * @throws IOException
     */
    public static boolean updateData(String index, String type, String id, Map<String, Object> params) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
        //遍历map设置需要更新的数据
        for (Map.Entry<String,Object> entry : params.entrySet()) {
            contentBuilder.field(entry.getKey(), entry.getValue());
        }
        contentBuilder.endObject();
        updateRequest.doc(contentBuilder);
        UpdateResponse response = EsClient.getClient().update(updateRequest).actionGet();
        return response.isFragment();
    }

    /**
     * 根据id查询数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    public static String queryById(String index, String type, String id){
        return EsClient.getClient()
                .prepareGet(index, type, id)
                .get()
                .getSourceAsString();
    }

    /**
     * 根据id查询返回实体类
     * @param index
     * @param type
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T queryById(String index, String type, String id, Class<T> clazz){
        return JSON.parseObject(queryById(index, type, id), clazz);
    }

    /**
     * 获取索引库下所有文档数量
     * @param indexName
     * @return
     */
    public static long statCount(String indexName){
        IndicesStatsResponse response = EsClient.getClient()
                .admin()
                .indices()
                .prepareStats(indexName)
                .all()
                .get();
        return response.getPrimaries().getDocs().getCount();
    }

    /**
     * 获取索引库下某种类型所有文档数量
     * @return
     */
    public static long statCount(String index, String type){
        SearchResponse searchResponse = EsClient.getClient()
                .prepareSearch(index)
                .setTypes(type)
                .setSize(0)
                .execute()
                .actionGet();
        return searchResponse.getHits().getTotalHits();
    }

    public static <T> List<T> queryPage(String index, String type, Class<T> clazz){
        return queryPage(index, type, null, clazz);
    }

    public static <T> List<T> queryPage(String index, String type, Integer from, Integer size, Class<T> clazz){
        return queryPage(index, type, from, size, null, clazz);
    }

    public static <T> List<T> queryPage(String index, String type, SortBuilder sortBuilder, Class<T> clazz){
        return queryPage(index, type, null, null, sortBuilder, clazz);
    }

    /**
     * 分页查询数据
     * @param index
     * @param type
     * @param from
     * @param size  如果size为空默认查询10条
     * @param sortBuilder
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> queryPage(String index, String type, Integer from, Integer size, SortBuilder sortBuilder, Class<T> clazz){
        return queryByCondition(index, type, from, size, (QueryBuilder) null, sortBuilder, clazz);
    }

    public static <T> List<T> queryByCondition(String index, String type, Map<String, Object> conditions, Class<T> clazz){
        return queryByCondition(index, type, null, null, conditions, clazz);
    }

    public static <T> List<T> queryByCondition(String index, String type, Map<String, Object> conditions, SortBuilder sortBuilder, Class<T> clazz){
        return queryByCondition(index, type, null, null, conditions, sortBuilder, clazz);
    }

    public static <T> List<T> queryByCondition(String index, String type,Integer from, Integer size, Map<String, Object> conditions, Class<T> clazz){
        return queryByCondition(index, type, from, size, conditions, null, clazz);
    }

    /**
     * 分页多条件查询
     * @return
     */
    public static <T> List<T> queryByCondition(String index, String type,Integer from, Integer size, Map<String, Object> conditions, SortBuilder sortBuilder, Class<T> clazz){
        return queryByCondition(index, type, from, size, constructCondition(conditions), sortBuilder, clazz);
    }

    /**
     * 构建条件 使用termsQuery精准匹配不带分词功能, matchQuery是带分词功能
     * must 相当于 and  ,  should 相当于 or ， mustNot 取反
     * @param conditions
     * @return
     */
    private static QueryBuilder constructCondition(Map<String, Object> conditions){
        //封装查询条件
        BoolQueryBuilder boolQueryBuilder = null;
        if (Objects.nonNull(conditions)){
            boolQueryBuilder = QueryBuilders.boolQuery();
            for (Map.Entry<String, Object> entry:conditions.entrySet()){
                //如果是数组或者集合需要用termsQuery方法，因为是map拿出来必须强转不能直接使用value否则查询不出来
                if (entry.getValue().getClass().isArray()){
                    boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), (Object[])entry.getValue()));
                }else if(entry.getValue() instanceof Collection){
                    boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), (Collection)entry.getValue()));
                }else{
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }
        return boolQueryBuilder;
    }

    public static <T> List<T> queryByCondition(String index, String type, QueryBuilder queryBuilder, Class<T> clazz){
        return queryByCondition(index, type, null, null, queryBuilder, clazz);
    }

    public static <T> List<T> queryByCondition(String index, String type, Integer from, Integer size, QueryBuilder queryBuilder, Class<T> clazz){
        return queryByCondition(index, type, from, size, queryBuilder, null, clazz);
    }

    public static <T> List<T> queryByCondition(String index, String type, QueryBuilder queryBuilder, SortBuilder sortBuilder, Class<T> clazz){
        return queryByCondition(index, type, null, null, queryBuilder, sortBuilder, clazz);
    }

    /**
     * 分页多条件查询
     * @param index
     * @param type
     * @param from
     * @param size  如果size为空默认查询10条
     * @param queryBuilder
     * @param sortBuilder
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> queryByCondition(String index, String type, Integer from, Integer size, QueryBuilder queryBuilder, SortBuilder sortBuilder, Class<T> clazz){
        SearchRequestBuilder searchRequestBuilder = EsClient.getClient()
                .prepareSearch(index)
                .setTypes(type);
        //设置查询开始位置0是第一条
        if (Objects.nonNull(from)){
            searchRequestBuilder.setFrom(from);
        }
        //设置查询条数默认10条
        if (Objects.nonNull(size)){
            searchRequestBuilder.setSize(size);
        }
        //封装查询条件 没有条件就查询全部
        if (Objects.nonNull(queryBuilder)){
            searchRequestBuilder.setQuery(queryBuilder);
        }else{
            searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        }
        //设置排序
        if (Objects.nonNull(sortBuilder)){
            searchRequestBuilder.addSort(sortBuilder);
        }
        return execute(searchRequestBuilder, clazz);
    }

    public static Map<String, Aggregation> queryByAggregation(String index, String type, AggregationBuilder aggregationBuilder){
        SearchResponse searchResponse = EsClient.getClient()
                .prepareSearch(index)
                .setTypes(type)
                .addAggregation(aggregationBuilder)
                .get();
        return searchResponse.getAggregations().asMap();
    }

    /**
     * 根据参数聚合操作
     * @param index
     * @param type
     * @param aggregationBuilder
     * @return
     */
    public static <T> Map<String, List<T>> queryByAggregation(String index, String type, AggregationBuilder aggregationBuilder, String topHitsName, Class<T> clazz){
        return getAggregationResult(
                queryByAggregation(index, type, aggregationBuilder),
                aggregationBuilder.getName(),
                topHitsName,
                clazz);
    }

    /**
     * 获取聚合后的数据(只能获取单个聚合的数据)
     * @param aggregationMap
     * @param name
     * @param topHitsName
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, List<T>> getAggregationResult(Map<String, Aggregation> aggregationMap, String name, String topHitsName, Class<T> clazz){
        //根据name获取聚合后的全部数据
        StringTerms stringTerms = (StringTerms)aggregationMap.get(name);
        //获取桶中的数据用于封装
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        Map<String, List<T>> result = new HashMap<>();
        List<T> lists;
        while (iterator.hasNext()){
            StringTerms.Bucket bucket = iterator.next();
            //获取每个分组下的所有总条数
            long docCount = bucket.getDocCount();
            //根据topHitsName获取存放数据的SearchHit数组
            InternalTopHits internalTopHits = (InternalTopHits)bucket.getAggregations().asMap().get(topHitsName);
            SearchHit[] searchHits = internalTopHits.getHits().getHits();
            lists = new ArrayList<>();
            //遍历数组封装返回值
            if (Objects.nonNull(searchHits) && searchHits.length > 0){
                for (SearchHit searchHit:searchHits) {
                    JSONObject jsonObject = JSON.parseObject(searchHit.getSourceAsString());
                    jsonObject.put("docCount", docCount);
                    lists.add(jsonObject.toJavaObject(clazz));
                }
            }
            result.put(bucket.getKeyAsString(), lists);
        }
        return result;
    }

    /**
     * 执行查询操作封装查询返回值
     * @param searchRequestBuilder
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> execute(SearchRequestBuilder searchRequestBuilder, Class<T> clazz){
        SearchResponse searchResponse = searchRequestBuilder.get();
        List<T> result = new ArrayList<>();
        if (searchResponse.status() != RestStatus.OK){
            return result;
        }
        SearchHits responseHits = searchResponse.getHits();
        SearchHit[] hits = responseHits.getHits();
        if (Objects.nonNull(hits) && hits.length > 0){
            for (SearchHit hit:hits) {
                result.add(JSON.parseObject(hit.getSourceAsString(), clazz));
            }
        }
        return result;
    }

}
