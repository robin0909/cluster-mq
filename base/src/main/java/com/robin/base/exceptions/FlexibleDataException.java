package com.robin.base.exceptions;

public class FlexibleDataException extends Thread {

    public FlexibleDataException() {
        super("非法包异常");
    }

    public FlexibleDataException(String name) {
        super(name);
    }
}
