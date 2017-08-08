package agent;

import java.io.Serializable;

public class Message implements Serializable {
    int sender;
    int receiver;
    MESSAGE_TYPE type;
    Block block;

    public enum MESSAGE_TYPE {
        READY, NEW_BLOCK
    }

    @Override
    public String toString() {
        return String.format("Message {type=%s, sender=%d, receiver=%d, block=%s}", type, sender, receiver, block);
    }

    static class MessageBuilder {
        private final Message message = new Message();

        MessageBuilder withSender(final int sender) {
            message.sender = sender;
            return this;
        }

        MessageBuilder withReceiver(final int receiver) {
            message.receiver = receiver;
            return this;
        }

        MessageBuilder withType(final MESSAGE_TYPE type) {
            message.type = type;
            return this;
        }

        MessageBuilder withBlock(final Block block) {
            message.block = block;
            return this;
        }

        Message build() {
            return message;
        }

    }
}
