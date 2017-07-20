package agent;

import java.io.Serializable;

public class Message implements Serializable {
    public int sender;
    public int receiver;
    public MESSAGE_TYPE type;
    public Block<?> block;

    public enum MESSAGE_TYPE {
        READY, RESPONSE, FAILURE, SUCCESS
    }

    @Override
    public String toString() {
        return String.format("Message {type=%s, sender=%d, receiver=%d, block=%s}", type, sender, receiver, block);
    }

    public static class MessageBuilder {
        private final Message message = new Message();

        public MessageBuilder withSender(final int sender) {
            message.sender = sender;
            return this;
        }

        public MessageBuilder withReceiver(final int receiver) {
            message.receiver = receiver;
            return this;
        }

        public MessageBuilder withType(final MESSAGE_TYPE type) {
            message.type = type;
            return this;
        }

        public MessageBuilder withBlock(final Block<?> block) {
            message.block = block;
            return this;
        }

        public Message build() {
            return message;
        }

    }
}
