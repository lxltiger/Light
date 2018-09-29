package com.kimascend.light.common;

import android.util.SparseIntArray;

public class Config {
    private static final int LAMP_RGB = 1;
    private static final int LAMP = 4;
    private static final int SOCKET = 6;
    private static final int PANEL = 9;
    private static final int LAMP_TYPE = 256;
    private static final int SOCKET_TYPE = 259;
    private static final int PANEL_TYPE = 258;

   public static final  String FACTORY_NAME = "kimascend";
   public static final  String FACTORY_PASSWORD = "123456";

    private static SparseIntArray deviceType = new SparseIntArray();

    private Config() {}

    static {
        deviceType.put(LAMP_RGB, LAMP_TYPE);
        deviceType.put(LAMP, LAMP_TYPE);
        deviceType.put(SOCKET, SOCKET_TYPE);
        deviceType.put(PANEL, PANEL_TYPE);
    }


    public static int getType(int productUUID) {
        return deviceType.get(productUUID);
    }


}
