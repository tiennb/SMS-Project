package net.everythingandroid.smspopup.util;

/**
 * Message is a Custom Object to encapsulate message information/fields
 * 
 * @author Adil Soomro
 *
 */
public class ChatMessage {
	/**
	 * The content of the message
	 */
	private String messageChat;
	private String timeChat;
	/**
	 * boolean to determine, who is sender of this message
	 */
	private boolean receiver;

	/**
	 * boolean to determine, whether the message is a status message or not. it
	 * reflects the changes/updates about the sender is writing, have entered
	 * text etc
	 */

	/**
	 * Constructor to make a Message object
	 */
	public ChatMessage(String message, String time, boolean isReceiver) {
		super();
		this.messageChat = message;
		this.timeChat = time;
		this.receiver = isReceiver;
	}

	public String getMessageChat() {
		return messageChat;
	}

	public void setMessageChat(String messageChat) {
		this.messageChat = messageChat;
	}

	public String getTimeChat() {
		return timeChat;
	}

	public void setTimeChat(String timeChat) {
		this.timeChat = timeChat;
	}

	public boolean isReceiver() {
		return receiver;
	}

	public void setReceiver(boolean isReceiver) {
		this.receiver = isReceiver;
	}

}
