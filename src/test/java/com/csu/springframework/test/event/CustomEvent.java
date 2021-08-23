package com.csu.springframework.test.event;

import com.csu.springframework.context.event.ApplicationContextEvent;

public class CustomEvent extends ApplicationContextEvent {

    private Long id;
    private String message;


    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public CustomEvent(Object source, Long id, String message) {
        super(source);
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
