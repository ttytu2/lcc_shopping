package com.worthytrip.shopping.service.impl;

import com.worthytrip.shopping.dao.mapper.flightdata.FixedFlightMapper;
import com.worthytrip.shopping.dao.model.flightdata.FixedFlight;
import com.worthytrip.shopping.execution.config.BaseConfig;
import com.worthytrip.shopping.service.IFixedFlightService;
import com.worthytrip.shopping.service.IRedisService;
import com.worthytrip.shopping.util.Constants;
import com.worthytrip.shopping.util.PrivateData;
import com.worthytrip.shopping.util.RoutingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FixedFlightServiceImpl implements IFixedFlightService {

    @Autowired
    private FixedFlightMapper fixedFlightMapper;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private BaseConfig config;

    @Override
    public List<FixedFlight> queryAllFlights() {
        return fixedFlightMapper.queryAllFlights();
    }

    @Override
    public boolean checkCache(FixedFlight fixedFlight) {
        return this.isExpireThanConfig(RoutingUtil.createRedisKeyByEntity(fixedFlight));
    }

    @Override
    public boolean isFixedFlightGrabing(FixedFlight fixedFlight) {

        String redisKey = RoutingUtil.createRedisKeyByEntity(fixedFlight);
        String hashValue = redisService.getHash(Constants.REDIS_FLIGHT_GRAB_STATUS, redisKey);
        return hashValue != null && String.valueOf(Constants.GrabStatusOption.GRAB_GRABING.getCode()).equals(hashValue);

    }

    @Override
    public void changeFixedFlightGrabStatus(FixedFlight fixedFlight, int status) {
        String redisKey = RoutingUtil.createRedisKeyByEntity(fixedFlight);
        redisService.setHash(Constants.REDIS_FLIGHT_GRAB_STATUS, redisKey, String.valueOf(status));
    }

    @Override
    public synchronized void setGrabCountAndFlushRedis(FixedFlight fixedFlight) {
        int grabCount = PrivateData.NEED_GRAB_CACHE.get(fixedFlight);
        if (grabCount > Constants.INT_ZERO) {
            PrivateData.NEED_GRAB_CACHE.put(fixedFlight, grabCount - 1);
            //判断该fixedFlight生成的所有异步线程是否全部完成,然后将redis中该fixedFlight的状态设为0,并且在Map中删除fixedFlight
            if ((grabCount - 1) <= Constants.INT_ZERO) {
                PrivateData.NEED_GRAB_CACHE.remove(fixedFlight);
                this.changeFixedFlightGrabStatus(fixedFlight, Constants.INT_ZERO);
            }
        }
    }


    private boolean isExpireThanConfig(String redisKey) {
        long surplusTime = redisService.getExpire(redisKey);
        return surplusTime > config.getCacheSurplusTime();
    }


}
