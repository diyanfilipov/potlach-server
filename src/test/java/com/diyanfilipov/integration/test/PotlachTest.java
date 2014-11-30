package com.diyanfilipov.integration.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import com.diyanfilipov.potlach.Gift;
import com.diyanfilipov.potlach.GiftObsceneStatus;
import com.diyanfilipov.potlach.GiftObsceneStatus.ObsceneState;
import com.diyanfilipov.potlach.GiftStatus;
import com.diyanfilipov.potlach.GiftStatus.ImageState;
import com.diyanfilipov.potlach.GiftTouchStatus;
import com.diyanfilipov.potlach.GiftTouchStatus.TouchState;
import com.diyanfilipov.potlach.client.PotlachSvcApi;
import com.diyanfilipov.potlach.client.SecuredRestBuilder;
import com.google.common.collect.Lists;

public class PotlachTest extends TestCase{
	private class ErrorRecorder implements ErrorHandler {

		private RetrofitError error;

		@Override
		public Throwable handleError(RetrofitError cause) {
			error = cause;
			return error.getCause();
		}

		public RetrofitError getError() {
			return error;
		}
	}

	
	private ErrorRecorder error = new ErrorRecorder();
	
	// Create an insecure version of our Rest Adapter that doesn't know how
	// to use OAuth.
	private PotlachSvcApi insecurePotlachService = new RestAdapter.Builder()
	.setClient(
			new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
	.setEndpoint(TestUtils.TEST_URL).setLogLevel(LogLevel.FULL)
	.setErrorHandler(error).build().create(PotlachSvcApi.class);
	
	private PotlachSvcApi readWritePotlachUser1 = TestUtils.createPotlachSvcApi(TestUtils.ADMIN, TestUtils.PASSWORD);
	
	
	private static Gift gift = new Gift("Test title");
	
	
	
	@Test
	public void testSetupData() throws InterruptedException{
		List<String> names = Lists.newArrayList(
				"dido",
				"vasil",
				"ivo",
				"george",
				"hannibal",
				"jhon",
				"martin",
				"ivan",
				"kevin",
				"dido1",
				"whilliam",
				"adam",
				"peter",
				"jack",
				"chris",
				"dwayne",
				"phill",
				"phillip",
				"bob",
				"brian");
		
		for (int i = 0; i < names.size(); i++) {
			insecurePotlachService.register(names.get(i), TestUtils.PASSWORD);
		}
		
		List<String> giftChains = Arrays.asList(
				"Nature",
				"Art",
				"Cars",
				"Animals",
				"Cities",
				"Girls",
				"Toys",
				"Hentai",
				"Movies");
		
		
		Random random = new Random();
		File giftsPredefinedDir = new File("predefinedGifts");
		File gifts = new File("gifts");
		if(giftsPredefinedDir.exists() && giftsPredefinedDir.isDirectory()){
			File[] images = giftsPredefinedDir.listFiles();
			if(images.length > 0){
				for(File f : gifts.listFiles()){
					f.delete();
				}
				
				List<Long> idsToTouch = new ArrayList<Long>();
				
				for(int i = 0; i < 50; i++){
					Gift gift = new Gift("Title " + i);
					gift.setDescription(TestUtils.DESCRIPTION);
					String chain = giftChains.get(i%giftChains.size());
					
					int userIdx = i % names.size();
					PotlachSvcApi potlachSvc = TestUtils.createPotlachSvcApi(names.get(userIdx), TestUtils.PASSWORD);
					Gift receivedGift = potlachSvc.postGift(gift, chain);
					assertTrue(receivedGift.getId() > 0);
					
					if(idsToTouch.size() < 10){
						idsToTouch.add(receivedGift.getId());
					}
					
					File giftImage = images[random.nextInt(images.length)];
					
					GiftStatus status = potlachSvc.addGiftImage(receivedGift.getId(), 
							new TypedFile("image/jpeg", giftImage));
					assertEquals(ImageState.READY, status.getImageState());
				}
				
				
				for (int i = 0; i < idsToTouch.size(); i++) {
					long giftId = idsToTouch.get(i);
					switch (i) {
						case 0:
							TestUtils.createPotlachSvcApi("dido", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("vasil", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("ivan", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("kevin", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("whilliam", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("peter", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("jack", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("phill", TestUtils.PASSWORD).touch(giftId);
							break;
						case 1:	
							TestUtils.createPotlachSvcApi("dido", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("jack", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("peter", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("ivo", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("jhon", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("dido1", TestUtils.PASSWORD).touch(giftId);
							break;
						case 2:
							TestUtils.createPotlachSvcApi("martin", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("adam", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("george", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("jhon", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 3:
							TestUtils.createPotlachSvcApi("martin", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("chris", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("dido", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("hannibal", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 4:
							TestUtils.createPotlachSvcApi("jhon", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("phillip", TestUtils.PASSWORD).touch(giftId);
							TestUtils.createPotlachSvcApi("chris", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 5:
							TestUtils.createPotlachSvcApi("hannibal", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 6:
							TestUtils.createPotlachSvcApi("dwayne", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 7:
							TestUtils.createPotlachSvcApi("dwayne", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 8:
							TestUtils.createPotlachSvcApi("bob", TestUtils.PASSWORD).touch(giftId);					
							break;
						case 9:
							TestUtils.createPotlachSvcApi("brian", TestUtils.PASSWORD).touch(giftId);					
							break;
					}
				}
			}
		}
	}
	
	@Test
	public void testGetGiftChains(){
		Collection<Gift> giftChains = readWritePotlachUser1.getAllGiftChains();
		assertTrue(!giftChains.isEmpty());
	}
	
	@Test
	public void testPostingGift() throws Exception {
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertEquals(gift.getTitle(), received.getTitle());
		assertEquals(gift.getDescription(), received.getDescription());
		assertTrue(received.getTouches() == 0);
		assertTrue(received.getId() > 0);
	}
	
	@Test
	public void testAddGiftImageData() throws IOException{
		Gift receivedGift = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(receivedGift.getId() > 0);
		
		File imageFile = new File("gifts/gift2.jpg");
		
		GiftStatus status = readWritePotlachUser1.addGiftImage(receivedGift.getId(), 
				new TypedFile("image/jpeg", imageFile));
		assertEquals(ImageState.READY, status.getImageState());
		
		Response response = readWritePotlachUser1.getGiftImage(receivedGift.getId());
		assertEquals(200, response.getStatus());
		
		InputStream image = response.getBody().in();
		byte[] received = IOUtils.toByteArray(new FileInputStream(imageFile));
		byte[] original = IOUtils.toByteArray(image);
		assertTrue(Arrays.equals(received, original));
		
		
	}
	
	@Test
	public void testPostGetAllGifts(){
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		Collection<Gift> stored = readWritePotlachUser1.getAllGifts();
		assertTrue(!stored.isEmpty());
	}
	
	@Test
	public void testPostGetGift(){
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		Gift stored = readWritePotlachUser1.getGiftById(received.getId());
		assertEquals(stored.getTitle(), received.getTitle());
		assertEquals(stored.getDescription(), received.getDescription());
	}
	
	@Test
	public void testPostGetGiftFromGiftChain(){
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		Collection<Gift> stored = readWritePotlachUser1.getGiftsByChain("cars", 0, 1, true);
		assertTrue(stored.contains(received));
	}
	
	@Test
	public void testGetChainByGift(){
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		Gift chain = readWritePotlachUser1.getGiftChainByGift(received.getId());
		assertTrue(chain.getTitle().equals("cars"));
	}
	
	@Test
	public void testGiftFindByTitle(){
		String[] names = new String[5];
		names[0] = "Sunset";
		names[1] = "Ocean";
		names[2] = "Downtown";
		names[3] = "Downtown Sunset";
		names[4] = "Clouds";
		
		List<Gift> gifts = new ArrayList<Gift>();
		for (int i = 0; i < names.length; i++) {
			gifts.add(new Gift("Test title"));
			gifts.get(i).setTitle(names[i]);
			readWritePotlachUser1.postGift(gifts.get(i), "random");
		}
		
		//search for Sunset
		Collection<Gift> searchResults = readWritePotlachUser1.findByTitle("Sunset", 0, 15, true);
		assertTrue(searchResults.size() == 2);
		
		//search for Ocean
		searchResults = readWritePotlachUser1.findByTitle("Ocean", 0, 15, true);
		assertTrue(searchResults.size() == 1);
	}
	
	@Test
	public void testGiftFindByUploader(){
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		Collection<Gift> searchResults = readWritePotlachUser1.findByUploader(TestUtils.ADMIN, 0, 20, true);
		assertFalse(searchResults.isEmpty());
		assertTrue(searchResults.contains(received));
	}
	
	@Test
	public void testTopGiftGivers(){
		final int maxTouches = 100000;
		String[] names = new String[20];
		names[0] = "Sunset";
		names[1] = "Ocean";
		names[2] = "Downtown";
		names[3] = "Downtown Sunset";
		names[4] = "Clouds";
		names[5] = "Clouds";
		names[6] = "Clouds";
		names[7] = "Clouds";
		names[8] = "Clouds";
		names[9] = "Clouds";
		names[10] = "Clouds";
		names[11] = "Clouds";
		names[12] = "Clouds";
		names[13] = "Clouds";
		names[14] = "Clouds";
		names[15] = "Clouds";
		names[16] = "Clouds";
		names[17] = "Clouds";
		names[18] = "Clouds";
		names[19] = "Clouds";
		
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			Gift gift = new Gift("Test title");
			gift.setTitle(names[i]);
			if(i == 10){
				gift.setTouches(maxTouches);
			}else{
				gift.setTouches(random.nextInt(10000) + 1);
			}
			
			gift = readWritePotlachUser1.postGift(gift, "random");
			assertTrue(gift.getId() > 0);
		}
		
		Collection<Object[]> searchResults = readWritePotlachUser1.getTopGiftGivers(0, 10);
		assertFalse(searchResults.isEmpty());
		Object[] top = searchResults.iterator().next();
		assertTrue(top.length == 2);
	}
	
	@Test
	public void testMostRecentGifts(){
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		Gift postedGift = null;
		for (int i = 0; i < 30; i++) {
			Gift gift = new Gift("Test title " + i);
			c.setTime(date);
			c.add(Calendar.YEAR, -i);
			
			gift.setDateAdded(c.getTime().getTime());
			postedGift = readWritePotlachUser1.postGift(gift, "cars");
		}
		
		
		c.setTime(date);
		c.add(Calendar.YEAR, -10);
		
		Gift parent = readWritePotlachUser1.getGiftChainByGift(postedGift.getId());
		assertTrue(parent != null);
		
		Collection<Gift> searchResults = readWritePotlachUser1.findMostRecentGifts(c.getTime().getTime(), parent.getId());
		assertFalse(searchResults.isEmpty());
		assertTrue(searchResults.size() <= 20);
	}
	
	@Test
	public void testUserTouchesGift(){
		Gift gift = new Gift("Test title");
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		PotlachSvcApi potlachSvcApi = TestUtils.createPotlachSvcApi("ivan", TestUtils.PASSWORD); 
		received = potlachSvcApi.getGiftById(49);
		assertTrue(received.getId() > 0);
		GiftTouchStatus touchStatus = potlachSvcApi.touch(received.getId());
		assertTrue(touchStatus != null);
		assertTrue(touchStatus.getTouchState() == TouchState.TOUCHED);
	}
	
	@Test
	public void testGetGiftTouches(){
		Gift gift = new Gift("Test title");
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		assertEquals(0, received.getTouches());
		GiftTouchStatus touchStatus = readWritePotlachUser1.touch(received.getId());
		assertTrue(touchStatus != null);
		assertTrue(touchStatus.getTouchState() == TouchState.TOUCHED);
		int touches = readWritePotlachUser1.getTouchesCount(received.getId());
		assertTrue(touches == 1);
	}
	
	@Test
	public void testUserTouchesGiftTwice(){
		ErrorRecorder error = new ErrorRecorder();
		
		Gift gift = new Gift("Test title");
		Gift received = readWritePotlachUser1.postGift(gift, "cars");
		assertTrue(received.getId() > 0);
		assertEquals(0, received.getTouches());
		GiftTouchStatus touchStatus = readWritePotlachUser1.touch(received.getId());
		assertTrue(touchStatus != null);
		assertTrue(touchStatus.getTouchState() == TouchState.TOUCHED);
		
		
		
		try{
			PotlachSvcApi readWritePotlachUserWithErrorHndlr = new SecuredRestBuilder()
				.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TestUtils.TEST_URL)
				.setLoginEndpoint(TestUtils.TEST_URL + PotlachSvcApi.TOKEN_PATH)
				.setErrorHandler(error)
				.setUsername(TestUtils.ADMIN).setPassword(TestUtils.PASSWORD).setClientId(TestUtils.CLIENT_ID)
				.build().create(PotlachSvcApi.class);
			
			touchStatus = readWritePotlachUserWithErrorHndlr.touch(received.getId());
			fail("The user was able to touch the Gift more than once!");
		}catch(Exception e){
			assertEquals(HttpStatus.SC_BAD_REQUEST, error.getError()
					.getResponse().getStatus());
		}
		
		received = readWritePotlachUser1.getGiftById(received.getId());
		assertTrue(received != null);
		assertTrue(received.getTouches() == 1);
	}
	
	@Test
	public void testUserUntouchesGift(){
		PotlachSvcApi potlachSvcApi = TestUtils.createPotlachSvcApi("admin", TestUtils.PASSWORD);
		Gift gift = potlachSvcApi.getGiftById(8);
		assertTrue(gift != null);
		long touches = gift.getTouches();
		
		gift = potlachSvcApi.untouch(8, "dido");
		assertEquals(gift.getTouches(), (touches == 0) ? 0 : touches - 1); 
	}
	
	@Test
	public void testMarkGiftAsObscene(){
		GiftObsceneStatus giftObsceneStatus = readWritePotlachUser1.obscene(58);
		assertTrue(giftObsceneStatus.getObsceneState() == ObsceneState.MARKED);
	}
	
	@Test
	public void testDenyGiftAddWithoutOAuth() throws Exception {
		ErrorRecorder error = new ErrorRecorder();

		// Create an insecure version of our Rest Adapter that doesn't know how
		// to use OAuth.
		PotlachSvcApi insecurevideoService = new RestAdapter.Builder()
				.setClient(
						new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TestUtils.TEST_URL).setLogLevel(LogLevel.FULL)
				.setErrorHandler(error).build().create(PotlachSvcApi.class);
		try {
			// This should fail because we haven't logged in!
			insecurevideoService.postGift(gift, "cars");

			fail("Yikes, the security setup is horribly broken and didn't require the user to authenticate!!");

		} catch (Exception e) {
			// Ok, our security may have worked, ensure that
			// we got a 401
			assertEquals(HttpStatus.SC_UNAUTHORIZED, error.getError()
					.getResponse().getStatus());
		}

		// We should NOT get back the video that we added above!
		Collection<Gift> stored = readWritePotlachUser1.getAllGifts();
		assertFalse(stored.contains(gift));
	}
	
	@Test
	public void testRegisterUser(){
		final String username = "testUser";
		final String password = "testPassword";
		ErrorRecorder error = new ErrorRecorder();

		// Create an insecure version of our Rest Adapter that doesn't know how
		// to use OAuth.
		PotlachSvcApi insecurePotlachService = new RestAdapter.Builder()
		.setClient(
				new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TestUtils.TEST_URL).setLogLevel(LogLevel.FULL)
		.setErrorHandler(error).build().create(PotlachSvcApi.class);
		
		insecurePotlachService.register(username, password);
		
		insecurePotlachService = new SecuredRestBuilder()
			.setClient(
				new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TestUtils.TEST_URL)
				.setLoginEndpoint(TestUtils.TEST_URL + PotlachSvcApi.TOKEN_PATH)
				.setLogLevel(LogLevel.FULL)
				.setUsername(username).setPassword(password).setClientId(TestUtils.CLIENT_ID)
				.setErrorHandler(error).build().create(PotlachSvcApi.class);
		
		insecurePotlachService.postGift(gift, "cars");
	}
	
	@Test
	public void testUserAlreadyExists(){
		final String username = "testUser";
		final String password = "testPassword";
		ErrorRecorder error = new ErrorRecorder();
		
		PotlachSvcApi insecurevideoService = new SecuredRestBuilder()
			.setClient(
				new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TestUtils.TEST_URL)
				.setLoginEndpoint(TestUtils.TEST_URL + PotlachSvcApi.TOKEN_PATH)
				.setLogLevel(LogLevel.FULL)
				.setUsername(username).setPassword(password).setClientId(TestUtils.CLIENT_ID)
				.setErrorHandler(error).build().create(PotlachSvcApi.class);
		
		try {
			// This should fail because we haven't logged in!
			insecurevideoService.register(username, password);

			fail("Yikes, the security setup is horribly broken and didn't require the user to authenticate!!");

		} catch (Exception e) {
			// Ok, our security may have worked, ensure that
			// we got a 401
			assertEquals(HttpStatus.SC_BAD_REQUEST, error.getError()
					.getResponse().getStatus());
		}
	}
}
