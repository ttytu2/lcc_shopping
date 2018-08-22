package com.worthytrip.shopping.service;

import com.worthytrip.shopping.dao.model.flightdata.FixedFlight;

import java.util.List;

public interface IFixedFlightService {

    boolean checkCache(FixedFlight fixedFlight);

    List<FixedFlight> queryAllFlights();

    public boolean isFixedFlightGrabing(FixedFlight fixedFlight);

    public  void setGrabCountAndFlushRedis(FixedFlight fixedFlight);

    public void changeFixedFlightGrabStatus(FixedFlight fixedFlight, int status);

}
