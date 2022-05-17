package com.yj2025.oauth2.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

public class ExceptionUtils {

    public static void wrapExceptions(RunnableWrapper runnable) {
        wrapExceptions(runnable, Strings.EMPTY);
    }

    public static void wrapExceptions(RunnableWrapper runnable, String message) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(message)) {
                throw new RuntimeException(message + " " + e.getMessage(), e);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void wrapExceptions(RunnableWrapper runnable, RuntimeException throwE) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (throwE != null) {
                throw throwE;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier) {
        return wrapExceptions(tSupplier, Strings.EMPTY);
    }

    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier, RuntimeException throwE) {
        try {
            return tSupplier.get();
        } catch (Exception e) {
            if (throwE != null) {
                throw throwE;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T wrapExceptions(SupplierWrapper<T> tSupplier, String errMessage) {
        try {
            return tSupplier.get();
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(errMessage)) {
                throw new RuntimeException(errMessage + " " + e.getMessage(), e);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public interface SupplierWrapper<T> {
        T get() throws Exception;
    }

    public interface RunnableWrapper {
        void run() throws Exception;
    }

}
