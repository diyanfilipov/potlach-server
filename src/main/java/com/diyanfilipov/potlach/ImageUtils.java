package com.diyanfilipov.potlach;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class ImageUtils {
	public static final int MAX_WIDTH = 1920;
	public static final int MAX_HEIGHT = 1080;
	public static final int MAX_PIXELS = MAX_WIDTH * MAX_HEIGHT;
	public static BufferedImage scaleImage(BufferedImage image){
		if(image == null){
			return image;
		}
		
		BufferedImage scaledImage = null;
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		boolean rotated = false;
		if(width * height > MAX_PIXELS){
			if(width < height){
				rotated = true; 
			}
			
			if(rotated && width > MAX_HEIGHT || height > MAX_WIDTH){
				scaledImage = new BufferedImage(MAX_HEIGHT, MAX_WIDTH, BufferedImage.TYPE_INT_RGB);
				drawImage(image, scaledImage, MAX_HEIGHT, MAX_WIDTH);
				return scaledImage;
			}else if(width > MAX_WIDTH || height > MAX_HEIGHT){
				scaledImage = new BufferedImage(MAX_WIDTH, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
				drawImage(image, scaledImage, MAX_WIDTH, MAX_HEIGHT);
				return scaledImage;
			}
		}
		
		
		
		return image;
	}
	private static void drawImage(BufferedImage originalImage,
			BufferedImage scaledImage, int width, int height) {
		Graphics2D g = scaledImage.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(originalImage, 0, 0, width, height, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
		g.dispose();
	}
}
