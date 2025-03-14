package com.beyond.StomachForce.User.repository;

import com.beyond.StomachForce.User.domain.Enum.Influencer;
import com.beyond.StomachForce.User.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByName(String name);
    Optional<User> findByBirth(String birth);
    Optional<User> findByIdentify(String identify);
    Optional<User> findByNickName(String nickName);
    Optional<User> findByEmail(String email);

    Page<User> findAll(Specification<User> specification, Pageable pageable);


    @Query("SELECT u.id, u.profilePhoto, u.nickName, COUNT(f) " +
            "FROM User u LEFT JOIN u.followers f " +
            "WHERE u.influencer = :influencer " +
            "GROUP BY u.id " +
            "ORDER BY COUNT(f) DESC")
    List<Object[]> findTopInfluencersByInfluencer(
            @Param("influencer") Influencer influencer, Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY SIZE(u.followers) DESC")
    List<User> findTopInfluencers(Pageable pageable);

    @Query(
            value = "SELECT u.* FROM user u " +
                    "LEFT JOIN follower f ON u.id = f.user_id " +
                    "GROUP BY u.id " +
                    "ORDER BY COUNT(f.follower_user_id) DESC " +
                    "LIMIT 10",
            nativeQuery = true)
    List<User> findTop10UsersByFollowerCountNative();


}
