package com.example.movies.sys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
@Data
@AllArgsConstructor
public class Error {
    private Date timestamp;
    private String type;
    private String message;
}
