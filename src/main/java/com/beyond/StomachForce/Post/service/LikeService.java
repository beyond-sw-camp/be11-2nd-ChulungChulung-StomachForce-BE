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
        String key = String.valueOf(postId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId); // 유저가 이미 좋아요 했는지 확인

        if (Boolean.TRUE.equals(isMember)) {
            // 이미 좋아요를 눌렀다면 → 좋아요 취소 (Set에서 삭제)
            redisTemplate.opsForSet().remove(key, userId);
        } else {
            // 좋아요 추가 (Set에 userId 추가)
            redisTemplate.opsForSet().add(key, userId);
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
}
