package com.yj2025.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * @see `https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.custom-converters.configuration`
 */
@WritingConverter
public class BooleanToStringConverter implements Converter<Boolean, String> {

    @Override
    public String convert(Boolean source) {
        return source != null && source ? "T" : "F";
    }
}

