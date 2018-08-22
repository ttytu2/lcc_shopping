package com.worthytrip.shopping.execution.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class BaseConfig {

    @Value("${lcc.validTime}")
    private String validTime;

    @Value("${lcc.grapUrl}")
    private String grapUrl;

    @Value("${lcc.parser}")
    private String parser;

    @Value("${lcc.browser}")
    private String browser;

    @Value("${lcc.entry}")
    private String entry;

    @Value("${lcc.timeout}")
    private String timeout;

    @Value("${lcc.httpmaxTotal}")
    private String httpmaxTotal;

    @Value("${lcc.httpMaxPerRoute}")
    private String httpMaxPerRoute;

    @Value("${lcc.timeOutForBooking}")
    private String timeOutForBooking;

    @Value("${lcc.timeOutForCheckPrice}")
    private String timeOutForCheckPrice;

    @Value("${lcc.priceLimit}")
    private String priceLimit;

    @Value("${lcc.everGrabCount}")
    private int everGrabCount;

    @Value("${lcc.redisDeadLine}")
    private Integer redisDeadLine;

    @Value("${lcc.cacheTimeOut}")
    private int cacheTimeOut;

    @Value("${lcc.switchProxyUrl}")
    private String switchProxyUrl;

    @Value("${lcc.webDriverSwitch}")
    private int webDriverSwitch;

    @Value("${lcc.multipleGrabCount}")
    private int multipleGrabCount;

    @Value("${lcc.waitProxySwitchTime}")
    private int waitProxySwitchTime;

    @Value("${lcc.failedCount}")
    private int failedCount;

    @Value("${lcc.orderPassengerLimit}")
    private int orderPassengerLimit;

    @Value("${lcc.ipccFilterSeats}")
    private String ipccFilterSeats;

    @Value("${lcc.cacheSurplusTime}")
    private int cacheSurplusTime;

    @Value("${lcc.queryTimeOut}")
    private int queryTimeOut;

    @Value("${lcc.ipccFilterDays}")
    private String ipccFilterDays;

    public int getIpccFilterDays(String ipcc) {
        return JSONObject.parseObject(ipccFilterDays.replaceAll("/", "")).getIntValue(ipcc);
    }

    public void setIpccFilterDays(String ipccFilterDays) {
        this.ipccFilterDays = ipccFilterDays;
    }

    public int getQueryTimeOut() {
        return queryTimeOut;
    }

    public void setQueryTimeOut(int queryTimeOut) {
        this.queryTimeOut = queryTimeOut;
    }


    public int getCacheSurplusTime() {
        return cacheSurplusTime;
    }

    public void setCacheSurplusTime(int cacheSurplusTime) {
        this.cacheSurplusTime = cacheSurplusTime;
    }

    public int getEverGrabCount() {
        return everGrabCount;
    }

    public void setEverGrabCount(int everGrabCount) {
        this.everGrabCount = everGrabCount;
    }

    public int getOrderPassengerLimit() {
        return orderPassengerLimit;
    }

    public void setOrderPassengerLimit(int orderPassengerLimit) {
        this.orderPassengerLimit = orderPassengerLimit;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getWaitProxySwitchTime() {
        return waitProxySwitchTime;
    }

    public void setWaitProxySwitchTime(int waitProxySwitchTime) {
        this.waitProxySwitchTime = waitProxySwitchTime;
    }

    public int getMultipleGrabCount() {
        return multipleGrabCount;
    }

    public void setMultipleGrabCount(int multipleGrabCount) {
        this.multipleGrabCount = multipleGrabCount;
    }

    public int getWebDriverSwitch() {
        return webDriverSwitch;
    }

    public void setWebDriverSwitch(int webDriverSwitch) {
        this.webDriverSwitch = webDriverSwitch;
    }

    public String getSwitchProxyUrl() {
        return switchProxyUrl;
    }

    public void setSwitchProxyUrl(String switchProxyUrl) {
        this.switchProxyUrl = switchProxyUrl;
    }

    public int getCacheTimeOut() {
        return cacheTimeOut;
    }

    public void setCacheTimeOut(int cacheTimeOut) {
        this.cacheTimeOut = cacheTimeOut;
    }


    public int getIpccFilterSeats(String ipcc) {
        return JSONObject.parseObject(ipccFilterSeats.replaceAll("/", "")).getIntValue(ipcc);
    }

    public void setIpccFilterSeats(String ipccFilterSeats) {
        this.ipccFilterSeats = ipccFilterSeats;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getGrapUrl() {
        return grapUrl;
    }

    public void setGrapUrl(String grapUrl) {
        this.grapUrl = grapUrl;
    }

    public String getParser() {
        return parser;
    }

    public void setParser(String parser) {
        this.parser = parser;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getHttpmaxTotal() {
        return httpmaxTotal;
    }

    public void setHttpmaxTotal(String httpmaxTotal) {
        this.httpmaxTotal = httpmaxTotal;
    }

    public String getHttpMaxPerRoute() {
        return httpMaxPerRoute;
    }

    public void setHttpMaxPerRoute(String httpMaxPerRoute) {
        this.httpMaxPerRoute = httpMaxPerRoute;
    }

    public String getTimeOutForBooking() {
        return timeOutForBooking;
    }

    public void setTimeOutForBooking(String timeOutForBooking) {
        this.timeOutForBooking = timeOutForBooking;
    }

    public String getTimeOutForCheckPrice() {
        return timeOutForCheckPrice;
    }

    public void setTimeOutForCheckPrice(String timeOutForCheckPrice) {
        this.timeOutForCheckPrice = timeOutForCheckPrice;
    }

    public String getPriceLimit() {
        return priceLimit;
    }

    public void setPriceLimit(String priceLimit) {
        this.priceLimit = priceLimit;
    }

    public Integer getRedisDeadLine() {
        return redisDeadLine;
    }

    public void setRedisDeadLine(Integer redisDeadLine) {
        this.redisDeadLine = redisDeadLine;
    }
}
