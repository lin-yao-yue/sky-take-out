package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
* 自定义定时任务类
* */
//@Component  // 取消定时任务就注释掉
@Slf4j
public class MyTask {

    /*
    * 定时任务
    * */
    @Scheduled(cron = "0/5 * * * * ?")
    public void test(){
        log.info("定时任务开始执行：{}",new Date());
    }
}






















































































