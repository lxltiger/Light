package com.kimascend.light;


/**
 * 应用的配置信息
 */
@Deprecated
public interface Config {

    String IMG_PREFIX = "http://192.168.1.33/img";
    String FACTORY_NAME = "kimascend";
    String FACTORY_PASSWORD = "123456";

    int LAMP_RGB = 1;
    int LAMP = 4;
    int SOCKET = 6;
    int PANEL = 9;
    int LAMP_TYPE = 256;
    int SOCKET_TYPE = 259;
    int PANEL_TYPE = 258;

    //闹钟页面的三种类型
    int CLOCK_OPEN = 1;
    int CLOCK_CLOSE = 2;
    int CLOCK_RGB = 3;
}
