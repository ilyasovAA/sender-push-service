package com.example.demo;

public class DeviceStatus {
    String fcmToken;
    boolean isSend;
    String imei;

    public DeviceStatus(String imei, String fcmToken, boolean isSend) {
        this.fcmToken = fcmToken;
        this.isSend = isSend;
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
