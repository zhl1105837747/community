package com.lll;

import com.lll.dao.DiscussPostMapper;
import com.lll.dao.elasticearch.DiscussPostRepostitory;
import com.lll.entity.DiscussPost;
import com.lll.service.ElasticsearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepostitory discussPostRepostitory;

    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;


    @Test
    public void testInsert() {
        discussPostRepostitory.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepostitory.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepostitory.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepostitory.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("????????????,???????????????");
        discussPostRepostitory.save(post);
    }

    @Test
    public void testDelete() {
        // discussPostRepostitory.deleteById(231);
        discussPostRepostitory.deleteAll();
    }

    @Test
    public void testSearchByRepository() {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // ???????????????????????????????????????, ??????????????????.

        Page<DiscussPost> page = discussPostRepostitory.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate() {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = elasticTemplate.search(searchQuery, DiscussPost.class);
        // ?????????????????????????????????
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // ??????????????????????????????????????????
        List<DiscussPost> discussPosts = new ArrayList<>();
        // ?????????????????????????????????
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            // ???????????????
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // ???????????????????????????content???
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setTitle(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // ??????????????????
            discussPosts.add(searchHit.getContent());
        }
        System.out.println(discussPosts.size());
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Test
    public void test() {
        List<DiscussPost> list = elasticsearchService.searchDiscussPost("???????????????", 0, 10);
        System.out.println(list.size());
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }
    }
}

