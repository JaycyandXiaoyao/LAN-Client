package com.client;
public class MyOut implements Log {

	@Override
	public void println(String msg) {
		System.out.println(msg);
	}

	@Override
	public void println(Object object) {
		System.out.println(object.toString());
	}

}
