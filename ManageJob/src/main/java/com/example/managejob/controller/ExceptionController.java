package com.example.managejob.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    private  static Logger logger = Logger.getLogger(ExceptionController.class);

    @ExceptionHandler(Exception.class)
    public String exception(Exception exception){
        logger.error(exception);
        exception.printStackTrace();
        return "error403";
    }
}
