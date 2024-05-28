package com.yj2025.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import java.sql.Date;
import java.time.YearMonth;

public class YearMonthToLocalDateConverter implements Converter<YearMonth, Date> {

  @Override
  public Date convert(@NonNull YearMonth source) {
    return Date.valueOf(source.atDay(1));
  }
}
