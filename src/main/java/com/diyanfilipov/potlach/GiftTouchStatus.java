package com.diyanfilipov.potlach;

public class GiftTouchStatus {
	public enum TouchState {
		TOUCHED, UNTOUCHED;
	}
	
	private TouchState touchState;
	
	public GiftTouchStatus(TouchState touchState){
		this.touchState = touchState;
	}
	
	public TouchState getTouchState(){
		return touchState;
	}
}
