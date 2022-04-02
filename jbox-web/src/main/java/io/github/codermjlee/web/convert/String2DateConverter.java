package io.github.codermjlee.web.convert;

import io.github.codermjlee.web.msg.Msgs;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class String2DateConverter implements Converter<String, Date> {
    private static final String[] FMTS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};
    @Override
    public Date convert(String s) {
        if (s.length() == 0) return null;
        try {
            return new Date(Long.parseLong(s));
        } catch (Exception e) {
            for (String fmt : FMTS) {
                try {
                    return new SimpleDateFormat(fmt).parse(s);
                } catch (Exception ignored) {}
            }
        }
        return Msgs.DATE_FMT_ERROR.raise();
    }
}
