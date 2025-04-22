package com.ecetasci.restaurantrezervationapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservationCouldntFind extends RuntimeException{

    public ReservationCouldntFind(String message){
        super(message);
    }

    public ReservationCouldntFind(String message, Throwable err){
        super(message,err);
    }
}
