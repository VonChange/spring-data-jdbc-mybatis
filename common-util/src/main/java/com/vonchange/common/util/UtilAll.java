package com.vonchange.common.util;

import com.vonchange.common.util.exception.ParseException;
import com.vonchange.common.util.io.FastCharArrayWriter;
import org.slf4j.helpers.MessageFormatter;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vonchange.common.util.StringPool.EMPTY;

public class UtilAll {
    public static class UFile {
        private static final   String  URLSEPARATOR="/";
        public static ClassLoader getDefaultClassLoader() {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread().getContextClassLoader();
            }
            catch (Throwable ex) {
                // Cannot access thread context ClassLoader - falling back...
            }
            if (cl == null) {
                // No thread context class loader -> use class loader of this class.
                cl = UtilAll.class.getClassLoader();
                if (cl == null) {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    }
                    catch (Throwable ex) {
                        // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    }
                }
            }
            return cl;
        }
        public static String readUTFString(final InputStream inputStream) throws IOException {
            try {
                return UStream.copy(inputStream, StringPool.UTF_8).toString();
            } finally {
                UStream.close(inputStream);
            }
        }


        /**
         *  取指定文件的扩展名
         * @param filePathName
         *            文件路径
         * @return 扩展名
         */
        public static String getFileExt(String filePathName) {
            int pos = 0;
            pos = filePathName.lastIndexOf('.');
            if (pos != -1)
                return filePathName.substring(pos + 1, filePathName.length());
            else
                return "";
        }
        //classpath:com/test/a.md
        public static InputStream getClassResource(String location){
            //PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            //return patternResolver.getResource(location);
            ClassLoader classLoader = getDefaultClassLoader();
            if(null==classLoader){
                return null;
            }
            //String pathToUse = UString.replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
            return classLoader.getResourceAsStream(location);
        }
    }
    public static class UStream {
        private static final int ZERO = 0;
        private static final int NEGATIVE_ONE = -1;
        private static final int ALL = -1;
        public static int ioBufferSizeX = 16384;
        /**
         * Closes silently the closable object. If it is {@link Flushable}, it
         * will be flushed first. No exception will be thrown if an I/O error occurs.
         */
        public static void close(final Closeable closeable) {
            if (closeable != null) {
                if (closeable instanceof Flushable) {
                    try {
                        ((Flushable) closeable).flush();
                    } catch (IOException ignored) {
                    }
                }

                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }


        public static FastCharArrayWriter copy(final InputStream input, final String encoding) throws IOException {
            return copy(input, encoding, ALL);
        }
        public static FastCharArrayWriter copy(final InputStream input, final String encoding, final int count) throws IOException {
            try (FastCharArrayWriter output = createFastCharArrayWriter()) {
                copy(input, output, encoding, count);
                return output;
            }
        }
        private static FastCharArrayWriter createFastCharArrayWriter() {
            return new FastCharArrayWriter(bufferSize());
        }

        private static int bufferSize() {
            return ioBufferSizeX;
        }
        private static int bufferSize(final int count) {
            final int ioBufferSize = ioBufferSizeX;
            if (count < ioBufferSize) {
                return count;
            } else {
                return ioBufferSize;
            }
        }
        public static <T extends Writer> T copy(final InputStream input, final T output, final String encoding, final int count) throws IOException {
            copy(inputStreamReadeOf(input, encoding), output, count);
            return output;
        }
        public static InputStreamReader inputStreamReadeOf(final InputStream input, final String encoding) throws UnsupportedEncodingException {
            return new InputStreamReader(input, encoding);
        }
        public static int copy(final Reader input, final Writer output, final int count) throws IOException {
            if (count == ALL) {
                return copy(input, output);
            }

            int numToRead = count;
            char[] buffer = new char[numToRead];

            int totalRead = ZERO;
            int read;

            while (numToRead > ZERO) {
                read = input.read(buffer, ZERO, bufferSize(numToRead));
                if (read == NEGATIVE_ONE) {
                    break;
                }
                output.write(buffer, ZERO, read);

                numToRead = numToRead - read;
                totalRead = totalRead + read;
            }

            output.flush();
            return totalRead;
        }
        public static int copy(final Reader input, final Writer output) throws IOException {
            int numToRead = bufferSize();
            char[] buffer = new char[numToRead];

            int totalRead = ZERO;
            int read;

            while ((read = input.read(buffer, ZERO, numToRead)) >= ZERO) {
                output.write(buffer, ZERO, read);
                totalRead = totalRead + read;
            }

            output.flush();
            return totalRead;
        }



    }
    public static class UString {
        public static String uuid() {
            String uuid = UUID.randomUUID().toString();
            return remove(uuid, "-");
        }
        public static String substringBeforeLast(final String str, final String separator) {
            if (isEmpty(str) || isEmpty(separator)) {
                return str;
            }
            final int pos = str.lastIndexOf(separator);
            if (pos == -1) {
                return str;
            }
            return str.substring(0, pos);
        }

        public static String substringAfterLast(final String str, final String separator) {
            if (isEmpty(str)) {
                return str;
            }
            if (isEmpty(separator)) {
                return EMPTY;
            }
            final int pos = str.lastIndexOf(separator);
            if (pos == -1 || pos == str.length() - separator.length()) {
                return EMPTY;
            }
            return str.substring(pos + separator.length());
        }
        public static boolean isEmpty(final CharSequence cs) {
            return cs == null || cs.length() == 0;
        }
        public static String remove(String s, String sub) {
            int c = 0;
            int sublen = sub.length();
            if (sublen == 0) {
                return s;
            } else {
                int i = s.indexOf(sub, c);
                if (i == -1) {
                    return s;
                } else {
                    StringBuilder sb = new StringBuilder(s.length());

                    do {
                        sb.append(s, c, i);
                        c = i + sublen;
                    } while ((i = s.indexOf(sub, c)) != -1);

                    if (c < s.length()) {
                        sb.append(s, c, s.length());
                    }

                    return sb.toString();
                }
            }
        }

        public static boolean isBlank(final CharSequence cs) {
            int strLen;
            if (cs == null || (strLen = cs.length()) == 0) {
                return true;
            }
            for (int i = 0; i < strLen; i++) {
                if (Character.isWhitespace(cs.charAt(i)) == false) {
                    return false;
                }
            }
            return true;
        }

        public static boolean isNotBlank(final CharSequence cs) {
            return !isBlank(cs);
        }

        public static String format(String messageTemplate, Object... parameters) {
            return MessageFormatter.arrayFormat(messageTemplate, parameters).getMessage();
        }
        public static String formatException(String messageTemplate,Throwable throwable, Object... parameters) {
            return MessageFormatter.arrayFormat(messageTemplate, parameters,throwable).getMessage();
        }

        public static String strNums(String str, String split, int num) {
            if (num == 0) {
                return "";
            }
            StringBuilder fullStr = new StringBuilder();
            for (int i = 0; i < num; i++) {
                fullStr.append(str).append(split);
            }
            return fullStr.substring(0, fullStr.length() - split.length());
        }

        public static String strList(List<String> strs, String split) {
            if (null == strs || strs.isEmpty()) {
                return "";
            }
            StringBuilder fullStr = new StringBuilder();
            for (String string : strs) {
                fullStr.append(string).append(split);
            }
            return fullStr.substring(0, fullStr.length() - split.length());
        }

        public static Boolean isNull(Object obj) {
            if (null == obj) {
                return true;
            }
            String str = String.valueOf(obj);
            if (isBlank(str)) {
                return true;
            }
            return false;
        }

        public static final char UNDERLINE = '_';

        public static String camelToUnderline(String param) {
            if (param == null || "".equals(param.trim())) {
                return "";
            }
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(UNDERLINE);
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        /**
         * 正则表达式 实现
         *
         * @param tplStr
         * @param data
         */
        public static String tplByMap(String tplStr, Map<String, Object> data) {
            Matcher m = Pattern.compile("\\$\\{([\\w\\.]*)\\}").matcher(tplStr);
            List<String> patternList = new ArrayList<String>();
            List<String> replaceStrList = new ArrayList<String>();
            while (m.find()) {
                String group = m.group();
                patternList.add(group);
                group = group.replaceAll("\\$|\\{|\\}", "");
                String value = "";
                if (null != data.get(group)) {
                    value = String.valueOf(data.get(group));
                }
                replaceStrList.add(value);
            }
            tplStr = replaceEach(tplStr, patternList.toArray(new String[patternList.size()]),
                    replaceStrList.toArray(new String[replaceStrList.size()]));
            return tplStr;
        }

        /*
         * public static String tpl(String tplStr, Map<String, Object> data) {
         * StringTemplateParser stp = new StringTemplateParser();
         *
         * return stp.parse(tplStr,macroName -> {
         * return ConvertUtil.toString(data.get(macroName));
         * });
         * }
         */

        public static String capitalize(final String str) {
            int strLen;
            if (str == null || (strLen = str.length()) == 0) {
                return str;
            }

            final char firstChar = str.charAt(0);
            if (Character.isTitleCase(firstChar)) {
                // already capitalized
                return str;
            }

            return new StringBuilder(strLen)
                    .append(Character.toTitleCase(firstChar))
                    .append(str.substring(1))
                    .toString();
        }

        public static String lowerFirst(final String str) {
            int strLen;
            if (str == null || (strLen = str.length()) == 0) {
                return str;
            }

            final char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                // already capitalized
                return str;
            }

            return new StringBuilder(strLen)
                    .append(Character.toLowerCase(firstChar))
                    .append(str.substring(1))
                    .toString();
        }

        public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
            if (cs1 == cs2) {
                return true;
            }
            if (cs1 == null || cs2 == null) {
                return false;
            }
            if (cs1 instanceof String && cs2 instanceof String) {
                return cs1.equals(cs2);
            }
            return regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
        }

        public static String replaceEach(final String text, final String[] searchList, final String[] replacementList) {
            return replaceEach(text, searchList, replacementList, false, 0);
        }

        private static String replaceEach(
                final String text, final String[] searchList, final String[] replacementList, final boolean repeat,
                final int timeToLive) {

            // mchyzer Performance note: This creates very few new objects (one major goal)
            // let me know if there are performance requests, we can create a harness to
            // measure

            if (text == null || text.isEmpty() || searchList == null ||
                    searchList.length == 0 || replacementList == null || replacementList.length == 0) {
                return text;
            }

            // if recursing, this shouldn't be less than 0
            if (timeToLive < 0) {
                throw new IllegalStateException("Aborting to protect against StackOverflowError - " +
                        "output of one loop is the input of another");
            }

            final int searchLength = searchList.length;
            final int replacementLength = replacementList.length;

            // make sure lengths are ok, these need to be equal
            if (searchLength != replacementLength) {
                throw new IllegalArgumentException("Search and Replace array lengths don't match: "
                        + searchLength
                        + " vs "
                        + replacementLength);
            }

            // keep track of which still have matches
            final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

            // index on index that the match was found
            int textIndex = -1;
            int replaceIndex = -1;
            int tempIndex = -1;

            // index of replace array that will replace the search string found
            // NOTE: logic duplicated below START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                        searchList[i].isEmpty() || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i]);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic mostly below END

            // no search strings found, we are done
            if (textIndex == -1) {
                return text;
            }

            int start = 0;

            // get a good guess on the size of the result buffer so it doesn't have to
            // double if it goes over a bit
            int increase = 0;

            // count the replacement text elements that are larger than their corresponding
            // text being replaced
            for (int i = 0; i < searchList.length; i++) {
                if (searchList[i] == null || replacementList[i] == null) {
                    continue;
                }
                final int greater = replacementList[i].length() - searchList[i].length();
                if (greater > 0) {
                    increase += 3 * greater; // assume 3 matches
                }
            }
            // have upper-bound at 20% increase, then let Java take over
            increase = Math.min(increase, text.length() / 5);

            final StringBuilder buf = new StringBuilder(text.length() + increase);

            while (textIndex != -1) {

                for (int i = start; i < textIndex; i++) {
                    buf.append(text.charAt(i));
                }
                buf.append(replacementList[replaceIndex]);

                start = textIndex + searchList[replaceIndex].length();

                textIndex = -1;
                replaceIndex = -1;
                tempIndex = -1;
                // find the next earliest match
                // NOTE: logic mostly duplicated above START
                for (int i = 0; i < searchLength; i++) {
                    if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
                            searchList[i].isEmpty() || replacementList[i] == null) {
                        continue;
                    }
                    tempIndex = text.indexOf(searchList[i], start);

                    // see if we need to keep searching for this
                    if (tempIndex == -1) {
                        noMoreMatchesForReplIndex[i] = true;
                    } else {
                        if (textIndex == -1 || tempIndex < textIndex) {
                            textIndex = tempIndex;
                            replaceIndex = i;
                        }
                    }
                }
                // NOTE: logic duplicated above END

            }
            final int textLength = text.length();
            for (int i = start; i < textLength; i++) {
                buf.append(text.charAt(i));
            }
            final String result = buf.toString();
            if (!repeat) {
                return result;
            }

            return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
        }

        public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
            return endsWith(str, suffix, false);
        }

        private static boolean endsWith(final CharSequence str, final CharSequence suffix, final boolean ignoreCase) {
            if (str == null || suffix == null) {
                return str == null && suffix == null;
            }
            if (suffix.length() > str.length()) {
                return false;
            }
            final int strOffset = str.length() - suffix.length();
            return regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
        }

        public static boolean startsWithChar(String s, char c) {
            if (s.length() == 0) {
                return false;
            } else {
                return s.charAt(0) == c;
            }
        }

        public static boolean containsOnlyDigitsAndSigns(CharSequence string) {
            int size = string.length();

            for (int i = 0; i < size; ++i) {
                char c = string.charAt(i);
                if (!isDigit(c) && c != '-' && c != '+') {
                    return false;
                }
            }

            return true;
        }

        public static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        public static String[] split(final String src, final String delimiter) {
            int maxparts = (src.length() / delimiter.length()) + 2; // one more for the last
            int[] positions = new int[maxparts];
            int dellen = delimiter.length();

            int i, j = 0;
            int count = 0;
            positions[0] = -dellen;
            while ((i = src.indexOf(delimiter, j)) != -1) {
                count++;
                positions[count] = i;
                j = i + dellen;
            }
            count++;
            positions[count] = src.length();

            String[] result = new String[count];

            for (i = 0; i < count; i++) {
                result[i] = src.substring(positions[i] + dellen, positions[i + 1]);
            }
            return result;
        }

        /**
         * Returns <code>true</code> if string contains only digits.
         */
        public static boolean containsOnlyDigits(final CharSequence string) {
            int size = string.length();
            for (int i = 0; i < size; i++) {
                char c = string.charAt(i);
                if (!isDigit(c)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Splits a string in several parts (tokens) that are separated by delimiter
         * characters. Delimiter may contains any number of character and it is
         * always surrounded by two strings.
         *
         * @param src source to examine
         * @param d   string with delimiter characters
         *
         * @return array of tokens
         */
        public static String[] splitc(final String src, final String d) {
            if ((d.length() == 0) || (src.length() == 0)) {
                return new String[] { src };
            }
            return splitc(src, d.toCharArray());
        }

        /**
         * Splits a string in several parts (tokens) that are separated by delimiter
         * characters. Delimiter may contains any number of character and it is
         * always surrounded by two strings.
         *
         * @param src        source to examine
         * @param delimiters char array with delimiter characters
         *
         * @return array of tokens
         */
        public static String[] splitc(final String src, final char[] delimiters) {
            if ((delimiters.length == 0) || (src.length() == 0)) {
                return new String[] { src };
            }
            char[] srcc = src.toCharArray();

            int maxparts = srcc.length + 1;
            int[] start = new int[maxparts];
            int[] end = new int[maxparts];

            int count = 0;

            start[0] = 0;
            int s = 0, e;
            if (UChar.equalsOne(srcc[0], delimiters)) { // string starts with delimiter
                end[0] = 0;
                count++;
                s = UChar.findFirstDiff(srcc, 1, delimiters);
                if (s == -1) { // nothing after delimiters
                    return new String[] { EMPTY, EMPTY };
                }
                start[1] = s; // new start
            }
            while (true) {
                // find new end
                e = UChar.findFirstEqual(srcc, s, delimiters);
                if (e == -1) {
                    end[count] = srcc.length;
                    break;
                }
                end[count] = e;

                // find new start
                count++;
                s = UChar.findFirstDiff(srcc, e, delimiters);
                if (s == -1) {
                    start[count] = end[count] = srcc.length;
                    break;
                }
                start[count] = s;
            }
            count++;
            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = src.substring(start[i], end[i]);
            }
            return result;
        }
        static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
                                     final CharSequence substring, final int start, final int length)    {
            if (cs instanceof String && substring instanceof String) {
                return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
            }
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;

            while (tmpLen-- > 0) {
                final char c1 = cs.charAt(index1++);
                final char c2 = substring.charAt(index2++);

                if (c1 == c2) {
                    continue;
                }

                if (!ignoreCase) {
                    return false;
                }

                // The same check as in String.regionMatches():
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                        && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                    return false;
                }
            }

            return true;
        }
    }
    public static class UChar{
        public static int findFirstEqual(final char[] source, final int index, final char[] match) {
            for (int i = index; i < source.length; i++) {
                if (equalsOne(source[i], match)) {
                    return i;
                }
            }
            return -1;
        }
        public static boolean equalsOne(final char c, final char[] match) {
            for (char aMatch : match) {
                if (c == aMatch) {
                    return true;
                }
            }
            return false;
        }
        public static int findFirstDiff(final char[] source, final int index, final char[] match) {
            for (int i = index; i < source.length; i++) {
                if (!equalsOne(source[i], match)) {
                    return i;
                }
            }
            return -1;
        }
    }
    public static class UDate {
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
}
