package com.mj.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author MJ
 */
public class Strings {
    private static final int DELTA = 'a' - 'A';
    public static final String AS = "\\s*";
    public static final String NS = "\\S";
    public static final String DOT_ALL = "(?s)";
    public static final String CHINESE_CHAR = "[\\u4e00-\\u9fa5]";
    public static final String BS = "\\s+";
    private static ScriptEngine scriptEngine;
    private static synchronized ScriptEngine getEngine() {
        if (scriptEngine == null) {
            scriptEngine = new ScriptEngineManager().getEngineByName("js");
        }
        return scriptEngine;
    }

    public static class CapturedText implements Comparable<CapturedText> {
        private final String text;
        private int begin;
        private int end;
        private List<CapturedText> groups;
        public CapturedText(String text, int start, int end) {
            this.text = text;
            this.begin = start;
            this.end = end;
        }
        public void addGroup(CapturedText text) {
            if (groups == null) {
                groups = new ArrayList<>();
            }
            groups.add(text);
        }
        public String getText() {
            return text;
        }
        public int getBegin() {
            return begin;
        }
        public int getEnd() {
            return end;
        }
        @Override
        public String toString() {
            return "text=【" + text + "】, range=[" + begin + ", " + end + ")";
        }
        @Override
        public int compareTo(CapturedText o) {
            return begin - o.begin;
        }
        public List<CapturedText> getGroups() {
            return groups;
        }
        public String getGroupText(int i) {
            if (groups == null || i < 0 || i >= groups.size()) return null;
            return groups.get(i).getText();
        }
    }

    public static List<CapturedText> capturedTexts(String text, String regex1, String regex2) {
        List<CapturedText> groups = Strings.capture(text, regex1);
        List<CapturedText> notGroups = Strings.notCapture(text, groups);
        List<CapturedText> subTexts = Strings.capturedTexts(notGroups, regex2);
        groups.addAll(subTexts);
        groups.sort(null);
        return groups;
    }

    /**
     * 包含了wholeGroups和notGroups（会加上text的start偏移）
     */
    public static List<CapturedText> capturedTexts(List<CapturedText> srcTexts, String regex) {
        List<CapturedText> texts = new ArrayList<>();
        for (CapturedText srcText : srcTexts) {
            texts.addAll(Strings.capturedTexts(srcText, regex));
        }
        texts.sort(null);
        return texts;
    }

    /**
     * 包含了wholeGroups和notGroups（会加上text的start偏移）
     */
    public static List<CapturedText> capturedTexts(CapturedText text, String regex) {
        int offset = text.getBegin();
        String string = text.getText();
        List<CapturedText> subTexts = Strings.capturedTexts(string, regex);
        for (CapturedText subText : subTexts) {
            subText.begin = subText.begin + offset;
            subText.end = subText.end + offset;
        }
        return subTexts;
    }

    /**
     * 包含了wholeGroups和notGroups
     */
    public static List<CapturedText> capturedTexts(String text, String regex) {
        List<CapturedText> groups = Strings.capture(text, regex);
        List<CapturedText> notGroups = Strings.notCapture(text, groups);
        groups.addAll(notGroups);
        groups.sort(null);
        return groups;
    }

    public static List<CapturedText> notCapture(String text, String regex) {
        return notCapture(text, Strings.capture(text, regex));
    }

    public static List<CapturedText> notCapture(String text, List<CapturedText> groups) {
        int len = text.length();
        List<CapturedText> notGroups = new ArrayList<>();
        int start = 0;
        for (CapturedText group : groups) {
            int end = group.getBegin();
            if (end != start) {
                notGroups.add(new CapturedText(text.substring(start, end), start, end));
            }
            start = group.getEnd();
        }
        if (start != len) {
            notGroups.add(new CapturedText(text.substring(start, len), start, len));
        }
        return notGroups;
    }

    public static List<CapturedText> capture(String text, String regex) {
        return capture(text, Pattern.compile(regex));
    }

    public static List<CapturedText> capture(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        List<CapturedText> strs = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            if (group == null) continue;
            CapturedText groupText = new CapturedText(group, matcher.start(), matcher.end());
            strs.add(groupText);
            int gc = matcher.groupCount();
            for (int i = 1; i <= gc; i++) {
                String groupIdx = matcher.group(i);
                if (groupIdx == null) continue;
                groupText.addGroup(new CapturedText(groupIdx, matcher.start(i), matcher.end(i)));
            }
        }
        return strs;
    }

    public static String replace(String text, String regex,
                                 Function<CapturedText, String> func) {
        return replace(text, Pattern.compile(regex), func);
    }

    public static String replace(String text, Pattern pattern,
                                 Function<CapturedText, String> func) {
        StringBuilder sb = new StringBuilder(text);
        List<CapturedText> groups = capture(text, pattern);
        int gSize = groups.size();
        for (int i = gSize - 1; i >= 0; i--) {
            CapturedText group = groups.get(i);
            String replacement = func.apply(group);
            replacement = replacement.replace("<*>", group.text);
            if (replacement.contains("<")) {
                int subGSize = (group.groups != null) ? group.groups.size() : 0;
                for (int j = 0; j < subGSize; j++) {
                    String subGText = group.groups.get(j).text;
                    replacement = replacement.replace("<" + j + ">", subGText);
                    int lastIdx = j - subGSize;
                    replacement = replacement.replace("<" + lastIdx + ">", subGText);
                }
            }
            sb.replace(group.begin, group.end, replacement);
        }
        text = sb.toString();

        List<CapturedText> exprGroups = capture(text, "\\[\\[.+\\]\\]");
        if (exprGroups.size() == 0) return text;

        ScriptEngine se = getEngine();
        for (CapturedText group : exprGroups) {
            String txt = group.getText();
            String expr = txt.substring(2, txt.length() - 2);
            try {
                expr = se.eval(expr).toString();
            } catch (Exception ignored) {}
            text = text.replace(txt, expr);
        }
        return text;
    }

    public static String replace(String text, String regex, String replacement) {
        return replace(text, Pattern.compile(regex), replacement);
    }

    public static String replace(String text, Pattern pattern, String replacement) {
        if (replacement.contains("<")) {
            return replace(text, pattern, (txt) -> replacement);
        }
        return pattern.matcher(text).replaceAll(replacement);
    }

    public static List<String> split(String string, String seperator) {
        String[] strings = StringUtils.split(string, seperator);
        return Arrays.asList(strings);
    }

    public static String join(Object ...subpaths) {
        return joinBySeparator(null, subpaths);
    }

    public static String joinBySeparator(String seperator, Object ...subpaths) {
        if (ArrayUtils.isEmpty(subpaths)) return null;
        if (seperator == null) {
            seperator = "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object subpath : subpaths) {
            if (subpath != null) {
                sb.append(seperator);
                sb.append(subpath.toString());
            }
        }
        if (seperator.length() > 0) {
            int index = sb.indexOf(seperator);
            sb.delete(index, index + seperator.length());
        }
        return sb.toString();
    }

    public static String trim(String text) {
        return trimRight(trimLeft(text));
    }

    public static String trimRight(String text) {
        return text.replaceFirst(DOT_ALL + AS + "$", "");
    }

    public static String trimLeft(String text) {
        return text.replaceFirst(DOT_ALL + "^" + AS, "");
    }

    public static boolean isEmpty(String source) {
        return source == null || source.equals("");
    }

    public static boolean isBlank(String source) {
        return source == null || source.trim().equals("");
    }

    /**
     * 首字母变小写
     * @return TestCase -> testCase
     */
    public static String firstLetterLowercase(String source) {
        if (isEmpty(source)) return source;
        StringBuilder res = processFirstLetterLowercase(source);
        int len = source.length();
        for (int i = 1; i < len; i++) {
            res.append(source.charAt(i));
        }
        return res.toString();
    }

    private static StringBuilder processFirstLetterLowercase(String source) {
        StringBuilder res = new StringBuilder();
        // 拼接首字符
        char firstChar = source.charAt(0);
        if (isBigLetter(firstChar)) {
            res.append((char) (firstChar + DELTA));
        } else {
            res.append(firstChar);
        }
        return res;
    }

    /**
     * 驼峰 -> 下划线
     * @return TestCase -> test_case
     */
    public static String camel2underline(String source) {
        if (isEmpty(source)) return source;
        StringBuilder res = processFirstLetterLowercase(source);
        // 其他字符
        int len = source.length();
        for (int i = 1; i < len; i++) {
            char c = source.charAt(i);
            if (isBigLetter(c)) {
                res.append("_");
                res.append((char) (c + DELTA));
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }

    /**
     * 下划线 -> 小驼峰
     * @return test_case -> testCase
     */
    public static String underline2smallCamel(String source) {
        return underline2camel(source, false);
    }

    /**
     * 下划线 -> 大驼峰
     * @return test_case -> TestCase
     */
    public static String underline2bigCamel(String source) {
        return underline2camel(source, true);
    }

    private static String underline2camel(String source, boolean big) {
        if (isEmpty(source)) return source;
        StringBuilder res = new StringBuilder();
        // 其他字符
        int len = source.length();
        // 上一个字符是下划线
        boolean prevUnderline = false;
        for (int i = 0; i < len; i++) {
            char c = source.charAt(i);
            if (c == '_') {
                prevUnderline = true;
                continue;
            }
            if (res.length() == 0) { // 首字符
                if (big && isSmallLetter(c)) { // 大驼峰
                    res.append((char) (c - DELTA));
                } else if (!big && isBigLetter(c)) { // 小驼峰
                    res.append((char) (c + DELTA));
                } else {
                    res.append(c);
                }
            } else if (prevUnderline && isSmallLetter(c)) {
                res.append((char) (c - DELTA));
            } else {
                res.append(c);
            }
            prevUnderline = false;
        }
        return res.toString();
    }

    public static boolean isBigLetter(char source) {
        return source >= 'A' && source <= 'Z';
    }

    public static boolean isSmallLetter(char source) {
        return source >= 'a' && source <= 'z';
    }

    /**
     * 返回第一个不为empty的字符串
     */
    public static String notEmpty(String... sources) {
        if (sources == null) return null;
        for (String source : sources) {
            if (!isEmpty(source)) return source;
        }
        return null;
    }

    public static String joinPaths(Object... paths) {
        StringBuilder sb = new StringBuilder();
        for (Object path : paths) {
            sb.append(path).append("/");
        }
        int len = sb.length();
        if (len > 0) {
            sb.deleteCharAt(len - 1);
        }
        return sb.toString();
    }

    public static String base64(String str) {
        if (isEmpty(str)) return str;
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
}
