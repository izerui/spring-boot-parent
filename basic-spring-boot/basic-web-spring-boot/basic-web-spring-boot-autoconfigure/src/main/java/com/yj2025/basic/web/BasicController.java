package com.yj2025.basic.web;

import com.yj2025.basic.web.support.WebRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicController implements WebRequestAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

}
