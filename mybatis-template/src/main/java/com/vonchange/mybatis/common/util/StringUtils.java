/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vonchange.mybatis.common.util;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.vonchange.mybatis.common.util.StringPool.EMPTY;
public class StringUtils {
    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        return remove(uuid, "-");
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
                } while((i = s.indexOf(sub, c)) != -1);

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
    /**
     * 简单模板实现 String str = "I'm not a {0}, age is {1,number,short}", height is
     * {2,number,#.#};
     *
     * @param pattern
     * @param arguments
     * @return String
     */
    public static String format(String pattern, Object... arguments) {
        MessageFormat temp = new MessageFormat(pattern);
        return temp.format(arguments);
    }
    public static String strNums(String str, String split, int num) {
        if(num==0){
            return "";
        }
        StringBuilder fullStr = new StringBuilder();
        for (int i = 0; i < num; i++) {
            fullStr.append(str).append(split);
        }
        return fullStr.substring(0, fullStr.length()-split.length());
    }
    public static String strList(List<String> strs, String split) {
        if(null==strs||strs.isEmpty()){
            return "";
        }
        StringBuilder fullStr = new StringBuilder();
        for (String string : strs) {
            fullStr.append(string).append(split);
        }
        return fullStr.substring(0, fullStr.length()-split.length());
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

    public static final char UNDERLINE='_';
    public static String camelToUnderline(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=param.charAt(i);
            if (Character.isUpperCase(c)){
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 正则表达式 实现
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
        tplStr= replaceEach(tplStr, patternList.toArray(new String[patternList.size()]), replaceStrList.toArray(new String[replaceStrList.size()]));
        return tplStr;
    }

    /*public static String tpl(String tplStr, Map<String, Object> data) {
        StringTemplateParser stp = new StringTemplateParser();

        return stp.parse(tplStr,macroName -> {
                return ConvertUtil.toString(data.get(macroName));
        });
    }*/

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
        return CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
    }

    public static String replaceEach(final String text, final String[] searchList, final String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }
    private static String replaceEach(
            final String text, final String[] searchList, final String[] replacementList, final boolean repeat, final int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

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

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
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
        return CharSequenceUtils.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
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

        for(int i = 0; i < size; ++i) {
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
        int maxparts = (src.length() / delimiter.length()) + 2;		// one more for the last
        int[] positions = new int[maxparts];
        int dellen = delimiter.length();

        int i, j = 0;
        int count = 0;
        positions[0] = - dellen;
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
     * @param src    source to examine
     * @param d      string with delimiter characters
     *
     * @return array of tokens
     */
    public static String[] splitc(final String src, final String d) {
        if ((d.length() == 0) || (src.length() == 0)) {
            return new String[] {src};
        }
        return splitc(src, d.toCharArray());
    }
    /**
     * Splits a string in several parts (tokens) that are separated by delimiter
     * characters. Delimiter may contains any number of character and it is
     * always surrounded by two strings.
     *
     * @param src			source to examine
     * @param delimiters	char array with delimiter characters
     *
     * @return array of tokens
     */
    public static String[] splitc(final String src, final char[] delimiters) {
        if ((delimiters.length == 0) || (src.length() == 0) ) {
            return new String[] {src};
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (CharUtil.equalsOne(srcc[0], delimiters)) {	// string starts with delimiter
            end[0] = 0;
            count++;
            s = CharUtil.findFirstDiff(srcc, 1, delimiters);
            if (s == -1) {							// nothing after delimiters
                return new String[] {EMPTY, EMPTY};
            }
            start[1] = s;							// new start
        }
        while (true) {
            // find new end
            e = CharUtil.findFirstEqual(srcc, s, delimiters);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = CharUtil.findFirstDiff(srcc, e, delimiters);
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
/*    public static void main(String[] args) {

        String template = "Hello ${foo}. Today is ${dayName}.";
        // prepare data
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "Jodd");
        map.put("dayName", "Sunday");
    }*/
}
