package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* 配置类，用于创建 AliOssUtil 对象
* */
@Configuration
@Slf4j
public class OssConfiguration {
    /*
    * 由于 AliOssProperties 有@Component注解，所以已经交给了IOC容器管理
    * 而当前类 OssConfiguration 是被@Configuration标记的配置类，
    *   所以参数 AliOssProperties aliOssProperties 会被自动注入
    * */
    @Bean  // 创建 AliOssUtil 的bean对象，交给ioc管理
    @ConditionalOnMissingBean  // 避免创建多个 AliOssUtil 的 bean 对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("创建阿里云上传工具对象：{}", aliOssProperties);
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}
