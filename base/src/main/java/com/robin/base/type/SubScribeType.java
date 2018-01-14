package com.robin.base.type;

public enum  SubScribeType {

    ONE_TO_ONE(1),    // 一对一

    ONE_TO_MANY(2);   // 一对多

    int type;

    SubScribeType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static SubScribeType parse(int type) {

        switch (type) {
            case 1:
                return SubScribeType.ONE_TO_ONE;
            case 2:
                return SubScribeType.ONE_TO_MANY;
        }

        return SubScribeType.ONE_TO_ONE;
    }
}
