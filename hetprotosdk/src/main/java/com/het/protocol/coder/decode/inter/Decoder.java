package com.het.protocol.coder.decode.inter;

import com.het.protocol.coder.exception.DecodeException;

public interface Decoder {
    <T> T decode(Object data) throws DecodeException;
}
