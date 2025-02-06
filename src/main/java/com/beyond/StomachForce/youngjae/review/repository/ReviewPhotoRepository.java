package com.beyond.StomachForce.youngjae.review.repository;

import com.beyond.StomachForce.youngjae.review.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {

}
