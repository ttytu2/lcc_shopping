package com.worthytrip.shopping.service;

import com.alibaba.fastjson.JSONObject;
import com.worthytrip.shopping.dao.model.flightdata.RequstCheckPrice;

/**
 * @author lishuai
 * @date 18-2-28 下午5:57
 */
public interface ICheckPriceService {

     JSONObject checkPrice(RequstCheckPrice requst);

}
