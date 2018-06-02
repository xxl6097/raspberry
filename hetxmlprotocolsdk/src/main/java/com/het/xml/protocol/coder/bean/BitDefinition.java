package com.het.xml.protocol.coder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * 协议bit定义
 *
 * @author jake
 */
@XStreamAlias("bitDef")
public class BitDefinition extends BaseDefinition {

    private static final long serialVersionUID = 8445939520203504197L;
    //位移
    @XStreamAlias("shift")
    @XStreamAsAttribute
    private Integer shift;
    //位移方向
//	@XStreamAlias("direction")
//	@XStreamAsAttribute
//	private String direction="right";

    public Integer getShift() {
        return shift;
    }

    public void setShift(Integer shift) {
        this.shift = shift;
    }

//	public String getDirection() {
//		return direction;
//	}
//
//	public void setDirection(String direction) {
//		this.direction = direction==null?"right":direction.trim().toLowerCase();
//	}


}
