package com.diyanfilipov.potlach.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.diyanfilipov.potlach.Gift;

public class GiftFileManager {
	
	private static final Path targetDir = Paths.get("gifts");
	
	public static GiftFileManager get() throws IOException{
		return new GiftFileManager();
	}
	
	private GiftFileManager() throws IOException{
		if(!Files.exists(targetDir)){
			Files.createDirectories(targetDir);
		}
	}
	
	private Path getGiftPath(Gift gift){
		assert(gift != null);
		
		return targetDir.resolve("gift" + gift.getId() + ".jpg");
	}
	
	public boolean hasGiftData(Gift gift){
		Path source = getGiftPath(gift);
		return Files.exists(source);
	}
	
	public void copyGiftImage(Gift gift, OutputStream out) throws IOException{
		Path source = getGiftPath(gift);
		if(!Files.exists(source)){
			throw new FileNotFoundException("Unable to find the referenced image file for giftId:" + gift.getId());
		}
		Files.copy(source, out);
	}
	
	public void saveGiftImage(Gift gift, InputStream giftImage) throws IOException{
		assert(giftImage != null);
		
		Path target = getGiftPath(gift);
		Files.copy(giftImage, target, StandardCopyOption.REPLACE_EXISTING);
	}
}
