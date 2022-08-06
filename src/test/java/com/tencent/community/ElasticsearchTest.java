package com.tencent.community;

import com.alibaba.fastjson.JSONObject;
import com.tencent.community.dao.PostMapper;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.elasticsearch.DiscussPostRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class ElasticsearchTest {

    @Autowired
    PostMapper postMapper;

    @Autowired
    DiscussPostRepository discussPostRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Test
    public void insertPostTest(){
        discussPostRepository.save(postMapper.selectPostById(241));
        discussPostRepository.save(postMapper.selectPostById(242));
        discussPostRepository.save(postMapper.selectPostById(243));
    }

    @Test
    public void insertPostAllTest(){
        discussPostRepository.saveAll(postMapper.discussPostByUserId(101, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(102, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(103, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(111, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(112, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(131, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(132, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(133, 0, 100, 0));
        discussPostRepository.saveAll(postMapper.discussPostByUserId(134, 0, 100, 0));
    }

    @Test
    public void deleteOne(){
        discussPostRepository.deleteById(241);
    }

    @Test
    public void deleteAll(){
        discussPostRepository.deleteAll();
    }

    @Test
    public void update(){
        DiscussPost discussPost = postMapper.selectPostById(241);
        discussPost.setContent("你很卷啊，");
        discussPostRepository.save(discussPost);
    }

    @Test
    public void serachPost() throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");
        // 构造条件器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0).size(10)
                .highlighter(
                        new HighlightBuilder().field("title").preTags("<em>").postTags("</em>")
                                .field("content").preTags("<em>").postTags("</em>")
                );
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
    }
}
