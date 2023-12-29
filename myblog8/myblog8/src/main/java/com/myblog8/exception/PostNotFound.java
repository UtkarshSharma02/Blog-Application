package com.myblog8.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFound extends RuntimeException{
    public PostNotFound(String msg){
        super(msg);
    }
}
