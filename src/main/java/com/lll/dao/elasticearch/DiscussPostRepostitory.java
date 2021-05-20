package com.lll.dao.elasticearch;

import com.lll.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepostitory extends ElasticsearchRepository<DiscussPost, Integer> {

    
}
