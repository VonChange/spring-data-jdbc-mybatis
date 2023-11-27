package com.vonchange.common.util;

import com.vonchange.common.util.exception.ParseException;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        return parseDate(str, (Locale)null, parsePatterns);
    }
    public static Date parseDate(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, true);
    }
    private static Date parseDateWithLeniency(String str, Locale locale, String[] parsePatterns, boolean lenient) throws ParseException {
        if (str != null && parsePatterns != null) {
            SimpleDateFormat parser;
            if (locale == null) {
                parser = new SimpleDateFormat();
            } else {
                parser = new SimpleDateFormat("", locale);
            }

            parser.setLenient(lenient);
            ParsePosition pos = new ParsePosition(0);
            String[] arr$ = parsePatterns;
            int len$ = parsePatterns.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String parsePattern = arr$[i$];
                String pattern = parsePattern;
                if (parsePattern.endsWith("ZZ")) {
                    pattern = parsePattern.substring(0, parsePattern.length() - 1);
                }

                parser.applyPattern(pattern);
                pos.setIndex(0);
                String str2 = str;
                if (parsePattern.endsWith("ZZ")) {
                    str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
                }

                Date date = parser.parse(str2, pos);
                if (date != null && pos.getIndex() == str2.length()) {
                    return date;
                }
            }

            throw new ParseException("Unable to parse the date: " + str, -1);
        } else {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
    }

}
