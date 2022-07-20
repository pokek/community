package com.tencent.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AlphaConfig {


    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("YYYY-MM-dd");
    }
}
