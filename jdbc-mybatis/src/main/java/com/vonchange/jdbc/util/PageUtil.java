package com.vonchange.jdbc.util;

/**
 * Created by 冯昌义 on 2018/5/24.
 */
public class PageUtil {
    private PageUtil() { throw new IllegalStateException("Utility class"); }
    public static int getTotalPage(long totalNum, int pageSize) {
        if (pageSize <= 0 || totalNum <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalNum / (double) pageSize);
    }
}
