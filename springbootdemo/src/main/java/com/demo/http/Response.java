package com.demo.http;

public class Response<T> {

	private int codes = 200;
	private String msg = "OK";
	private T data;

	public Response(T valueOf) {
		this.data = valueOf;
	}

	public Response<T> OK() {
		this.codes = 200;
		this.msg = "OK";
		return this;
	}

	public Response<T> NO() {
		this.codes = 404;
		this.msg = "NO";
		return this;
	}

	public Response() {
	}

	public Response(int codes, String msg, T data) {
		this.codes = codes;
		this.msg = msg;
		this.data = data;
	}

	public int getCodes() {
		return codes;
	}

	public void setCodes(int codes) {
		this.codes = codes;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
