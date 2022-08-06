package com.tencent.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo("127.0.0.1:9200")
                .build();
        RestHighLevelClient client = RestClients.create(configuration).rest();
        return client;
    }
}
