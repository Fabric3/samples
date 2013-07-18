package org.fabric3.samples.hibernate;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class MessageList {
    private List<Message> messages = Collections.emptyList();

    public MessageList() {
    }

    public MessageList(List<Message> messages) {
        this.messages = messages;
    }

    @JsonProperty("messages")
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
