package com.keima.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public static String formatDate(LocalDate date) {
        return date.format(FORMATTER);
    }
}
