package com.yrmjhtdjxh.punch.exception;

/**
 * @author GO FOR IT
 */
public class TokenErrorException extends RuntimeException{

    private int code;

    public TokenErrorException() {
        super();
    }

    public TokenErrorException(int code, String msg){
        super(msg);
        this.code = code;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
