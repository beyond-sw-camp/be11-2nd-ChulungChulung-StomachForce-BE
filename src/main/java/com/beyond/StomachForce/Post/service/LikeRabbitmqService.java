package com.beyond.StomachForce.Post.service;

import com.beyond.StomachForce.Common.userConfig.RabbitmqConfig;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.Post.dtos.LikeRabbitDto;
import com.beyond.StomachForce.Post.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeRabbitmqService {
    private final RabbitTemplate template;
    private final PostRepository postRepository;

    public LikeRabbitmqService(RabbitTemplate template, PostRepository postRepository) {
        this.template = template;
        this.postRepository = postRepository;
    }
    public void publish(LikeRabbitDto dto){
        template.convertAndSend(RabbitmqConfig.LIKE_TOGGLE_QUEUE,dto);
    }

    @RabbitListener(queues = RabbitmqConfig.LIKE_TOGGLE_QUEUE)
    @Transactional
    public void subscribe(Message message) throws JsonProcessingException {
        String messageBody = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        LikeRabbitDto dto= objectMapper.readValue(messageBody,LikeRabbitDto.class);
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(()->new EntityNotFoundException("post is not found"));
        post.updateLike(dto.getLikes());
    }
}
