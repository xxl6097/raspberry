package com.het.httpserver.bean;

import java.io.Serializable;
import java.util.List;

public class FanyiBean implements Serializable{

    /**
     * from : de
     * to : zh
     * trans_result : [{"src":"Pakistan*Sindh*Karachi","dst":"巴基斯坦卡拉奇，信德省××"}]
     */

    private String from;
    private String to;
    private List<TransResultBean> trans_result;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<TransResultBean> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TransResultBean> trans_result) {
        this.trans_result = trans_result;
    }

    public static class TransResultBean {
        /**
         * src : Pakistan*Sindh*Karachi
         * dst : 巴基斯坦卡拉奇，信德省××
         */

        private String src;
        private String dst;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}
