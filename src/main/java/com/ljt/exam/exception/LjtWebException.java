package com.ljt.exam.exception;

public class LjtWebException extends  Exception{
    public final  LjtWebError ljtWebError;

    public LjtWebException(LjtWebError ljtWebError){
        this.ljtWebError=ljtWebError;
    }
}
