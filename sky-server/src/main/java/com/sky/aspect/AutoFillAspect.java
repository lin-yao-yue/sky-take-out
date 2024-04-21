package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
* 自定义切面，实现公共字段的自动填充
* */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
    * 切入点
    * @Pointcut：将公共的切点表达式抽取出来，用于后续@Around使用
    * 指定包下的所有方法 && 通过annotation(注解)指定切入点
    *   直接annotation会扫描全部，影响效率，加上execution就会只扫描mp里面带AutoFill注解的方法，提高性能
    * */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /*
    * 切入点引用
    * */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始进行公共字段的填充");
        // 获取当前被拦截的方法的数据库操作类型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();  // 向下转型，方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);  // 通过反射，获得方法上的注解对象
        OperationType operationType = autoFill.value();  // 获得注解中指定的操作类型

        // 获取当前被拦截的方法参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args==null || args.length==0) return;
        Object entity = args[0];  // 约定实体对象放在参数的第一个

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();  // 存入到ThreadLocal中的id

        // 根据不同的操作类型，为对应的操作的属性赋值
        if(operationType==OperationType.INSERT){  // 为四个公共字段赋值
            // 使用反射获得方法
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setCreateTime.invoke(entity, now);
            setCreateUser.invoke(entity, currentId);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        }
        else if(operationType==OperationType.UPDATE){  // 为两个字段赋值
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        }
    }
}









































































