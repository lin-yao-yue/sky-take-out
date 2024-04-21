package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 自定义注解，用于标识某个方法需要进行aop(功能字段自动填充)
* */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 数据库操作类型，update insert
    OperationType value();  // 如果属性设置为value，则使用的时候value名可以省略

}









































