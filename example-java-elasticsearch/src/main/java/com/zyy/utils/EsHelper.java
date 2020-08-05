package com.zyy.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static org.elasticsearch.action.DocWriteResponse.Result.NOT_FOUND;
import static org.elasticsearch.common.unit.TimeValue.timeValueMillis;
import static org.elasticsearch.common.unit.TimeValue.timeValueMinutes;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * @ClassName: EsHelper
 * @Description: Elasticsearch工具类
 */
@Component
public class EsHelper {

    private static final Logger logger = LoggerFactory.getLogger(EsHelper.class);

    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    private RestHighLevelClient client;

    @Value("${polaris.alarm.es.clusterAddress}")
    private String clusterAddress;

    @Value("${polaris.alarm.es.connectTimeout:10000}")
    private int connectTimeout;

    @Value("${polaris.alarm.es.socketTimeout:10000}")
    private int socketTimeout;

    @Value("${polaris.alarm.es.connectionRequestTimeout:10000}")
    private int connectionRequestTimeout;

    @Value("${polaris.alarm.es.maxConnectNum:100}")
    private int maxConnectNum = 100;

    @Value("${polaris.alarm.es.maxConnectPerRoute:100}")
    private int maxConnectPerRoute = 100;

    /**
     * client初始化
     */
    @PostConstruct
    public void init() {
        List<HttpHost> hostLists = new ArrayList<>();
        String[] hostList = clusterAddress.split(",");
        for (String addr : hostList) {
            String host = addr.split(":")[0];
            String port = addr.split(":")[1];
            hostLists.add(new HttpHost(host, Integer.parseInt(port)));
        }
        // 转换成 HttpHost 数组
        HttpHost[] httpHost = hostLists.toArray(new HttpHost[]{});
        // 构建连接对象
        RestClientBuilder builder = RestClient.builder(httpHost);
        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return requestConfigBuilder;
        });
        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });
        client = new RestHighLevelClient(builder);
    }

    /**
     * 增加文档
     *
     * @param index      索引名称
     * @param id         索引ID
     * @param jsonString json字符串
     * @throws IOException 异常
     */
    public Boolean insert(String index, String type, String id, String jsonString) throws IOException {
        // request的opType默认是INDEX(传入相同id会覆盖原document，CREATE则会将旧的删除)
        IndexRequest request = new IndexRequest(index)
                .type(type)
                .id(id)
                .source(jsonString, XContentType.JSON);
        IndexResponse response;
        try {
            response = client.index(request);
            // 分片处理信息
            ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
            // 获取分片副本写入失败
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    logger.error("fail to insert doc :{} to replica for reason：{}", id, failure.reason());
                }
            }
        } catch (ElasticsearchException e) {
            if (e.status().equals(RestStatus.CONFLICT)) {
                logger.error("doc version conflict...", e);
            }
            logger.error("insert new document failed...", e);
            return false;
        }
        return true;
    }

    /**
     * 查找文档
     *
     * @param index 索引
     * @param type  索引类型
     * @param id    ID
     * @return json字符串
     * @throws IOException 异常
     */
    public String getById(String index, String type, String id) throws IOException {
        GetRequest request = new GetRequest()
                .realtime(false)
                .refresh(true)
                .index(index)
                .type(type)
                .id(id);
        GetResponse response;
        try {
            response = client.get(request);
        } catch (ElasticsearchException e) {
            if (e.status().equals(RestStatus.NOT_FOUND)) {
                logger.error("document not found :{}", id, e);
            }
            if (e.status().equals(RestStatus.CONFLICT)) {
                logger.error("version conflict", e);
            }
            logger.error("fail to search document by id", e);
            return null;
        }

        if (response.isExists()) {
            return response.getSourceAsString();
        } else {
            return null;
        }
    }

    /**
     * 通过一个JSON字符串更新文档，如果文档不存在，则新建文档
     *
     * @param index 索引名称
     * @param type  索引类型
     * @param id    文档ID
     * @param field 字段
     * @param value 数值
     * @throws IOException 异常
     */
    public RestStatus updateById(String index, String type, String id, String field, Object value) throws IOException {
        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(jsonBuilder()
                .startObject()
                .field(field, value)
                .endObject());
        // 如果要更新的文档不存在，则失败
        request.docAsUpsert(false);
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            UpdateResponse response = client.update(request);
            ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    logger.error("fail to update doc :{} to replica for reason：{}", id, failure.reason());
                }
            }
        } catch (ElasticsearchException e) {
            if (e.status().equals(RestStatus.NOT_FOUND)) {
                logger.error("document not found when execute update request", e);
            } else if (e.status().equals(RestStatus.CONFLICT)) {
                logger.error("version conflict", e);
            }
            return e.status();
        }
        return RestStatus.OK;
    }

    /**
     * 通过一个JSON字符串更新文档，如果文档不存在，则新建文档
     *
     * @param index      索引名称
     * @param type       索引类型
     * @param id         文档ID
     * @param jsonString json字符串
     * @throws IOException 异常
     */
    public Boolean updateById(String index, String type, String id, String jsonString) throws IOException {
        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(jsonString, XContentType.JSON);
        // 如果要更新的文档不存在，则根据传入的参数新建一个文档
        request.docAsUpsert(true);
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        try {
            UpdateResponse response = client.update(request);
            ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    logger.error("fail to update doc :{} to replica for reason：{}", id, failure.reason());
                }
            }
        } catch (ElasticsearchException e) {
            if (e.status().equals(RestStatus.NOT_FOUND)) {
                logger.error("document not found when execute update request", e);
            } else if (e.status().equals(RestStatus.CONFLICT)) {
                logger.error("version conflict", e);
            }
            return false;
        }
        return true;
    }

    /**
     * 删除文档
     *
     * @param index 索引名称
     * @param type  索引类型
     * @param id    文档ID
     * @throws IOException 异常
     */
    public Boolean deleteById(String index, String type, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(index, type, id);
        DeleteResponse response = null;
        try {
            response = client.delete(request);
        } catch (ElasticsearchException e) {
            if (e.status().equals(RestStatus.CONFLICT)) {
                logger.error("version conflict...", e);
            }
            logger.error("fail to delete document in index {} with id {}", index, id, e);
            return false;
        }

        if (response != null) {
            if (response.getResult().equals(NOT_FOUND)) {
                logger.error("document not found...");
            }
            // 副本删除
            ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                    logger.error("fail to delete doc :{} to replica for reason：{}", id, failure.reason());
                }
            }
            logger.info("success to delete document in index {} with id：{}", index, id);
        }
        return true;
    }

    /**
     * 批量添加
     *
     * @param index   索引名称
     * @param type    索引类型
     * @param jsonMap 一个索引ID和json字符串的映射
     * @throws IOException 异常
     */
    public void bulkInsertData(String index, String type, Map<String, String> jsonMap) throws IOException {
        BulkRequest request = new BulkRequest();
        jsonMap.forEach((id, jsonString) -> request.add(new IndexRequest(index, type, id).source(XContentType.JSON, jsonString)));
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        BulkResponse response = client.bulk(request);
        // 全部操作成功
        if (response.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : response) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                    logger.error("bulk insert data id: {} fail for reason {}", failure.getId(), failure.getMessage());
                }
            }
        }
    }

    /**
     * 批量获取
     *
     * @param index 索引
     * @param ids   需要获取的id数组
     * @return 一个索引ID和json字符串的映射
     * @throws IOException 异常
     */
    public Map<String, String> multiGet(String index, List<String> ids) throws IOException {
        Map<String, String> resultList = new HashMap<>();
        MultiGetRequest request = new MultiGetRequest();
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        request.realtime(false);
        request.refresh(true);
        MultiGetResponse response = client.multiGet(request);
        for (MultiGetItemResponse next : response) {
            String source = next.getResponse().getSourceAsString();
            resultList.put(next.getId(), source);
        }
        return resultList;
    }

    /**
     * 针对keyword类型的字段进行搜索
     *
     * @param index         索引
     * @param types         索引类型数组
     * @param sourceBuilder 查询条件
     */
    public SearchResponse search(String index, String[] types, SearchSourceBuilder sourceBuilder) throws IOException {
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest request = new SearchRequest(index);
        request.source(sourceBuilder);
        if (types != null && types.length > 0) {
            request.types(types);
        }
        // 执行查询，然后处理响应结果
        return client.search(request);
    }

    /**
     * 计数
     *
     * @param index         索引
     * @param types         索引类型数组
     * @param sourceBuilder 查询条件
     * @return 计数
     */
    public long count(String index, String[] types, SearchSourceBuilder sourceBuilder) {
        try {
            sourceBuilder.fetchSource(false);
            SearchRequest request = new SearchRequest(index);
            request.source(sourceBuilder);
            if (types != null && types.length > 0) {
                request.types(types);
            }
            return client.search(request).getHits().totalHits;
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 使用游标获取全部结果，返回SearchHit集合
     * @param scrollTimeOut
     * @param index
     * @param types
     * @param sourceBuilder
     * @return
     * @throws IOException
     */
    public List<SearchHit> scrollSearchAll(Long scrollTimeOut, String index, String[] types, SearchSourceBuilder sourceBuilder) throws IOException {
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        if (types != null && types.length > 0) {
            searchRequest.types(types);
        }
        Scroll scroll = new Scroll(timeValueMillis(scrollTimeOut));
        searchRequest.scroll(scroll);
        SearchResponse searchResponse = client.search(searchRequest);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<SearchHit> resultSearchHit = new ArrayList<>();
        int i=0;
        while (ArrayUtils.isNotEmpty(hits)) {
            for (SearchHit hit : hits) {
                resultSearchHit.add(hit);
            }
            i++;
            logger.info(i+"scrollId："+scrollId+"    hits size: "+hits.length);

            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(scroll);
            SearchResponse searchScrollResponse = client.searchScroll(searchScrollRequest);
            scrollId = searchScrollResponse.getScrollId();
            hits = searchScrollResponse.getHits().getHits();

        }
        //及时清除es快照，释放资源
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        client.clearScroll(clearScrollRequest);
        return resultSearchHit;
    }


    /**
     * 使用游标获取结果
     * @param scrollTimeOut
     * @param index
     * @param types
     * @param sourceBuilder
     * @return
     * @throws IOException
     */
    public SearchResponse scrollSearch(String scrollId, Long scrollTimeOut, String index,
                                       String[] types, SearchSourceBuilder sourceBuilder) throws IOException {
        if (StringUtils.isNotEmpty(scrollId)){
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(new Scroll(timeValueMinutes(scrollTimeOut)));
            return client.searchScroll(searchScrollRequest);
        }else{
            // 创建查询请求对象，将查询对象配置到其中
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source(sourceBuilder);
            if (types != null && types.length > 0) {
                searchRequest.types(types);
            }
            searchRequest.scroll(new Scroll(timeValueMinutes(scrollTimeOut)));
            return client.search(searchRequest);
        }
    }

    /**
     * 及时清除es快照，释放资源
     * @param scrollId
     * @throws IOException
     */
    public void clearScroll(String scrollId) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        client.clearScroll(clearScrollRequest);
    }



    public void close() throws IOException {
        client.close();
    }

    public RestHighLevelClient getClient() {
        return client;
    }

}