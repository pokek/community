package com.tencent.community.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;

// 配置es储存信息，生成索引（表），文档（记录）
@Document(indexName = "discusspost")
// shards  分区  replicas 备份几份
@Setting(shards = 6, replicas = 3)
@Data
@NoArgsConstructor
public class DiscussPost {

    @Id
    private Integer id;

    @Field(type = FieldType.Text)
    private String userId;

    // analyzer 储存时 把句子拆分最大词条，增加搜索范围，查找时 找出符合意境的词条
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
    /**
     * '0-普通; 1-置顶;'
     */
    @Field(type = FieldType.Integer)
    private Integer type = 0;
    /**
     * '0-正常; 1-精华; 2-拉黑;'
     */
    @Field(type = FieldType.Integer)
    private Integer status = 0;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private Integer commentCount = 0;

    @Field(type = FieldType.Double)
    private Double score = 0.0;
}
