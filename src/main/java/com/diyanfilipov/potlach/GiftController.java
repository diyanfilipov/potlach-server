package com.diyanfilipov.potlach;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sun.net.www.content.image.gif;

import com.diyanfilipov.potlach.GiftObsceneStatus.ObsceneState;
import com.diyanfilipov.potlach.GiftStatus.ImageState;
import com.diyanfilipov.potlach.GiftTouchStatus.TouchState;
import com.diyanfilipov.potlach.client.PotlachSvcApi;
import com.diyanfilipov.potlach.data.GiftFileManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;


@Controller
public class GiftController {

	public static final Log LOG = LogFactory.getLog(GiftController.class);
	
	@Autowired
	private GiftRepository giftRepository;
	
	@Autowired
	private GiftTouchesRepository giftTouchesRepository;
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{giftChain}", method = RequestMethod.POST)
	public @ResponseBody Gift postGift(
			@RequestBody Gift gift, 
			@PathVariable("giftChain") String giftChain,
			Principal p){
		LOG.info("Posting Gift to chain " + giftChain);
		String username = p.getName();
		gift.setUploader(username);
		
		Gift chain = giftRepository.findByTitleAndParent(giftChain, 0);
		if(chain == null){
			chain = new Gift(giftChain);
		}
		giftRepository.save(chain);
		gift.setParent(chain.getId());
		
		giftRepository.save(gift);
		
		return gift;
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_IMAGE_PATH, method = RequestMethod.POST)
	public @ResponseBody GiftStatus addGiftImage(
			@PathVariable(PotlachSvcApi.ID_PARAMETER) long id,
			@RequestParam(PotlachSvcApi.DATA_PARAMETER) MultipartFile data,
			HttpServletResponse response){
		Gift gift = giftRepository.findOne(id);
		if(gift != null){
			try {
				InputStream inputStream = data.getInputStream();
				BufferedImage image = ImageUtils.scaleImage(ImageIO.read(inputStream));
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", os);
				inputStream = new ByteArrayInputStream(os.toByteArray());
				GiftFileManager giftFileManager = GiftFileManager.get();
				giftFileManager.saveGiftImage(gift, inputStream);
				GiftStatus status = new GiftStatus(ImageState.READY);
				return status;
			} catch (IOException e) {
				LOG.error("Error occured while saving image data.");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}else{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		return null;
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_IMAGE_PATH, method = RequestMethod.GET)
	public void getGiftImage(@PathVariable(PotlachSvcApi.ID_PARAMETER) long id,
			HttpServletResponse response){
		Gift gift = giftRepository.findOne(id);
		if(gift != null){
			try {
				GiftFileManager videoFileManager = GiftFileManager.get();
				videoFileManager.copyGiftImage(gift, response.getOutputStream());
			} catch (IOException e) {
				LOG.error("Error occured while obtaining image data.");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}else{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_CHAIN_PATH + "/{giftChain}", method = RequestMethod.GET)
	public @ResponseBody Collection<Gift> getGiftsByChain(
			@PathVariable("giftChain") String giftChain,
			@RequestParam("s") int start,
			@RequestParam("e") int end,
			@RequestParam("o") boolean showObscene){

		Collection<Gift> gifts = Lists.newArrayList();

		Gift chain = giftRepository.findByTitleAndParent(giftChain, 0);
		if(chain != null){
			LOG.debug("Getting Gifts from " + start + " to " + end);
			Pageable maxResults = new PageRequest((int) start, (int) end);
			if(showObscene){
				gifts = giftRepository.findByParentOrderByDateAddedDesc(chain.getId(), maxResults);
			}else{
				gifts = giftRepository.findByParentAndObsceneFalseOrderByDateAddedDesc(chain.getId(), maxResults);
			}
		}
		return gifts;
	}
	
	@RequestMapping(value = PotlachSvcApi.ALL_GIFT_CHAINS_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Gift> getAllGiftChains(){
		return giftRepository.findByParentOrderByTitleAsc(0);
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}", method = RequestMethod.GET)
	public @ResponseBody Gift getGiftById(@PathVariable("id") long id){
		return giftRepository.findOne(id);
	}

	@RequestMapping(value = PotlachSvcApi.GIFT_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Gift> getAllGifts(){
		return Lists.newArrayList(giftRepository.findAll());
	}

	@RequestMapping(value = PotlachSvcApi.GIFT_CHAIN_BY_GIFT_PATH + "/{id}", method = RequestMethod.GET)
	public @ResponseBody Gift getGiftChainByGift(@PathVariable("id") long id){
		Gift gift = giftRepository.findOne(id);
		if(gift != null){
			Gift chain = giftRepository.findOne(gift.getParent());
			return chain;
		}
		return null;
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_USER_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Gift> findByUploader(
			@RequestParam("user") String user,
			@RequestParam("s") int start,
			@RequestParam("e") int end,
			@RequestParam("o") boolean showObscene){
		LOG.debug("Getting Gifts by uploader " + user + " from " + start + " to " + end);
		Pageable maxResults = new PageRequest((int) start, (int) end);
		Collection<Gift> giftsByUploader = Lists.newArrayList();
		if(showObscene){
			giftsByUploader = giftRepository.findByUploaderOrderByDateAddedDesc(user, maxResults);
		}else{
			giftsByUploader = giftRepository.findByUploaderAndObsceneFalseOrderByDateAddedDesc(user, maxResults);
		}

		return giftsByUploader;
	}

	@RequestMapping(value = PotlachSvcApi.GIFT_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Gift> findByTitle(
			@RequestParam("title") String title,
			@RequestParam("s") int page,
			@RequestParam("e") int size,
			@RequestParam("o") boolean showObscene){
		
		Pageable maxResults = new PageRequest(page, size);
		Collection<Gift> giftsByTitle = Lists.newArrayList();
		if(showObscene){
			giftsByTitle = giftRepository.findByTitleContainingOrderByDateAddedDesc(title, maxResults);
		}else{
			giftsByTitle = giftRepository.findByTitleContainingAndObsceneFalseOrderByDateAddedDesc(title, maxResults);
		}
		return giftsByTitle;
	}
	
	
	@RequestMapping(value = PotlachSvcApi.GIFT_MOST_RECENT_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody List<Gift> findMostRecentGifts(@RequestParam("date") long period, @RequestParam("parent") long parent){
		Pageable maxResults = new PageRequest(0, 20);
		List<Gift> mostRecentGifts = giftRepository.findByDateAddedGreaterThanEqualAndParentOrderByDateAddedDesc(period, parent, maxResults);
		if(mostRecentGifts != null){
			return mostRecentGifts;
		}
		return Lists.newArrayList();
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}/touch", method = RequestMethod.POST)
	public @ResponseBody GiftTouchStatus touch(
			@PathVariable("id") long id,
			HttpServletResponse response,
			Principal p) throws IOException{
		
 		Gift gift = giftRepository.findOne(id);
		if(gift == null){
			return null;
		}
		
		String username = p.getName();
		GiftTouches giftTouches = getTouchesByUser(id, username);
		if(giftTouches == null){
			giftTouches = new GiftTouches(id, username);
			giftTouchesRepository.save(giftTouches);
			gift.setTouches(gift.getTouches() + 1);
			giftRepository.save(gift);
			return new GiftTouchStatus(TouchState.TOUCHED);
		}else{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new GiftTouchStatus(TouchState.UNTOUCHED);
		}
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}/touches", method = RequestMethod.GET)
	public @ResponseBody int getTouchCount(
			@PathVariable("id") long id){
		Gift gift = giftRepository.findOne(id);
		if(gift == null){
			return 0;
		}
		Collection<GiftTouches> giftTouches = giftTouchesRepository.findByGiftId(id);
		if(giftTouches != null){
			return giftTouches.size();
		}
		return 0;
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}/{username}/touches", method = RequestMethod.GET)
	public @ResponseBody GiftTouches getTouchesByUser(
			@PathVariable("id") long id,
			@PathVariable("username") String username){
		
		GiftTouches giftTouches = giftTouchesRepository.findByGiftIdAndUsername(id, username);
		return giftTouches;
	}
	
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}/{username}/untouch", method = RequestMethod.POST)
	public @ResponseBody Gift untouch(
			@PathVariable("id") long id,
			@PathVariable("username") String user,
			Principal p){
		if(p instanceof OAuth2Authentication){
			OAuth2Authentication auth2Authentication = (OAuth2Authentication) p;
			Collection<GrantedAuthority> authorities = auth2Authentication.getAuthorities();
			for(GrantedAuthority authority : authorities){
				if(authority.getAuthority().equals("ADMIN")){
					Gift gift = giftRepository.findOne(id);
					if(gift != null){
						GiftTouches touchesByUser = giftTouchesRepository.findByGiftIdAndUsername(id, user);
						if(touchesByUser != null){
							giftTouchesRepository.delete(touchesByUser);
						}
						Collection<GiftTouches> touches = giftTouchesRepository.findByGiftId(id);
						if(touches != null){
							gift.setTouches(touches.size());
						}else{
							gift.setTouches(0);
						}
					}
					return gift;
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}/obscene", method = RequestMethod.POST)
	public @ResponseBody GiftObsceneStatus obscene(@PathVariable("id") long id){
		Gift gift = giftRepository.findOne(id);
		if(gift == null){
			return null;
		}
		
		if(!gift.isObscene()){
			gift.setObscene(Boolean.TRUE);
			giftRepository.save(gift);
			return new GiftObsceneStatus(ObsceneState.MARKED);
		}
		
		return new GiftObsceneStatus(ObsceneState.UNMARKED);
	}
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/{id}/obscene/status", method = RequestMethod.GET)
	public @ResponseBody GiftObsceneStatus obsceneStatus(@PathVariable("id") long id){
		Gift gift = giftRepository.findOne(id);
		if(gift == null){
			return null;
		}
		return gift.isObscene() ? new GiftObsceneStatus(ObsceneState.MARKED) : new GiftObsceneStatus(ObsceneState.UNMARKED);
	}
	
	
	@RequestMapping(value = PotlachSvcApi.GIFT_PATH + "/top", method = RequestMethod.GET)
	public @ResponseBody Collection<Object[]> getTopGiftGivers(
			@RequestParam("s") int start,
			@RequestParam("e") int end){
		Pageable maxResults = new PageRequest(start, end);
		Collection<Object[]> topGiftGivers = giftTouchesRepository.findTopGiftGivers(maxResults);
		//		Collection<Gift> topGifts = giftRepository.findTopGiftGivers(new PageRequest(0, 10));
		if(topGiftGivers != null){
			return topGiftGivers;
		}
		return Lists.newArrayList();
	}
}
