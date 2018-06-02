package com.het.protocol.coder.encode;

import com.het.protocol.coder.bean.ProtocolBean;
import com.het.protocol.coder.encode.inter.Encoder;
import com.het.protocol.coder.exception.DecodeException;
import com.het.protocol.coder.exception.EncodeException;

public abstract class AbstractEncoder implements Encoder {
    @Override
    public byte[] encode(Object data) throws EncodeException {
        if (data == null)
            return null;
        if (data instanceof ProtocolBean) {
            ProtocolBean bean = (ProtocolBean) data;
            fillData(bean);

        }
        return null;
    }

    abstract void fillData(ProtocolBean bean) throws DecodeException;
}
