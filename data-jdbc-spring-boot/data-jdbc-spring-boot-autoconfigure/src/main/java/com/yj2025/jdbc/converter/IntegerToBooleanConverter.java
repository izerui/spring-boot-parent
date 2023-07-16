package com.yj2025.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * @see `https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.custom-converters.configuration`
 */
@ReadingConverter
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
    @Override
    public Boolean convert(Integer source) {
        return source != null && source.equals(1) ? Boolean.TRUE : Boolean.FALSE;
    }
}
