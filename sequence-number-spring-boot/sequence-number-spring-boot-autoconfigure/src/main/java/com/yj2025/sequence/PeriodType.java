package com.yj2025.sequence;

import org.joda.time.DateTime;

public enum PeriodType {
    DAY {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyyMMdd");
        }
    }, MONTH {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyyMM");
        }
    }, YEAR {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyy");
        }
    }, YEAR2 {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yy");
        }
    }, YEAR_MONTH2 {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yymm");
        }
    }, YEAR_WW2 {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyww");
        }
    }, DAY2 {
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyMMdd");
        }
    }, FOREVER {
        @Override
        public Period period(DateTime dateTime) {
            return () -> "FOREVER";
        }
    };

    public abstract Period period(DateTime dateTime);

    public interface Period {
        String getPeriodFormatter();
    }

}
