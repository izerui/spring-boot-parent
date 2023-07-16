package com.yj2025.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * @see `https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.custom-converters.configuration`
 */
@ReadingConverter
public class StringToBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(String source) {
        return source != null && source.equalsIgnoreCase("T") ? Boolean.TRUE : Boolean.FALSE;
    }
}