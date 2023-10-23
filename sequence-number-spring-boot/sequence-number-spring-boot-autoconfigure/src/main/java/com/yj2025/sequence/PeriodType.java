package com.yj2025.sequence;

import org.joda.time.DateTime;

public enum PeriodType {
    DAY("yyyyMMdd") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyyMMdd");
        }

    }, MONTH("yyyyMM") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyyMM");
        }
    }, YEAR("yyyy") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyy");
        }
    }, YEAR2("yy") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yy");
        }
    }, YEAR_MONTH2("yymm") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yymm");
        }
    }, YEAR_WW2("yyww") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyww");
        }
    }, DAY2("yyMMdd") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyMMdd");
        }
    }, FOREVER("FOREVER") {
        @Override
        public Period period(DateTime dateTime) {
            return () -> "FOREVER";
        }
    };

    public String formateStr;

    PeriodType(String formateStr) {
        this.formateStr = formateStr;
    }

    public abstract Period period(DateTime dateTime);

    public interface Period {
        String getPeriodFormatter();
    }

}
