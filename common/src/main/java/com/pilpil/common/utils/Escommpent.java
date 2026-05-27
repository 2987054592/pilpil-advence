package com.pilpil.common.utils;

import com.alibaba.fastjson.JSON;
import com.pilpil.common.entity.dto.VideoReview;
import com.pilpil.common.entity.dto.queryVideo;
import com.pilpil.common.entity.po.VideoDoc;
import com.pilpil.common.entity.vo.VideoDocVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Escommpent {
    private final RestHighLevelClient client;
    private static final String INDEX_NAME = "video_search";

    // 保存
    public void save(VideoDoc videoDoc){

        try {
            IndexRequest request = new IndexRequest(INDEX_NAME);
            request.id(videoDoc.getVideoId().toString());
            request.source(JSON.toJSONString(videoDoc), XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            log.info("保存视频信息成功: {}", response.getId());
        }catch (Exception e){
            log.error("保存视频信息失败: {}", e.getMessage());
        }
    }

    // ===================== 高亮+分页查询（终极版） =====================
    public VideoDocVo searchVideo(queryVideo queryVideo, Integer pageNum, Integer pageSize) {
        List<VideoDoc> list = new ArrayList<>();
        int from = (pageNum - 1) * pageSize;

        try {
            SearchRequest request = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            String categoryId = queryVideo.getCategoryId();
            List<Integer> category=new ArrayList<>();
            if(categoryId != null){
                String[] split = categoryId.split(",");
                for (String s : split) {
                    category.add(Integer.parseInt(s));
                }
            }
            // 1. 拼接查询条件
            if (queryVideo.getName() != null && !queryVideo.getName().isBlank()) {
                boolQuery.must(QueryBuilders.matchQuery("name", queryVideo.getName()));
            }
            if (queryVideo.getCategoryId() != null) {
                boolQuery.must(QueryBuilders.termsQuery("categoryId", category));
            }
            if (queryVideo.getTags() != null && !queryVideo.getTags().isBlank()) {
                boolQuery.must(QueryBuilders.matchQuery("tags", queryVideo.getTags()));
            }

            // ===================== 核心：高亮配置（ES7.x 绝对生效） =====================
            HighlightBuilder highlight = new HighlightBuilder();
            highlight.field("name").field("tags"); // 高亮字段
            highlight.preTags("<font color='red'>"); // 红色标签（兼容所有前端）
            highlight.postTags("</font>");
            highlight.requireFieldMatch(false); // 关闭字段强制匹配
            highlight.highlightQuery(boolQuery); // 绑定查询条件（关键修复！）

            sourceBuilder.query(boolQuery);
            sourceBuilder.highlighter(highlight);
            sourceBuilder.from(from);
            sourceBuilder.size(pageSize);
            request.source(sourceBuilder);

            // 执行查询
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            long total = hits.getTotalHits().value;

            // 封装数据 + 高亮替换
            for (SearchHit hit : hits) {
                VideoDoc videoDoc = JSON.parseObject(hit.getSourceAsString(), VideoDoc.class);
                Map<String, HighlightField> highlightMap = hit.getHighlightFields();

                // 替换标题高亮
                if (highlightMap.containsKey("name")) {
                    HighlightField field = highlightMap.get("name");
                    videoDoc.setName(field.getFragments()[0].toString());
                }
                // 替换标签高亮
                if (highlightMap.containsKey("tags")) {
                    HighlightField field = highlightMap.get("tags");
                    videoDoc.setTags(field.getFragments()[0].toString());
                }

                list.add(videoDoc);
            }

            return VideoDocVo.builder()
                    .videoDocs(list)
                    .total((int) total)
                    .build();

        } catch (Exception e) {
            log.error("ES分页查询失败：", e);
            return VideoDocVo.builder().videoDocs(new ArrayList<>()).total(0).build();
        }
    }

    public void reviewVideo(VideoReview videoReview) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, videoReview.getId().toString());
            Integer code = videoReview.getStatus().getCode();
            String json="{\"status\":"+code+"}";
            updateRequest.doc(json, XContentType.JSON);
            UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
            log.info("更新es数据成功：{}", update.getId());

        } catch (IOException e) {
            log.error("更新es数据异常：", e);
        }

    }
    public void deleteVideo(Integer videoId){
        try{
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, videoId.toString());
            DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("删除es数据成功：{}", delete.getId());

        }catch (Exception e){
            log.error("删除视频信息异常：", e);
        }
    }
}