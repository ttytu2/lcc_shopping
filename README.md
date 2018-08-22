# lcc_shopping(亚航抓取服务)

## V2.7.0- 2018-08-13 by 李帅

* [Features]
  1. 新增LC5J_F
* [Bugfixed]
  1. 无
* [Configuration]
  1.  修改ipccFilterSeats: 
  ```
    /{"TGAU_F":4,"9C_F":1,"LCJC_F":4,"LCPAM_F":1,"LC5J_F":1}/
  ```
  2.  修改ipccFilterDays :
   ```
    /{"TGAU_F":5,"9C_F":5,"LCJC_F":3,"LCPAM_F":1,"LC5J_F":3}/
   ```

## V2.6.0- 2018-08-07 by 李帅

* [Features]
  1. 新增PAM
* [Bugfixed]
  1. 无
* [Configuration]
  1.  修改ipccFilterSeats: 
  ```
    /{"TGAU_F":4,"9C_F":1,"LCJC_F":4,"LCPAM_F":1/}
  ```
  2.  修改ipccFilterDays :
   ```
   /{"TGAU_F":5,"9C_F":5,"LCJC_F":3,"LCPAM_F":1/}
   ```
## V2.5.0- 2018-07-27 by 李帅

* [Features]
  1. 新增ipcc过滤座位数和过滤日期配置
* [Bugfixed]
  1. 无
* [Configuration]
  1. 移出filterSeatsCount配置
  2. 新增ipccFilterSeats配置
  ```
        #过滤某条航班座位数
        ipccFilterSeats: /{"TGAU_F":4,"9C_F":1,"LCJC_F":4/}
   ```
  3. 新增ipccFilterDays配置
  ```
        #过滤查询日期
        ipccFilterDays : /{"TGAU_F":5,"9C_F":5,"LCJC_F":3/}
  ```
        

## V2.4.3- 2018-07-25 by 李帅

* [Features]
  1. 无
* [Bugfixed]
  1. 9C不能正确返回爬虫查询码
* [Configuration]
  1. 无

## V2.4.2- 2018-07-24 by 李帅

* [Features]
  1. 无
* [Bugfixed]
  1. 修复LCJC_F匹配行李额问题,按官网价格返回价格
* [Configuration]
  1. 无

## V2.4.1- 2018-07-23 by 李帅

* [Features]
  1. 无
* [Bugfixed]
  1. 修复9C机场码爬不到数据问题
* [Configuration]
  1. 无

## V2.4.0- 2018-07-11 by 李帅

* [Features]
  1. PVG修改成SHA
* [Bugfixed]
  1. 无
* [Configuration]
  1. 无

## V2.3.0- 2018-07-10 by 李帅

* [Features]
  1. 增加查询超时时间配置
* [Bugfixed]
  1. 无
* [Configuration]
  1.增加查询超时时间
  ```
  queryTimeOut: 3000
  ```

## V2.2.1- 2018-07-05 by 李帅

* [Features]
  1. 无
* [Bugfixed]
  1. 修复一个获取redisKey的bug
    
* [Configuration]
  1.无

## V2.2.0- 2018-07-05 by 李帅

* [Features]
  1. 限制生单人数
  2.移出切换代理定时器
  3.增加提前缓存配置
* [Bugfixed]
  1. 无
    
* [Configuration]
  1.缓存剩余时间启动爬虫(单位秒,这个数值必须小于cacheTimeOut数值)
     ``` 
      cacheSurplusTime: 600
     ```

## V2.1.0- 2018-07-03 by 李帅

* [Features]
  1. 限制生单人数
  2. 查询接口过滤座位数
    
* [Bugfixed]
  1. 无
    
* [Configuration]
  1. 无

## V2.0.0- 2018-06-28 by 李帅

* [Features]
  1. 优化代码
  2. redisKey加入ipcc
  3. 项目所有的机场三字码更换为城市三字码
  4. 优化日志输出
    
* [Bugfixed]
  1. 无
    
* [Configuration]
  1. lcc.cache_data新增ipcc字段
  ```
    ALTER TABLE cache_data ADD ipcc VARCHAR(10) NULL;
  ```
  2. lcc.cache_data新增关于cache_data的普通索引
  ```
    CREATE INDEX cache_data_createTime_index
    ON cache_data (createTime);
  ```
  3. lcc.fixed_flight新增爬虫状态码grabSwitch字段
  ```
  ALTER TABLE fixed_flight ADD grabSwitch INT NULL;
  ```
  4. lcc.order_pnr新增fromCity字段
  ```
   ALTER TABLE order_pnr ADD fromCity VARCHAR(3) NULL;
  ```
  5. lcc.order_pnr新增toCity字段
  ```
  ALTER TABLE order_pnr ADD toCity VARCHAR(3) NULL;
  ```
  6. lcc.order_pnr新增ipcc字段
  ```
  ALTER TABLE order_pnr ADD toCity VARCHAR(10) NULL;
  ```
  7. lcc.order_pnr新增关于createTime的普通索引
  ```
  CREATE INDEX order_pnr_createTime_index
      ON order_pnr (createTime);
  ```
## V1.14.0- 2018-06-26 by 李帅

* [Features]
  1. 9C新增生单人数限制
    
* [Bugfixed]
  1. 无
    
* [Configuration]
  1.生单人数限制 :  /data/config/lcc_shopping/config.properties
  ```
   #生单人数限制(默认2人)
   lcc.orderPassengerLimit=2
  ```

## V1.13.6- 2018-06-25 by 宗泽坤

* [Features]
  1. 9C生单增加证件有效期是否半年以上校验
    
* [Bugfixed]
  1. 无
    
* [Configuration]
  1. 无

## V1.13.5- 2018-06-22 by 李帅

* [Features]
  1. 无
    
* [Bugfixed]
  1. 修复9C价格校验返回行李额数据错误
    
* [Configuration]
  1. 无

## V1.13.4- 2018-06-22 by 李帅

* [Features]
  1. 无
    
* [Bugfixed]
  1. 修复9C价格校验返回行李额为空问题
    
* [Configuration]
  1. 无

## V1.13.3- 2018-06-22 by 李帅

* [Features]
  1. 无
    
* [Bugfixed]
  1. 修复9C价格校验返回行李额问题
    
* [Configuration]
  1. 无

## V1.13.2- 2018-06-14 by 李帅

* [Features]
  1. 无
    
* [Bugfixed]
  1. 修复行李额未空
    
* [Configuration]
  1. 无

## V1.13.1- 2018-06-12 by 李帅

* [Features]
  1. 无
    
* [Bugfixed]
  1. 修复9C和亚航机场三字码冲突问题
    
* [Configuration]
  1. 无

## V1.13.0- 2018-06-12 by 李帅

* [Features]
  1. 新增9C模块
    
* [Bugfixed]
  1. 无
    
* [Configuration]
  1. 无

## V1.12.1- 2018-06-05 by 魏云鹏

* [Features]
  1. 添加生单前校验redis中是否存在
    
* [Bugfixed]
  1. 无
    
* [Configuration]
  1. 存入redis中的过期时间
  ```
  lcc.redisDeadLine=1800
  ```

## V1.12.0- 2018-05-09 by 魏云鹏

* [Features]
  1. 添加验价校验，生单校验，支付校验接口的logger记录(记录每次的请求数据)
  
* [Bugfixed]
  1. 修复日志记录not found找不到routings的Bug
  
* [Configuration]
  1. 无
  
## V1.11.0- 2018-05-08 by 魏云鹏

* [Features]
  1. 修改查询方法logger记录
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 无
  
## V1.10.1- 2018-04-27 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复还能查询到5天之内的数据
  
* [Configuration]
  1. 无

## V1.10.0- 2018-04-26 by 李帅

* [Features]
  1. 移出切换代理定时器
  
* [Bugfixed]
  1. 修复线程池满后到导致的爬虫状态混乱问题
  
* [Configuration]
  1. 添加失败次数(失败多少次后主动切换代理)
  ``` 
  lcc.failedCount=3
  ```
  2.切换代理等待其生效时间(毫秒)
  ```
  lcc.waitProxySwitchTime=20000
  ```

## V1.9.1- 2018-04-24 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复重试机制存在的bug
  
* [Configuration]
  1. 无

## V1.9.0- 2018-04-20 by 李帅

* [Features]
  1. 添加爬虫等待重试机制
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 无

## V1.8.2- 2018-04-17 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复异步爬虫数量过多时服务不可用
  
* [Configuration]
  1. 新增异步单次爬虫次数
  ```
  lcc.multipleGrabCount=50
  ```


## V1.8.1- 2018-04-16 by 李帅/董泽坤

* [Features]
  1. 无
  
* [Bugfixed]
  1. 
  
* [Configuration]
  1. 是否开启批量爬虫（0:关闭,大于0开启）
  ```
  lcc.webDriverSwitch=0
  ```
   2. 自动切换代理定时器
  ```
  chedule.switchproxy.fixedflight=0 0/5 * * * ?
  ```

## V1.8.0- 2018-04-10 by 李帅/董泽坤

* [Features]
  1. 配合spider新增爬虫模块
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 添加单个fixedflight需要开启线程的总个数(默认:3)
  ```
   lcc.grabCount=3
  ```
  
## V1.7.5- 2018-04-11 by 李帅/董泽坤
  
* [Features]
    1. 无
    
* [Bugfixed]
    1. 修复ASIA城市获取机场不唯一
    
* [Configuration]
    1. 无
  
## V1.7.4- 2018-04-04 by 李帅/董泽坤

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复AUTG城市获取机场不唯一
  
* [Configuration]
  1. 无

## V1.7.3- 2018-03-30 by 李帅/董泽坤

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复批量爬虫不等待返回问题
  
* [Configuration]
  1. 添加切换代理IP配置
  ```
    - 切换代理IP的URL
    lcc.switchProxyUrl=http://ip4.hahado.cn/simple/switch-ip?username=duoipcnxxfqgjdr&password=oBAyJKXO0q4d0
    - 当前代理IP查询的最大次数
    lcc.switchProxyCount=100
  ```

## V1.7.2- 2018-03-23 by 李帅/董泽坤

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复不能读出所有机场
  
* [Configuration]

## V1.7.1- 2018-03-22 by 李帅/董泽坤

* [Features]
  1. 修复生单错误匹配routing
  
* [Bugfixed]
  1. 无
  
* [Configuration]

## V1.7.0- 2018-03-21 by 李帅/董泽坤

* [Features]
  1. 新增澳洲虎航
  
* [Bugfixed]
  1. 无
  
* [Configuration]
1. lcc.fixed_flight新增列
```
  -- ipcc
  ALTER TABLE fixed_flight ADD ipcc VARCHAR(10) NULL;
```

## V1.6.0- 2018-03-12 by 李帅

* [Features]
  1. 定时检查数据库，往缓存map中插值
  2. 定时检测缓存map中数据，批量爬虫
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 增加缓存超时间(秒)
  ```
  lcc.cacheTimeOut=3600
  ```
  2. 增加数据库查询定时器
  ```
  schedule.db.fixedflight=0 0/5 * * * ?
  ```
   3. 增加缓存map集合查询定时器
   ```
  schedule.grab.fixedflight=0 0/3 * * * ?
   ```
   
## V1.5.0- 2018-03-09 by 李帅

* [Features]
  1. 程序启动时不再主动爬虫
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 无

## V1.4.0- 2018-03-08 by 李帅

* [Features]
  1. 移出超时队列爬虫，改用定时器查询数据库查看缓存进行爬虫
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 无

## V1.3.4- 2018-03-07 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修正超时爬虫时时间为空问题
  
* [Configuration]
  1. 无

## V1.3.3- 2018-03-06 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修正查询接口itineraryId为空
  
* [Configuration]
  1. 无

## V1.3.2- 2018-03-06 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修正存入库中出发时间不正确问题 
  2. 修正存入库中不为机场三字码问题
  
* [Configuration]
  1. 无

## V1.3.1- 2018-03-05 by 李帅

* [Features]
  1. 无
  
* [Bugfixed]
  1. 解决设置和读取缓存时间不一致问题
  
* [Configuration]
  1. 无

## V1.3.0 - 2018-03-05 by 李帅

* [Features]
  1. 生单验证直接从缓存中数据校验
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 无

## V1.2.0 - 2018-03-05 by 李帅

* [Features]
  1. 即时缓存热门航线
  
* [Bugfixed]
  1. 无
  
* [Configuration]
  1. 增加线程池配置
  ```
    lcc.thread.corePoolSize=10
    lcc.thread.maxPoolSize=20
    lcc.thread.queueCapacity=100
    lcc.thread.keepAlive=20
  ```
  

## V1.1.1 - 2018-02-28 by 李帅/朱帅龙

* [Features]
  1. 无
  
* [Bugfixed]
  1. 修复往返情况下价格校验bug
  
* [Configuration]
  1. 增加爬虫定时器配置
  ```
    schedule.flight.cache=0 0 0 * * ? 
  ```
  
   

## V1.1.0 - 2018-02-27 by 李帅/朱帅龙
* [Features]
  1. 增加支付校验接口
  2. 支付校验接口集成pnr校验,添加pnr验证数据表

* [Bugfixed]
  1. 修复验价redisKey封装bug

* [Configuration]
  1. lcc新增pnr表
  
   ```
    -- pnr
    CREATE TABLE order_pnr
    (
         id  INT AUTO_INCREMENT PRIMARY KEY,
         sessionId   VARCHAR(32)        NOT NULL,
         pnrCode     VARCHAR(6)         NOT NULL,
         adultNumber INT(2) DEFAULT '1' NULL,
         childNumber INT(2) DEFAULT '0' NULL,
         createTime  VARCHAR(30)        NOT NULL,
         CONSTRAINT order_pnr_pnrCode_uindex
         UNIQUE (pnrCode)
    )
    ```

       
  
## V1.0.1 - 2018-02-11 by 李帅/朱帅龙
* [Features]
  1. 无

* [Bugfixed]
  1. 修复验价bug

* [Configuration]
  1. 无

## V1.0.0 - 2018-02-09 by 李帅/朱帅龙
* [Features]
  1. 1.0.0

* [Bugfixed]
  1. 无

* [Configuration]
  1. 新增配置 /data/config/lcc_shopping/config.properties

  ```
  #缓存数据有效时间基数一小时
  lcc.validTime=3600000
  #抓取数据的URL地址
  lcc.grapUrl=http://localhost:9000/lcc/shopping
  #抓取数据配置，parser使用js脚本解析数据，browser使用firefox浏览器，entry抓取pc的网站，timeout设置请求数据的超时时间
  lcc.parser=js
  lcc.browser=Chrome
  lcc.entry=pc
  lcc.timeout=4500000
  #httpclient的两个初始化参数
  lcc.httpmaxTotal=300
  lcc.httpMaxPerRoute=300
  #生单请求的超时时间设置
  lcc.timeOutForBooking=8000
  #生单价格比较的价格范围设置
  lcc.priceLimit=50
  #验价的请求时间超时设置
  lcc.timeOutForCheckPrice=5000
  #redis的访问ip及端口设置
  spring.redis.host=127.0.0.1
  spring.redis.port=6379
  ```

* [DB]

  1. 新建数据库lcc
  
  ```
  -- 黑名单表
  CREATE TABLE lcc.black_list
  (
      id varchar(32),
      requestJson varchar(666),
      black_key varchar(100),
      timeCount int(11),
      createTime varchar(100),
      check_status int(11) DEFAULT '0'
  );
  -- 缓存数据表
  CREATE TABLE lcc.cache_data
  (
      id varchar(32) PRIMARY KEY NOT NULL,
      resultJson mediumtext,
      flightOption varchar(20) NOT NULL,
      fromCity varchar(3) NOT NULL,
      toCity varchar(3) NOT NULL,
      startDate varchar(20) NOT NULL,
      retDate varchar(20),
      adultNumber int(11) DEFAULT '0' NOT NULL,
      childNumber int(11) DEFAULT '0' NOT NULL,
      refreshTime varchar(30) NOT NULL,
      surplusNumber int(11) DEFAULT '10',
      createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
      grapStatus int(1) DEFAULT '1' NOT NULL COMMENT '"抓取状态0--成功，之外均失败"',
      grapTime int(1) NOT NULL,
      grapType int(1) NOT NULL COMMENT '抓取类型0--查询，1--固定，2--超时队列,3--生单，4--验价'
  );
  -- 固定航线表
  CREATE TABLE lcc.fixed_flight
  (
      id int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
      fromCity varchar(3) NOT NULL,
      toCity varchar(3) NOT NULL,
      startDate int(2) NOT NULL,
      retDate int(2) NOT NULL,
      adultNumber int(11) DEFAULT '1',
      childNumber int(11) DEFAULT '0',
      flightOption int(1) NOT NULL
  );
  INSERT INTO lcc.fixed_flight (id, fromCity, toCity, startDate, retDate, adultNumber, childNumber, flightOption) VALUES (1, 'PEK', 'AKL', 0, 0, 1, 0, 0);
  INSERT INTO lcc.fixed_flight (id, fromCity, toCity, startDate, retDate, adultNumber, childNumber, flightOption) VALUES (2, 'HND', 'AKL', 1, 1, 1, 0, 1);
  INSERT INTO lcc.fixed_flight (id, fromCity, toCity, startDate, retDate, adultNumber, childNumber, flightOption) VALUES (3, 'GAU', 'DEL', 1, 1, 1, 0, 0);
  ```

  2. 修改配置 /data/config/jdbc.properties

  ```
  #lcc的jdbc数据源配置
  spring.datasource.lcc.url=jdbc:mysql://localhost:3306/lcc?useUnicode=true&characterEncoding=utf8
  spring.datasource.lcc.username=root
  spring.datasource.lcc.password=
  spring.datasource.lcc.driver-class-name=com.mysql.jdbc.Driver
  #policy的jdbc数据源配置
  spring.datasource.policy.url=jdbc:mysql://192.168.1.23:13306/bst_policy?characterEncoding=UTF-8
  spring.datasource.policy.username=jipiao
  spring.datasource.policy.password=jipiao
  spring.datasource.policy.driver-class-name=com.mysql.jdbc.Driver
  ```
