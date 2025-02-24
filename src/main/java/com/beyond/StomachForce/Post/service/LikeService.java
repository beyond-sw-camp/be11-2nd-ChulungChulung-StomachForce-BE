package com.beyond.StomachForce.Post.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LikeService {
    @Qualifier("likeDB")
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeService(@Qualifier("likeDB") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void toggleLike(Long postId, String userId) {
        String postKey = String.valueOf(postId);
        Boolean isMember = redisTemplate.opsForSet().isMember(postKey, userId);

        if (Boolean.TRUE.equals(isMember)) {
            // 좋아요 취소: 포스트 set에서 userId 삭제
            redisTemplate.opsForSet().remove(postKey, userId);
            // 역인덱스에서도 해당 postId 삭제 (문자열로 저장)
            redisTemplate.opsForSet().remove("user:likedPosts:" + userId, String.valueOf(postId));
        } else {
            // 좋아요 추가: 포스트 set에 userId 추가
            redisTemplate.opsForSet().add(postKey, userId);
            // 역인덱스에 해당 postId 추가 (문자열로 저장)
            redisTemplate.opsForSet().add("user:likedPosts:" + userId, String.valueOf(postId));
        }
    }
    public Long getLikeCount(Long postId) {
        String key = String.valueOf(postId);
        return redisTemplate.opsForSet().size(key); // 좋아요 수 반환
    }

    public boolean isUserLikedPost(Long postId, Long userId) {
        String key = String.valueOf(postId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, String.valueOf(userId));
        return Boolean.TRUE.equals(isMember); // 좋아요 했으면 true, 안 했으면 false 반환
    }

    // 회원 탈퇴 시 해당 사용자의 좋아요 정보를 모두 제거하는 메서드
    public void removeUserLikes(String userId) {
        String userLikedKey = "user:likedPosts:" + userId;
        Set<Object> likedPosts = redisTemplate.opsForSet().members(userLikedKey);
        if (likedPosts != null) {
            for (Object postIdObj : likedPosts) {
                String postKey = String.valueOf(postIdObj);
                // 각 포스트의 좋아요 set에서 해당 userId를 삭제
                redisTemplate.opsForSet().remove(postKey, userId);
            }
            // 역인덱스 키 삭제
            redisTemplate.delete(userLikedKey);
        }
    }
}
