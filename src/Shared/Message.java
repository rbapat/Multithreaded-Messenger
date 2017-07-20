package Shared;

import java.util.Arrays;

public class Message {

	private String sender;
	private String operation;
	private String target;
	private String message;
	private String payload;

	public Message(String sender, String operation, String target, String message) {
		this.sender = sender;
		this.operation = operation;
		this.target = target;
		this.message = message;
		payload = (sender + "#" + operation + "#" + target + "#" + message + "#");
	}

	public Message(String payload) {
		String[] comps = payload.split("#");
		if (comps.length == 4) {
			this.sender = comps[0];
			this.operation = comps[1];
			this.target = comps[2];
			this.message = comps[3];
			this.payload = payload;
		}
		else {
			this.sender = null;
			this.operation = null;
			this.target = null;
			this.message = null;
			this.payload = null;
		}
	}

	public String getSender() {
		return sender;
	}

	public String getOperation() {
		return operation;
	}

	public String getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}

	public String getPayload() {
		return payload;
	}

	public String toString() {
		return ("sender: " + sender + ", op: " + operation + ", target: " + target + ", payload: " + payload);
	}

}