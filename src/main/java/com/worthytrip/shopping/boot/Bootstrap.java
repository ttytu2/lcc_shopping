package com.worthytrip.shopping.boot;

import com.worthytrip.commons.http.client.manager.HttpClientPoolManager;
import com.worthytrip.shopping.dao.model.codedata.CityCode;
import com.worthytrip.shopping.execution.config.BaseConfig;
import com.worthytrip.shopping.service.IRedisService;
import com.worthytrip.shopping.service.impl.CityCodeServiceImpl;
import com.worthytrip.shopping.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yuzhigang on 3/6/2018 10:15 PM.
 * @version 1.0
 * Description:
 */
@Component
public class Bootstrap implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Autowired
    private CityCodeServiceImpl cityCodeServiceImpl;
    @Autowired
    private IRedisService iRedisService;

    @Autowired
    private BaseConfig config;

    /**
     * Do initialization task here. The method will be invoked in the main thread
     * just after applicationcontext is created and before springboot application startup
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Your application started with option names : {}", args.getOptionNames());
        HttpClientPoolManager.init(Integer.valueOf(config.getHttpmaxTotal()), Integer.valueOf(config.getHttpMaxPerRoute()));
        List<CityCode> cityCodeList = cityCodeServiceImpl.queryAllCityCode();
        for (CityCode city : cityCodeList) {
            String airport = city.getAirportcode();
            String cityCode = city.getCitycode();
            iRedisService.set("airportCode_" + airport, cityCode);
            if (iRedisService.get("cityCode_" + cityCode) == null) {
                iRedisService.set("cityCode_" + cityCode, airport);
            } else {
                String airportCode = iRedisService.get("cityCode_" + cityCode).toString();
                if (!airportCode.contains(city.getAirportcode())) {
                    airportCode = airportCode + "," + airport;
                    iRedisService.set("cityCode_" + cityCode, airportCode);
                }
            }

        }
        //程序启动移出redis航线状态码
        iRedisService.removeKey(Constants.REDIS_FLIGHT_GRAB_STATUS);
    }
}

