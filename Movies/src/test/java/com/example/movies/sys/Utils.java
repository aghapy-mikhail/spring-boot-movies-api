package com.example.movies.sys;

import com.example.movies.sys.exception.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Utils {
    private Utils() {}

    public static String getJsonString(final Object obj) {
        ObjectMapper mapper=new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new InternalServerException("There's a problem converting this object to a JSON string: " + obj + "\n"+e.getLocalizedMessage() +"\n"+ e.getMessage() + "\n"+e.getCause());
        }
    }
}
