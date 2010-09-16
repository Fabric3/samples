package org.fabric3.samples.chat;

import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;

/**
 * Observes the chat channel and outputs messages to the console.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class ChatListener {

    @Consumer("chatChannel")
    public void onMessage(Message message) {
        System.out.println(message.getName() + ": " + message.getMessage());
    }
}
