package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.posts.dto.GPTRequest;
import com.credential.cubrism.server.posts.dto.GPTResponse;
import com.credential.cubrism.server.posts.entity.PostAiComments;
import com.credential.cubrism.server.posts.entity.Posts;
import com.credential.cubrism.server.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AiResponseService {

    private final PostRepository postRepository;
    private final RestTemplate restTemplate;

    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Async
    public void updatePostWithAiResponse(Posts post) {
        GPTRequest request = new GPTRequest(model, post.getContent(), 0.7, 1024, 1, 2, 2);

        GPTResponse gptResponse = restTemplate.postForObject(apiUrl, request, GPTResponse.class);

        String aiResponse = Objects.requireNonNull(gptResponse).getChoices().get(0).getMessage().getContent();
        PostAiComments postAiComments = new PostAiComments();
        postAiComments.setContent(aiResponse);
        postAiComments.setPost(post);
        post.setPostAiComments(postAiComments);
        postRepository.save(post);
    }
}
