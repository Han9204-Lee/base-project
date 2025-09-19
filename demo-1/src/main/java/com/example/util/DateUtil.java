package com.example.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class DateUtil {

    private DateUtil() {}
    /**
     * yyyymm 형태의 문자열을 받아 해당 월의 시작일과 종료일을 yyyymmdd 형식의 문자열로 반환합니다.
     * 예: "202506" -> ["20250601", "20250630"]
     */
    public static String[] getMonthStartEnd(String yyyymm) {
        Objects.requireNonNull(yyyymm, "yyyymm");
        String trimmed = yyyymm.trim();
        if (trimmed.length() != 6 || !trimmed.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("yyyymm 형식의 6자리 숫자 문자열이어야 합니다. 예: 202506");
        }
        YearMonth ym = YearMonth.parse(trimmed, DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        String startStr = start.format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        String endStr = end.format(DateTimeFormatter.BASIC_ISO_DATE);
        return new String[]{ startStr, endStr };
    }

    /**
     * yyyymm 형태의 정수(예: 202506)를 받아 해당 월의 시작일과 종료일을 yyyymmdd 형식 문자열로 반환합니다.
     */
    public static String[] getMonthStartEnd(int yyyymm) {
        return getMonthStartEnd(String.format("%06d", yyyymm));
    }
}
