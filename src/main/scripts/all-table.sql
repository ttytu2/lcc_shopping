-- auto-generated definition
CREATE TABLE fixed_flight
(
  id           INT AUTO_INCREMENT
    PRIMARY KEY,
  fromCity     VARCHAR(3)         NOT NULL,
  toCity       VARCHAR(3)         NOT NULL,
  startDate    INT(2)             NOT NULL,
  retDate      INT(2)             NOT NULL,
  adultNumber  INT DEFAULT '1'    NULL,
  childNumber  INT DEFAULT '0'    NULL,
  flightOption INT(1)             NOT NULL,
  ipcc         VARCHAR(20)        NULL,
  grabSwitch   INT(1) DEFAULT '1' NULL
)
  ENGINE = InnoDB;




  ########################


  -- auto-generated definition
CREATE TABLE order_pnr
(
  id          INT AUTO_INCREMENT
    PRIMARY KEY,
  sessionId   VARCHAR(32)        NOT NULL,
  pnrCode     VARCHAR(6)         NOT NULL,
  adultNumber INT(2) DEFAULT '1' NULL,
  childNumber INT(2) DEFAULT '0' NULL,
  createTime  VARCHAR(30)        NOT NULL,
  fromCity    VARCHAR(3)         NULL,
  toCity      VARCHAR(3)         NULL,
  ipcc        VARCHAR(10)        NULL,
  CONSTRAINT order_pnr_pnrCode_uindex
  UNIQUE (pnrCode)
)
  ENGINE = InnoDB;

CREATE INDEX order_pnr_createTime_index
  ON order_pnr (createTime);

#########################

-- auto-generated definition
CREATE TABLE account_9c
(
  id           BIGINT UNSIGNED AUTO_INCREMENT
    PRIMARY KEY,
  username     VARCHAR(30)                  NULL
  COMMENT '9C账号',
  password     VARCHAR(30)                  NULL
  COMMENT '密码',
  gmt_creat    DATETIME                     NULL
  COMMENT '创建时间',
  gmt_modified DATETIME                     NULL
  COMMENT '更新时间',
  is_disabled  TINYINT UNSIGNED DEFAULT '0' NULL
  COMMENT '用户状态（1：封号、0：未封号）',
  CONSTRAINT account_9c_username_uindex
  UNIQUE (username)
)
  ENGINE = InnoDB;

###########################

-- auto-generated definition
CREATE TABLE cache_data
(
  id            VARCHAR(32)                         NOT NULL
    PRIMARY KEY,
  resultJson    MEDIUMTEXT                          NOT NULL,
  flightOption  VARCHAR(20)                         NOT NULL,
  fromCity      VARCHAR(3)                          NOT NULL,
  toCity        VARCHAR(3)                          NOT NULL,
  startDate     VARCHAR(20)                         NOT NULL,
  retDate       VARCHAR(20)                         NULL,
  adultNumber   INT DEFAULT '0'                     NOT NULL,
  childNumber   INT DEFAULT '0'                     NOT NULL,
  refreshTime   VARCHAR(30)                         NOT NULL,
  surplusNumber INT DEFAULT '10'                    NULL,
  createTime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  grapStatus    INT(1) DEFAULT '1'                  NOT NULL
  COMMENT '"抓取状态0--成功，之外均失败"',
  grapTime      INT(1)                              NOT NULL,
  grapType      INT(1)                              NOT NULL
  COMMENT '抓取类型0--查询，1--固定，2--超时队列,3--生单，4--验价',
  ipcc          VARCHAR(10)                         NULL
)
  ENGINE = InnoDB;

CREATE INDEX cache_data_createTime_index
  ON cache_data (createTime);



