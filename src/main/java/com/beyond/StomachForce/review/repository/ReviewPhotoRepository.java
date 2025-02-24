package com.beyond.StomachForce.review.repository;

import com.beyond.StomachForce.review.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {

    List<ReviewPhoto> findByReviewImagePathIn(List<String> photoUrlsToRemove);

}
