package com.het.udp.core.smartlink.ti.callback;

/**
 * Created by UUXIA on 2015/6/25.
 */
public interface SmartConfigListener {
    void onSmartConfigEvent(SmartConfigListener.SmtCfgEvent var1, Exception var2);

    public static enum SmtCfgEvent {
        FTC_SUCCESS,
        FTC_ERROR,
        FTC_TIMEOUT;

        private SmtCfgEvent() {
        }
    }
}
