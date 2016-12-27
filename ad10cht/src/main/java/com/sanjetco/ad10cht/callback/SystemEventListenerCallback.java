package com.sanjetco.ad10cht.callback;

/**
 * Created by PaulLee on 2016/5/11.
 */
public interface SystemEventListenerCallback {
    void notifyIgnitionStatusPolling(boolean status);
    void notifyDongleDetached();
    void notifyDongleConnected();
    void notifyIgnitionEventRaised(boolean status);
    void notifyLowCarBattery();
}
