package com.diyanfilipov.potlach;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository extends CrudRepository<Gift, Long>{

	List<Gift> findByTitleContainingAndObsceneFalseOrderByDateAddedDesc(String title, Pageable pageable);

	List<Gift> findByTitleContainingOrderByDateAddedDesc(String title, Pageable pageable);
	
	Gift findByTitleAndParent(String title, long parent);
	
	List<Gift> findByUploaderAndObsceneFalseOrderByDateAddedDesc(String uploader, Pageable pageable);
	
	List<Gift> findByUploaderOrderByDateAddedDesc(String uploader, Pageable pageable);
	
	List<Gift> findByParentOrderByDateAddedDesc(long parent, Pageable pageable);

	List<Gift> findByParentAndObsceneFalseOrderByDateAddedDesc(long parent, Pageable pageable);
	
	Collection<Gift> findByParentOrderByTitleAsc(long parent);
	
	List<Gift> findByDateAddedGreaterThanEqualAndParentOrderByDateAddedDesc(long dateAdded, long parent, Pageable pageable);
}
