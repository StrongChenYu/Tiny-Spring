package com.csu.springframework.context.event;

import com.csu.springframework.context.ApplicationEvent;

public class ApplicationContextEvent extends ApplicationEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ApplicationContextEvent(Object source) {
        super(source);
    }

    public final ApplicationEvent getApplicationContext() {
        return (ApplicationEvent) source;
    }
}
