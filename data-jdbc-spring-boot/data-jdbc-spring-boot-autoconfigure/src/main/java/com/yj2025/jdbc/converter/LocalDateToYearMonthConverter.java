package com.yj2025.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import java.sql.Date;
import java.time.YearMonth;

@ReadingConverter
public class LocalDateToYearMonthConverter implements Converter<Date, YearMonth> {

  @Override
  public YearMonth convert(@NonNull Date source) {
    return YearMonth.from(source.toLocalDate());
  }
}
