package com.robin.manager.help.action;

/**
 * Created by robinyang on 2017/6/26.
 */
public class Result {

    private int code;

    private String msg;

    private Object data;

    public static Result newObj() {
        return new Result();
    }

    public static Result newSucess(String msg) {
        return new Result().setCode(200).setMsg(msg);
    }

    public static Result newInitResult(int state,String msg) {
        return new Result().setCode(state).setMsg(msg);
    }

    public static Result newFail(String msg) {
        return new Result().setCode(500).setMsg(msg);
    }

    public static Result newSucess(String msg, Object data) {
        return new Result().setCode(200).setMsg(msg).setData(data);
    }

    public static Result newFail(String msg, Object data) {
        return new Result().setCode(500).setMsg(msg).setData(data);
    }

    public static Result newSucess() {
        return new Result().setCode(200);
    }

    public static Result newFail() {
        return new Result().setCode(500);
    }

    public static Result newSucess(Object data) {
        return new Result().setCode(200).setData(data);
    }

    public static Result newFail(Object data) {
        return new Result().setCode(500).setData(data);
    }

    public static Result newFail(int code, String msg) {
        return new Result().setMsg(msg).setCode(code);
    }

    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

}
