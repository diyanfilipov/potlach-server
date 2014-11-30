package com.diyanfilipov.potlach;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftTouchesRepository extends CrudRepository<GiftTouches, Long>{

	Collection<GiftTouches> findByUsername(String username);
	
	Collection<GiftTouches> findByGiftId(long giftId);
	
	GiftTouches findByGiftIdAndUsername(long giftId, String username);
	
	@Query("select count(giftId) as c, username from GiftTouches group by username order by c desc, username asc")
	List<Object[]> findTopGiftGivers(Pageable maxResults);
}
