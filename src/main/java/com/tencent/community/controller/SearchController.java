package com.tencent.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import com.tencent.community.domain.DiscussPost;
import com.tencent.community.domain.Page;
import com.tencent.community.service.ElasticsearchService;
import com.tencent.community.service.LikeService;
import com.tencent.community.service.UserService;
import com.tencent.community.util.CommunityConstant;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    UserService us;

    @Autowired
    LikeService likeService;

    @GetMapping("/search")
    public String search(Model model, String keyword, Page page){
        page.setPath("/search?keyword=" + keyword);
        page.setRows(elasticsearchService.searchCount(keyword));
        SearchHit[] searchHits = elasticsearchService.searchEngine(keyword, page.getStart(), page.getLimit());
        List<Map<String, Object>> res = new ArrayList<>();
        // 聚合数据
        if(searchHits != null){
            for(SearchHit hit : searchHits){
                DiscussPost post = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
                Map<String, Object> searchVO = new HashMap<>();
                searchVO.put("post", post);
                searchVO.put("user", us.findUserById(Integer.parseInt(post.getUserId())));
                searchVO.put("likeCount", likeService.likeCount(post.getId(), ENTITY_TYPE_COMMENT));
                res.add(searchVO);
            }
        }

        model.addAttribute("discussPosts", res);
        model.addAttribute("keyword", keyword);
        return "/site/search";
    }
}
