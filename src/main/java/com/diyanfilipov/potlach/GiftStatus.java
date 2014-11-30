package com.diyanfilipov.potlach;

public class GiftStatus {
	public enum ImageState{
        READY, PROCESSING;
    }

    private ImageState imageState;

    public GiftStatus(ImageState state) {
    	super();
        this.imageState = state;
    }

	public ImageState getImageState() {
		return imageState;
	}

	public void setImageState(ImageState imageState) {
		this.imageState = imageState;
	}
}
