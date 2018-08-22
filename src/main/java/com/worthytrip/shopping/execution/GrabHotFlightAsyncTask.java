package com.worthytrip.shopping.execution;

import com.worthytrip.shopping.dao.model.flightdata.CacheDataModel;
import com.worthytrip.shopping.dao.model.flightdata.FixedFlight;
import com.worthytrip.shopping.execution.config.InitiativeExecutorConfig;
import com.worthytrip.shopping.service.impl.CityCodeServiceImpl;
import com.worthytrip.shopping.service.impl.CommonDataServiceImpl;
import com.worthytrip.shopping.service.impl.FixedFlightServiceImpl;
import com.worthytrip.shopping.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;


@Component
public class GrabHotFlightAsyncTask {

    @Autowired
    private CommonDataServiceImpl commonDataServiceImpl;

    @Autowired
    private CityCodeServiceImpl cityCodeServiceImpl;

    @Autowired
    private FixedFlightServiceImpl fixedFlightService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async("initiativeExecutorExcutor")
    public Future<FixedFlight> grabHotFlight(FixedFlight fixedFlight) throws InterruptedException {

        //将当前线程名push进集合中,用于验证失败返回状态码3时是否为本次批量生成的异步线程
        InitiativeExecutorConfig.pushThreadInfo(Thread.currentThread().getName());

        long start = System.currentTimeMillis();

        String startDate = DateFormat.addDay(fixedFlight.getStartdate());

        String retDate = fixedFlight.getFlightoption() == Constants.INT_ONE ? DateFormat.addDay(fixedFlight.getStartdate() + fixedFlight.getRetdate()) : Constants.NORETDATE;

        String fromCity = fixedFlight.getFromcity();

        String toCity = fixedFlight.getTocity();

        String fromAirport = cityCodeServiceImpl.getAirportCodeFromCityCode(fromCity, fixedFlight.getIpcc());

        String toAirport = cityCodeServiceImpl.getAirportCodeFromCityCode(toCity, fixedFlight.getIpcc());

        String reqJson = RoutingUtil.createJsonReq(fixedFlight, fromAirport, toAirport);

        String redisKey = RoutingUtil.createRedisKeyByEntity(fixedFlight);

        CacheDataModel cacheDataModel = RoutingUtil.createCacheDataByFixedFlight(fixedFlight, startDate, retDate);

        String blackRedisKey = fixedFlight.getFromcity() + "-" + fixedFlight.getTocity() + "-" + "black";

        try {
            commonDataServiceImpl.grabData(reqJson, redisKey, cacheDataModel, blackRedisKey, null, Constants.GrapType.AUTO.getCode());
        } catch (Exception e) {
            logger.warn("{}: Auto Grab Failed Caused :{}", redisKey, e.getMessage());

            //这里代表该请求出现未知异常，该线程的爬虫结束，减去一次爬虫次数,再判断是否需要重置redis
            fixedFlightService.setGrabCountAndFlushRedis(fixedFlight);

            return new AsyncResult<FixedFlight>(null);
        }

        //这里代表该请求完成正确的爬虫，该线程的爬虫结束，减去一次爬虫次数,再判断是否需要重置redis
        fixedFlightService.setGrabCountAndFlushRedis(fixedFlight);

        long end = System.currentTimeMillis();

        logger.debug("{} FINISHED, TIME ELAPSED: {} MS.", fixedFlight, end - start);

        return new AsyncResult<FixedFlight>(fixedFlight);
    }
}
