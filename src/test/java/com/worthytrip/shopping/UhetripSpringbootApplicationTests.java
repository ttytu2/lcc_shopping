package com.worthytrip.shopping;

import com.worthytrip.shopping.service.impl.IPCCQueueServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UhetripSpringbootApplicationTests {
    @Autowired
    IPCCQueueServiceImpl ipccQueueService;

    @Test
    public void contextLoads() {

        System.out.println(ipccQueueService.FLIGHT_CONFIG.getIntValue("TGAU_F"));
    }

}
