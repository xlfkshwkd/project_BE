package org.simsimhi.api.controllers;

import org.simsimhi.commons.exceptions.BadRequestException;
import org.simsimhi.commons.exceptions.CommonException;
import org.simsimhi.commons.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice("org.simsimhi.api.controllers")
public class CommonController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONData> errorHandler(Exception e){
        HttpStatus status =HttpStatus.INTERNAL_SERVER_ERROR;
        Object message =e.getMessage();

        if(e instanceof CommonException){
            CommonException commonException =(CommonException) e;
            status =commonException.getStatus();

            if(commonException.getMessages() != null) message=commonException.getMessages();
        }
        else if (e instanceof BadRequestException){
            status =HttpStatus.UNAUTHORIZED;
        } else if (e instanceof AccessDeniedException) {
            status =HttpStatus.FORBIDDEN;

        }
        //BadCredentialsException -> 500 -> 401
        //AccessDeniedException -> 500 -> 403


        JSONData data =new JSONData();
        data.setSuccess(false);
        data.setStatus(status);
        data.setMessage(message);

        e.printStackTrace();

        return ResponseEntity.status(status).body(data);
    }
}

