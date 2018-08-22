package com.worthytrip.shopping.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.worthytrip.shopping.dao.model.flightdata.FixedFlight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IPCCQueueServiceImpl {

    public int config = 20;

    public int config9C = 20;

    public int configTGAU = 50;

    public static JSONObject FLIGHT_CONFIG;

    @Value("${lcc.flightCount}")
    public static void setFlightConfig(String flightConfig) {
        FLIGHT_CONFIG = JSONObject.parseObject(flightConfig);
    }

    ConcurrentHashMap<String, Queue<FixedFlight>> ipccQueryQueue = new ConcurrentHashMap<>();

    Queue<FixedFlight> cacheQueue = new LinkedList<>();

    Queue<FixedFlight> grabQueue = new LinkedList<>();


    public FixedFlight getNextFixedFlight() {
        if (grabQueue.size() == 0) {

        }
        return grabQueue.poll();
    }

    public void addFixedFlightToQueryQueue(FixedFlight fixedFlight) {
        String ipcc = fixedFlight.getIpcc();
        if (ipccQueryQueue.containsKey(ipcc)) {
            ipccQueryQueue.get(ipcc).offer(fixedFlight);
            return;
        }
        Queue<FixedFlight> newIpccQueue = new LinkedList<>();
        newIpccQueue.offer(fixedFlight);
        ipccQueryQueue.put(ipcc, newIpccQueue);
    }

    public void fillGrabQueue() {
        Enumeration<String> keys = ipccQueryQueue.keys();
        while (keys.hasMoreElements()) {
            String ipcc = keys.nextElement();
            int ipccFillCount = FLIGHT_CONFIG.getIntValue(ipcc);
            ipccQueryQueue.get(keys.nextElement()).poll();
        }
    }

    private int getFillCount(String ipcc) {
        return FLIGHT_CONFIG.getIntValue(ipcc);
    }

}
