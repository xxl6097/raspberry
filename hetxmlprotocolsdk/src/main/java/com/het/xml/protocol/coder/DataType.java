package com.het.xml.protocol.coder;

public enum DataType {
    CHAR("Char", "char", 1),
    SHORT("UnsignedShort", "number", 2),
    INTEGER("Int", "number", 4),
    STRING("String", "string", 0),
    BYTE("UnsignedByte", "number", 1),
    LONG("Long", "number", 8),
    FLOAT("Float", "float", 4),
    DOUBLE("Double", "float", 8),
    HEX_STRING("HexString", "string", 0),
    BYTE_ARRAY("ByteArray", "array", 0);

    // 数据类型
    private String typeName;
    // 数据类型字节数
    private int size;
    // 分类
    private String category;

    private DataType(String typeName, String category, int size) {
        this.typeName = typeName;
        this.size = size;
        this.category = category;
    }

    @Override
    public String toString() {
        return typeName;
    }

    public int getSize() {

        return size;
    }

    public String getCategory() {
        return category;
    }
}
