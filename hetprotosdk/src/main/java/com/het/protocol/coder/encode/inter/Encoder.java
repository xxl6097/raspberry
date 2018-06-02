package com.het.protocol.coder.encode.inter;

import com.het.protocol.coder.exception.EncodeException;

public interface Encoder {
    byte[] encode(Object data) throws EncodeException;
}
