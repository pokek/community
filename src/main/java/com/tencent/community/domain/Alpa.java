package com.tencent.community.domain;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Scope("prototype")
public class Alpa implements BeanPostProcessor {

    public Alpa() {
    }

    @PostConstruct
    public void inti(){

    }

    @PreDestroy
    public void destroy(){

    }
}
