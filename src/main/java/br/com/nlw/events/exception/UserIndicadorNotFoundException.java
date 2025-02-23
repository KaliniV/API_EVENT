package br.com.nlw.events.exception;

public class UserIndicadorNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UserIndicadorNotFoundException (String msg) {
		super(msg);
	}
}
