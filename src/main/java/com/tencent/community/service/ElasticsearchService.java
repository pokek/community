package com.tencent.community.service;

import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.elasticsearch.DiscussPostRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticsearchService {

    @Autowired
    DiscussPostRepository discussPostRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    public void addPostToEngine(DiscussPost post){
        discussPostRepository.save(post);
    }

    public void deletePostFromEngine(int postId){
        discussPostRepository.deleteById(postId);
    }

    public SearchHit[] searchEngine(String keyWords, int offset, int limit){
        SearchRequest searchRequest = new SearchRequest("discusspost");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyWords, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .highlighter(
                        new HighlightBuilder().field("title").preTags("<em>").postTags("</em>")
                                .field("content").preTags("<em>").postTags("</em>")
                )
                .from(offset)
                .size(limit);

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return search.getHits().getHits();
    }

    public int searchCount(String keyWords){
        SearchRequest searchRequest = new SearchRequest("discusspost");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // size 返回的条数限制
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyWords, "title", "content"))
                .size(1000);
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return search.getHits().getHits().length;
    }


}
