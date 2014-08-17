package com.otv.entity;

//import org.springframework.stereotype.Component;

//@Component
public class ReturnMsg {
	private String retCode;
	private String retMsg;
	
	public ReturnMsg(String retCode, String retMsg)
	{
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

}
