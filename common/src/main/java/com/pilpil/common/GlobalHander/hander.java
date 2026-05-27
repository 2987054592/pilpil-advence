package com.pilpil.common.GlobalHander;


import com.pilpil.common.entity.Result;
import com.pilpil.common.exception.illegalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

import static com.pilpil.common.constants.Exception.exceptionConstants.EXIST;


@RestControllerAdvice
public class hander {
    @ExceptionHandler(illegalException.class)
    public Result handle(Exception e){
        return Result.error(e.getMessage());
    }
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handles(Exception e){
        return Result.error(EXIST);
    }
}
