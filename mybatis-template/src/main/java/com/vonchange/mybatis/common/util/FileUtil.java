package com.vonchange.mybatis.common.util;

import com.vonchange.mybatis.common.util.io.UnicodeInputStream;


import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static String readUTFString(final InputStream inputStream) throws IOException {
        UnicodeInputStream in = null;
        try {
            in = new UnicodeInputStream(inputStream, null);
            return StreamUtil.copy(in, detectEncoding(in)).toString();
        } finally {
            StreamUtil.close(in);
        }
    }
    private static String detectEncoding(final UnicodeInputStream in) {
        String encoding = in.getDetectedEncoding();
        if (encoding == null) {
            encoding = StringPool.UTF_8;
        }
        return encoding;
    }
}
