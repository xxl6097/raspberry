package com.het.protocol.coder.exception;

public class EncodeException extends RuntimeException {
    private int code;

    public EncodeException(int code) {
        this.code = code;
    }

    public EncodeException(String s, int code) {
        super(s);
        this.code = code;
    }

    public EncodeException(String s, Throwable throwable, int code) {
        super(s, throwable);
        this.code = code;
    }

    public EncodeException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public EncodeException(String s, Throwable throwable, boolean b, boolean b1, int code) {
        super(s, throwable, b, b1);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public final class ERR{
        public final static int DECODE_BODYLEN_ERROR = 200001;
        public final static int DECODE_DEV_ERROR = 200002;
        public final static int DECODE_DATALEN_ERROR = 200003;
        public final static int DECODE_CRC_ERROR = 200004;
    }
}
