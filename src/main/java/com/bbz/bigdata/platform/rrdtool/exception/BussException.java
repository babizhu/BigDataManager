package com.bbz.bigdata.platform.rrdtool.exception;

/**
 * 自定义业务异常
 * @author weiran
 *
 */
public class BussException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6426467058895704531L;

	public BussException(int code){
		this.code=code;
		this.msg=getMsg(code);
	}
	
	public BussException(int code,String msg){
		this.code=code;
		this.msg=msg;
	}
	
	private int code;
	private String msg;
	
	public final static int UNITTYPE_NOT_MATCHED=1; 
	public final static int UNIT_NOT_MATCHED=2; 
	public final static int CAN_NOT_TO_PERCENT=3;
	public final static int CAN_NOT_FIND_TOTAL =4;

	private String getMsg(int code){
		switch (code) {
			case UNITTYPE_NOT_MATCHED:return "单位类型不同";
			case UNIT_NOT_MATCHED:return "单位不同";
			case CAN_NOT_TO_PERCENT:return "不能转换成百分比";
			case CAN_NOT_FIND_TOTAL:return "未找到总量";

		default:return "未知异常代码:"+code;
		}
	}
	
	@Override
	public String getMessage() {
		return this.msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
