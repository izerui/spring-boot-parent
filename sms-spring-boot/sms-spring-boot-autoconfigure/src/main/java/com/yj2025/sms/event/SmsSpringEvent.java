package com.yj2025.sms.event;

import com.yj2025.sms.providers.SmsExecuteContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class SmsSpringEvent extends ApplicationEvent {

    @Getter
    private SmsExecuteContext context;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SmsSpringEvent(Object source) {
        super(source);
    }

    public SmsSpringEvent(Object source, SmsExecuteContext context) {
        super(source);
        this.context = context;
    }
}
