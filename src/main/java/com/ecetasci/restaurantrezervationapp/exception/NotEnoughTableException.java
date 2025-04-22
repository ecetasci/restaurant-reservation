package com.ecetasci.restaurantrezervationapp.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughTableException extends RuntimeException{

    public NotEnoughTableException(String message) {
        super(message);
    }
    public NotEnoughTableException(String message,Throwable err) {
        super(message,err);
    }

}
