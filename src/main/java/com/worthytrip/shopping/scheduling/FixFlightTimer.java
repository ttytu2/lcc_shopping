package com.worthytrip.shopping.scheduling;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.worthytrip.shopping.dao.mapper.flightdata.FixedFlightMapper;
import com.worthytrip.shopping.dao.model.flightdata.FixedFlight;
import com.worthytrip.shopping.execution.GrabHotFlightAsyncTask;
import com.worthytrip.shopping.execution.config.BaseConfig;
import com.worthytrip.shopping.execution.config.InitiativeExecutorConfig;
import com.worthytrip.shopping.service.impl.FixedFlightServiceImpl;
import com.worthytrip.shopping.util.Constants;
import com.worthytrip.shopping.util.PrivateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;


@Component
public class FixFlightTimer {

    @Autowired
    private GrabHotFlightAsyncTask grabHotFlightAsyncTask;

    @Autowired
    private FixedFlightMapper fixedFlightMapper;

    @Autowired
    FixedFlightServiceImpl fixedFlightServiceImpl;

    @Autowired
    private BaseConfig config;

    public static int grabCount = 0;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 检测NEED_GRAB_CACHE是否有数据需要爬取
     *
     * @author lishuai
     * @date 18-3-2 下午8:12
     */
    @Scheduled(cron = "${scheduling.fixed.grab.fixedflight}")
    public void checkFixedFlightCache() {

        if (config.getWebDriverSwitch() > 0) {
            long startTime = System.currentTimeMillis();
            Map<FixedFlight, Integer> grabMap = Maps.newHashMap();
            grabMap.putAll(PrivateData.NEED_GRAB_CACHE);
            Set<FixedFlight> keySet = grabMap.keySet();
            startGrab(keySet);
            long grabTime = System.currentTimeMillis() - startTime;
            logger.info("批量爬虫完成，耗时:{}", grabTime);
        }
        logger.info("批量爬虫开关状态:{}", config.getWebDriverSwitch());


    }

    /**
     * 请求从所有数据
     *
     * @author lishuai
     * @date 18-3-2 下午8:11
     */
    private void startGrab(Set<FixedFlight> fixedFlights) {
        Iterator<FixedFlight> iterator = fixedFlights.iterator();
        Set<Future<FixedFlight>> futures = Sets.newHashSet();
        logger.info("Auto WebDriver Grab 批处理开始:共有:{}个需要开始爬虫", fixedFlights.size());
        while (iterator.hasNext()) {
            FixedFlight fixedFlight = iterator.next();
            try {
                logger.debug("Auto WebDriver Grab开始请求：{}", fixedFlight);
                logger.info("Grab开始请求");
                Future<FixedFlight> future = grabHotFlightAsyncTask.grabHotFlight(fixedFlight);
                grabCount += 1;
                futures.add(future);
            } catch (InterruptedException e) {
                logger.error("Auto WebDriver Grab Error :", e);
            }
        }
        //等待所有的线程执行完成
        while (true) {
            Iterator<Future<FixedFlight>> isDoneIterator = futures.iterator();
            boolean flag = true;
            while (isDoneIterator.hasNext()) {
                if (!isDoneIterator.next().isDone()) {
                    flag = false;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        logger.info("WebDriver  Grab CallBack Error: ", e);
                    }
                    break;
                }
            }
            if (flag) {
                logger.info("Auto WebDriver Grab 批处理所有的线程执行完成...");
                break;
            }
        }
        //从NEED_GRAB_CACHE中移出所有请求数据
        Iterator<FixedFlight> removeIterator = fixedFlights.iterator();
        while (removeIterator.hasNext()) {
            FixedFlight fixedFlight = removeIterator.next();
            logger.info("Auto WebDriver Grab 移出{}", fixedFlight);
            PrivateData.NEED_GRAB_CACHE.remove(fixedFlight);
        }
        logger.info("NEED_GRAB_CACHE_WEB_DRIVER : 移出批处理FixedFlight完成");
    }

    /**
     * 检测数据库是否有更新
     *
     * @author lishuai
     * @date 18-3-2 下午8:10
     */
    @Scheduled(cron = "${scheduling.fixed.db.fixedflight}")
    public void checkNewFixFlight() {
        logger.info("检查数据库...");
        List<FixedFlight> fixedFlightList = fixedFlightMapper.queryAllFlights();
        Iterator<FixedFlight> iterator = fixedFlightList.iterator();
        while (iterator.hasNext()) {
            FixedFlight fixedFlight = iterator.next();
            if (fixedFlightServiceImpl.checkCache(fixedFlight)) {
                continue;
            }
            PrivateData.NEED_GRAB_CACHE.putIfAbsent(fixedFlight, 0);
        }
        logger.info("数据库现在有:{}个需要爬虫", PrivateData.NEED_GRAB_CACHE.size());
    }

    @Scheduled(cron = "${scheduling.fixed.multiplegrab.fixedflight}")
    public void multipleGrab() {
        Map<FixedFlight, Integer> grabMap = Maps.newHashMap();
        grabMap.putAll(PrivateData.NEED_GRAB_CACHE);
        Set<FixedFlight> fixedFlights = grabMap.keySet();
        Iterator<FixedFlight> iterator = fixedFlights.iterator();
        logger.info("Auto Multiple Grab批处理开始:共有:{}个需要开始爬虫", fixedFlights.size());

        //批量异步请求开始,初始化所存的线程信息
        InitiativeExecutorConfig.initCurrentThreadInfo();

        int count = 0;
        while (iterator.hasNext() && count < config.getMultipleGrabCount()) {
            FixedFlight fixedFlight = iterator.next();
            //检测当前fixedFlight是否在缓存中，检测当前fixedFlight是否正在爬虫,如果存在其任意一种情况则过滤该fixedFlight的多次异步请求
            if (fixedFlightServiceImpl.checkCache(fixedFlight) || fixedFlightServiceImpl.isFixedFlightGrabing(fixedFlight)) {
                continue;
            }
            logger.debug("Auto Multiple Grab开始请求：{}", fixedFlight);
            for (int i = 1; i <= config.getEverGrabCount(); i++) {
                try {
                    grabHotFlightAsyncTask.grabHotFlight(fixedFlight);
                    //该线程成功被创建的次数
                    PrivateData.NEED_GRAB_CACHE.put(fixedFlight, i);
                    //第一次成功创建线程后,将redis中该fixedFlight的状态置为1,代表已经正在爬虫中
                    if (i == 1) {
                        fixedFlightServiceImpl.changeFixedFlightGrabStatus(fixedFlight, Constants.INT_ONE);
                    }
                } catch (InterruptedException e) {
                    logger.error("Auto  Grab Run  Error :", e);
                } catch (TaskRejectedException e) {
                    //拒绝策略,该异步线程未被加入到线程池缓冲队列，且未被处理
                    logger.error("ThreadPool Rejected:{}", fixedFlight);
                }
            }
            count += 1;
        }
    }
}
