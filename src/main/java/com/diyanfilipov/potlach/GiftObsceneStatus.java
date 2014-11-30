package com.diyanfilipov.potlach;

public class GiftObsceneStatus {
	public enum ObsceneState {
        MARKED, UNMARKED
    }

    private ObsceneState obsceneState;

    public GiftObsceneStatus(ObsceneState obsceneState){
        this.obsceneState = obsceneState;
    }

    public ObsceneState getObsceneState() {
        return obsceneState;
    }
}
