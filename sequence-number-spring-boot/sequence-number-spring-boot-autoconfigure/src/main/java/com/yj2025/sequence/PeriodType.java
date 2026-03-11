package com.yj2025.sequence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum PeriodType {
    DAY("yyyyMMdd") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yyyyMMdd").format(dateTime);
        }

    }, MONTH("yyyyMM") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yyyyMM").format(dateTime);
        }
    }, YEAR("yyyy") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yyyy").format(dateTime);
        }
    }, YEAR2("yy") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yy").format(dateTime);
        }
    }, YEAR_MONTH2("yymm") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yymm").format(dateTime);
        }
    }, YEAR_WW2("yyww") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yyww").format(dateTime);
        }
    }, DAY2("yyMMdd") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> DateTimeFormatter.ofPattern("yyMMdd").format(dateTime);
        }
    }, FOREVER("FOREVER") {
        @Override
        public Period period(LocalDateTime dateTime) {
            return () -> "FOREVER";
        }
    };

    public String formateStr;

    PeriodType(String formateStr) {
        this.formateStr = formateStr;
    }

    public abstract Period period(LocalDateTime dateTime);

    public interface Period {
        String getPeriodFormatter();
    }

}
