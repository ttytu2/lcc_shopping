package com.worthytrip.shopping.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.worthytrip.shopping.dao.model.flightdata.CacheDataModel;
import com.worthytrip.shopping.dao.model.flightdata.OrderPnr;
import com.worthytrip.shopping.dao.model.flightdata.RequestCreateOrderBean;
import com.worthytrip.shopping.dao.model.flightdata.RequestCreateOrderBean.FromSegmentsBean;
import com.worthytrip.shopping.dao.model.flightdata.RequestCreateOrderBean.RetSegmentsBean;
import com.worthytrip.shopping.execution.config.BaseConfig;
import com.worthytrip.shopping.service.ICommonDataService;
import com.worthytrip.shopping.service.ICreateOrderService;
import com.worthytrip.shopping.service.IOrderPnrService;
import com.worthytrip.shopping.service.IRedisService;
import com.worthytrip.shopping.util.Constants;
import com.worthytrip.shopping.util.RoutingUtil;
import com.worthytrip.shopping.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreateOrderServiceImpl implements ICreateOrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICommonDataService commonDataService;
    @Autowired
    private IOrderPnrService orderPnrService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private BaseConfig config;


    @Override
    public JSONObject createOeder(RequestCreateOrderBean orderBean) {
        JSONObject result = new JSONObject();
        String message = Constants.SUCCESS;
        int status = 1;

        //检查生单人数
        if (!this.checkPassengerCount(orderBean)) {
            status = 0;
            message = "人数过多";
            result.put("status", status);
            result.put("message", message);
            return result;
        }

        String flightOption = orderBean.getFlightOption();
        String depAirport = orderBean.getFromSegments().get(0).getDepAirport();
        String arrAirport = orderBean.getFromSegments().get(orderBean.getFromSegments().size() - 1).getArrAirport();
        String startDate = orderBean.getStartTime();
        String retDate = Constants.NORETDATE;
        // 用于指向抓取的所有航线中与生单请求对应的那个航线
        JSONObject theFlight = null;
        // 往返类型才会有retgoTime
        if (Constants.FlightOption.ROUND_TRIP.getValue().equals(flightOption)) {
            retDate = orderBean.getEndTime();
        }
        int adultNumber = Integer.valueOf(orderBean.getAdultNumber());
        int childNumber = Integer.valueOf(orderBean.getChildNumber());
        if (StringUtil.isNullOrBlank(flightOption) || StringUtil.isNullOrBlank(depAirport) || StringUtil.isNullOrBlank(
                arrAirport) || StringUtil.isNullOrBlank(startDate) || StringUtil.isNullOrBlank(retDate) || adultNumber < 1) {
            logger.error("Create Order :请求参数错误");
            status = 0;
            message = "参数错误";
        }
        if (StringUtil.checkCardExpireDate(orderBean)) {
            logger.error("证件有效期不足半年");
            status = 0;
            message = "生单失败";
            logger.info("生单失败");
        } else {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(Constants.CID, Constants.CID_VALUE);
            jsonRequest.put(Constants.FROMCITY, depAirport);
            jsonRequest.put(Constants.TOCITY, arrAirport);
            jsonRequest.put(Constants.STARTDATE, startDate);
            jsonRequest.put(Constants.RETDATE, retDate);
            jsonRequest.put(Constants.FLIGHTOPTION, flightOption);
            jsonRequest.put(Constants.ADULTNUMBER, adultNumber);
            jsonRequest.put(Constants.CHILDNUMBER, childNumber);
            jsonRequest.put(Constants.DS, Constants.DS_VALUE);
            jsonRequest.put(Constants.IPCC, orderBean.getIpcc());
            jsonRequest.put(Constants.PARSER, config.getParser());
            jsonRequest.put(Constants.BROWSER, config.getBrowser());
            // 拼接抓取数据的请求json
            jsonRequest.put(Constants.ENTRY, config.getEntry());
            String redisKeyBlack = depAirport + "-" + arrAirport + "-" + "black";
            String json = jsonRequest.toString();
            String redisKey = RoutingUtil.createRedisKeyByEntity(orderBean);
            // 抓取数据顺便存储在数据库以及redis
            CacheDataModel cacheDataModel = new CacheDataModel();
            cacheDataModel.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            String refreshTime = String.valueOf(System.currentTimeMillis());
            cacheDataModel.setAdultNumber(adultNumber);
            cacheDataModel.setChildNumber(childNumber);
            cacheDataModel.setFlightOption(flightOption);
            cacheDataModel.setFromCity(depAirport);
            cacheDataModel.setRefreshTime(refreshTime);
            cacheDataModel.setRetDate(retDate);
            cacheDataModel.setStartDate(startDate);
            cacheDataModel.setToCity(arrAirport);
            cacheDataModel.setIpcc(orderBean.getIpcc());
            String resultJson = null;
            String pnrCode = orderPnrService.getUniquePnrCode();
            OrderPnr orderPnr = new OrderPnr();
            orderPnr.setAdultnumber(adultNumber);
            orderPnr.setChildnumber(childNumber);
            orderPnr.setCreatetime(StringUtil.getNow());
            orderPnr.setPnrcode(pnrCode);
            orderPnr.setSessionid(orderBean.getSessionId());
            orderPnr.setFromCity(orderBean.getFromCity());
            orderPnr.setToCity(orderBean.getToCity());
            orderPnr.setIpcc(orderBean.getIpcc());

            try {
                // 调用爬虫抓取数据
                resultJson = commonDataService.grabData(json, redisKey, cacheDataModel, redisKeyBlack, Integer
                        .valueOf(config.getTimeOutForBooking()), Constants.GrapType.ORDER.getCode());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            logger.debug("Order Grab ResultJson：" + resultJson);
            // 抓取強制8S超時从缓存获取数据
            if (resultJson == null) {
                // 从缓存中取值
                CacheDataModel cacheDataModelRedis = (CacheDataModel) redisService.get(redisKey);
                if (cacheDataModelRedis == null) {
                    message = "生单失败";
                    status = 0;
                    logger.info("Create Order Error:实时爬虫超时且缓存没有航线数据");
                } else {
                    resultJson = cacheDataModelRedis.getResultJson();
                    logger.info("Create Order Success：从缓存中或取数据成功");
                    int seatsRemain = 0;
                    List<JSONObject> flightObjectList = StringUtil.getJSONObjectListFromArrayStr(resultJson);
                    if (flightObjectList.size() > 0) {
                        for (JSONObject routing : flightObjectList) {
                            boolean fromFlag = true;
                            List<JSONObject> fromlineObjectList = StringUtil.getJSONObjectListFromArrayStr(routing.get(
                                    Constants.FROMSEGMENTS).toString());
                            // 比较去程的航班信息 // 航段相同
                            if (orderBean.getFromSegments().size() == fromlineObjectList.size()) {
                                for (int i = 0; i < orderBean.getFromSegments().size(); i++) {
                                    // 比较航班号
                                    if (!orderBean.getFromSegments().get(i).getFlightNumber().equals(
                                            fromlineObjectList.get(i).getString(Constants.FLIGHTNUMBER))) {
                                        fromFlag = false;
                                    }
                                }
                                if (fromFlag) {
                                    theFlight = routing;
                                }

                            }
                            // 判断是否有回程，并比较信息确定最终的航线
                            if (orderBean.getRetSegments() != null && fromFlag && orderBean.getRetSegments().size() > 0) {
                                List<JSONObject> backLineObjectList = StringUtil.getJSONObjectListFromArrayStr(routing
                                        .get(Constants.RETSEGMENTS).toString());
                                // 航段相同
                                if (orderBean.getRetSegments().size() == backLineObjectList.size()) {
                                    boolean retFlag = true;
                                    for (int i = 0; i < orderBean.getRetSegments().size(); i++) {
                                        if (!orderBean.getRetSegments().get(i).getFlightNumber().equals(
                                                backLineObjectList.get(i).getString(Constants.FLIGHTNUMBER))) {
                                            retFlag = false;
                                        }
                                    }
                                    if (retFlag) {
                                        theFlight = routing;
                                    }

                                }
                            }

                        }
                    } else {
                        // 无航班时 刷新时间延长
                        seatsRemain = 100;
                    }
                    // 为空表示没有匹配的航线了，代表之前存在的航线现在没有座位已经无该航班,100为特殊值说明无航班
                    if (theFlight != null && seatsRemain != 100) {
                        String cabins = "";
                        String flightTimes = "";
                        for (FromSegmentsBean from : orderBean.getFromSegments()) {
                            cabins += from.getCabin() + ",";
                            flightTimes += from.getDepTime() + "/" + from.getArrTime() + ",";
                        }
                        if (Constants.FlightOption.ROUND_TRIP.getValue().equals(orderBean.getFlightOption())) {
                            for (RetSegmentsBean ret : orderBean.getRetSegments()) {
                                cabins += ret.getCabin() + ",";
                                flightTimes += ret.getDepTime() + "/" + ret.getArrTime() + ",";
                            }
                        }
                        flightTimes = flightTimes.substring(0, flightTimes.length() - 1);
                        result.put("cabins", cabins.substring(0, cabins.length() - 1));
                        result.put("ds", orderBean.getDs());
                        result.put("flightTimes", flightTimes);
                        result.put("ipcc", orderBean.getIpcc());
                        result.put("lastTicketDate", "");
                        result.put("pnrCode", pnrCode);
                        result.put("pnrId", "");
                        result.put("specialAdultType", orderBean.getSpecialAdultType());
                        result.put("priceInfos", theFlight.get("priceInfos"));
                        orderPnrService.saveOrderPnr(orderPnr);
                    } else {
                        message = "已无该航航线";
                        status = 0;
                    }
                }
            } else {
                logger.info("Create Order Success：实时爬取数据成功");
                int seatsRemain = 0;
                List<JSONObject> flightObjectList = StringUtil.getJSONObjectListFromArrayStr(resultJson);
                if (flightObjectList.size() > 0) {
                    for (JSONObject routing : flightObjectList) {
                        boolean fromFlag = true;
                        List<JSONObject> fromLineObjectList = StringUtil.getJSONObjectListFromArrayStr(routing.get(
                                Constants.FROMSEGMENTS).toString());
                        // 比较去程的航班信息 / 航段相同
                        if (orderBean.getFromSegments().size() == fromLineObjectList.size()) {
                            for (int i = 0; i < orderBean.getFromSegments().size(); i++) {
                                // 比较航班号
                                if (!orderBean.getFromSegments().get(i).getFlightNumber().equals(fromLineObjectList
                                        .get(i).getString(Constants.FLIGHTNUMBER))) {
                                    fromFlag = false;
                                }
                            }
                            if (fromFlag) {
                                theFlight = routing;
                            }
                        }
                        // 判断是否有回程，并比较信息确定最终的航线
                        if (orderBean.getRetSegments() != null && orderBean.getRetSegments().size() > 0 && fromFlag) {
                            List<JSONObject> backlineObjectList = StringUtil.getJSONObjectListFromArrayStr(routing.get(
                                    Constants.RETSEGMENTS).toString());
                            // 航段相同
                            if (orderBean.getRetSegments().size() == backlineObjectList.size()) {
                                boolean retflag = true;
                                for (int i = 0; i < orderBean.getRetSegments().size(); i++) {
                                    if (!orderBean.getRetSegments().get(i).getFlightNumber().equals(backlineObjectList
                                            .get(i).getString(Constants.FLIGHTNUMBER))) {
                                        retflag = false;
                                    }
                                }
                                if (retflag) {
                                    theFlight = routing;
                                }
                            }
                        }
                    }
                } else {
                    // 无航班时 刷新时间延长
                    seatsRemain = 100;
                }
                // 为空表示没有匹配的航线了，代表之前存在的航线现在没有座位已经无该航班,100为特殊值说明无航班
                if (theFlight != null && seatsRemain != 100) {
                    String cabins = "";
                    String flightTimes = "";
                    for (FromSegmentsBean from : orderBean.getFromSegments()) {
                        cabins += from.getCabin() + ",";
                        flightTimes += from.getDepTime() + "/" + from.getArrTime() + ",";
                    }
                    if (Constants.FlightOption.ROUND_TRIP.getValue().equals(orderBean.getFlightOption())) {
                        for (RetSegmentsBean ret : orderBean.getRetSegments()) {
                            cabins += ret.getCabin() + ",";
                            flightTimes += ret.getDepTime() + "/" + ret.getArrTime() + ",";
                        }
                    }
                    flightTimes = flightTimes.substring(0, flightTimes.length() - 1);
                    result.put("cabins", cabins.substring(0, cabins.length() - 1));
                    result.put("ds", orderBean.getDs());
                    result.put("flightTimes", flightTimes);
                    result.put("ipcc", orderBean.getIpcc());
                    result.put("lastTicketDate", "");
                    result.put("pnrCode", pnrCode);
                    result.put("pnrId", "");
                    result.put("specialAdultType", orderBean.getSpecialAdultType());
                    result.put("priceInfos", theFlight.get("priceInfos"));
                    orderPnrService.saveOrderPnr(orderPnr);
                } else {
                    message = "已无该航航线";
                    status = 0;
                }
            }
        }
        result.put("message", message);
        result.put("status", status);
        return result;
    }

    @Override
    public boolean checkOrderExistence(RequestCreateOrderBean requestbody) {

        String str = orderRequestStringSplicing(requestbody);

        if (redisService.isExist(str)) {
            logger.info("订单已存在:{}", str);
            return true;
        } else {
            logger.info("订单不存在");

            return false;
        }
    }

    @Override
    public String orderRequestStringSplicing(RequestCreateOrderBean requestbody) {
        StringBuilder str = new StringBuilder();

        String formFlightNumber = obtainFormSegmentsFlightNumbers(requestbody.getFromSegments());
        str.append(requestbody.getFromCity()).append("_").append(requestbody.getToCity()).append(",").append(requestbody
                .getStartTime());
        if (!StringUtil.isNullOrBlank(requestbody.getEndTime())) {
            str.append("_").append(requestbody.getEndTime()).append(",").append(formFlightNumber);
        } else {
            str.append("_").append("").append(",").append(formFlightNumber);
        }

        logger.debug("单程String字符串拼接:{}", str);

        if (!StringUtil.isNullOrBlank(requestbody.getFlightOption()) && requestbody.getFlightOption().equalsIgnoreCase(
                "roundTrip")) {
            String retFlightNumber = obtainRetSegmentsFlightNumbers(requestbody.getRetSegments());
            str.append(";").append(requestbody.getFromCity()).append("_").append(requestbody.getToCity()).append(",")
                    .append(requestbody.getStartTime()).append("_").append(requestbody.getEndTime()).append(",").append(
                    retFlightNumber);

            logger.debug("返程String字符串拼接:{}", str);
        }
        logger.info("拼接的key值:{}", str);

        return str.toString();
    }

    private String obtainRetSegmentsFlightNumbers(List<RetSegmentsBean> flightSegments) {

        StringBuilder segmentFlightNumber = new StringBuilder();
        for (RetSegmentsBean retSegmentsBean : flightSegments) {
            segmentFlightNumber.append(retSegmentsBean.getFlightNumber()).append("_");
        }
        logger.debug("拼接的返程航班号:{}", segmentFlightNumber);
        return segmentFlightNumber.deleteCharAt(segmentFlightNumber.length() - 1).toString();
    }

    private String obtainFormSegmentsFlightNumbers(List<FromSegmentsBean> flightSegments) {
        StringBuilder segmentFlightNumber = new StringBuilder();
        for (FromSegmentsBean formSegmentsBean : flightSegments) {
            segmentFlightNumber.append(formSegmentsBean.getFlightNumber()).append("_");
        }
        logger.debug("拼接的去程航班号:{}", segmentFlightNumber);
        return segmentFlightNumber.deleteCharAt(segmentFlightNumber.length() - 1).toString();
    }

    @Override
    public void keepOrderKeyRedis(String str, RequestCreateOrderBean requestbody) {
        JSONObject json = (JSONObject) JSON.toJSON(requestbody);
        String reqStr = json.toString();
        redisService.set(str, reqStr, config.getRedisDeadLine());

    }


    private boolean checkPassengerCount(RequestCreateOrderBean requestbody) {

        int adultNumber = Integer.parseInt(requestbody.getAdultNumber());

        int childNumber = Integer.parseInt(requestbody.getChildNumber());

        return adultNumber + childNumber <= config.getOrderPassengerLimit();

    }


}
