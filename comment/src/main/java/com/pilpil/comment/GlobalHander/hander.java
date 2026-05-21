package com.pilpil.comment.GlobalHander;

import com.pilpil.comment.domain.Result;
import com.pilpil.comment.exception.illegalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class hander {
    @ExceptionHandler(illegalException.class)
    public Result handle(Exception e){
        return Result.error(e.getMessage());
    }
}
