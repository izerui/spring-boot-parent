package com.yj2025.sequence;

import org.joda.time.DateTime;

public enum PeriodType {
    DAY{
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyy-MM-dd");
        }
    },MONTH{
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyy-MM");
        }
    },YEAR{
        @Override
        public Period period(DateTime dateTime) {
            return () -> dateTime.toString("yyyy");
        }
    },FOREVER{
        @Override
        public Period period(DateTime dateTime) {
            return () -> "FOREVER";
        }
    };

    public abstract Period period(DateTime dateTime);

    public interface Period {
        String getPeriodFormater();
    }

}
