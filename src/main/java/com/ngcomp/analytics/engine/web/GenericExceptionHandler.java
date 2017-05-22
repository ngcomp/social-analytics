package com.ngcomp.analytics.engine.web;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * User: Ram Parashar
 * Date: 11/10/13
 * Time: 2:33 PM
 */
public class GenericExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleExceptionWithErrorCode(Exception e) {
        return exceptionAsJson(e);
    }

    private String exceptionAsJson(Exception e) {
        e.printStackTrace();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", e.getMessage());
        jsonObject.put("class", e.getClass().toString());
        jsonObject.put("data" , "error");
        if (e.getCause() != null) {
            jsonObject.put("cause", e.getCause().toString());
        }
        return jsonObject.toString();
    }
}
