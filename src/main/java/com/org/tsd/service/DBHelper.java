package com.org.tsd.service;

import java.sql.Date;

import org.springframework.stereotype.Service;


@Service
public class DBHelper {
    public static Date toSQLDate(java.util.Date date) {
        if (date != null) {
            return new Date(date.getTime());
        }
        return null;
    }
}
