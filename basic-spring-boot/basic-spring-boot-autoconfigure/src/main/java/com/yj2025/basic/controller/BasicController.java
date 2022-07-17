package com.yj2025.basic.controller;

import com.yj2025.basic.support.WebRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicController implements WebRequestAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    protected Logger getLogger() {
        return logger;
    }
}
