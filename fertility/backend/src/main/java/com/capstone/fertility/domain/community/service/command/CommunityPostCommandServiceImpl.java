package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.entity.PostImage;
import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.repository.CommunityPostImageRepository;
import com.capstone.fertility.domain.community.repository.CommunityPostRepository;
import com.capstone.fertility.domain.community.service.query.CommunityPostQueryService;
import com.capstone.fertility.domain.community.support.CommunityAccessSupport;
import com.capstone.fertility.domain.community.support.CommunityValidationSupport;
import com.capstone.fertility.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPostCommandServiceImpl implements CommunityPostCommandService {

    private final CommunityPostRepository postRepository;
    private final CommunityPostImageRepository postImageRepository;
    private final CommunityAccessSupport accessSupport;
    private final CommunityPostQueryService postQueryService;

    @Override
    public CommunityResDTO.PostDetail createPost(Long userId, CommunityReqDTO.PostWrite request) {
        User author = accessSupport.requireUser(userId);
        PostCategory category = CommunityValidationSupport.parseCategory(request.getCategory());
        String title = CommunityValidationSupport.requireTitle(request.getTitle());
        String body = CommunityValidationSupport.requireBody(request.getBody());
        List<String> tags = CommunityValidationSupport.normalizeTags(request.getTags());
        List<String> imageUrls = CommunityValidationSupport.normalizeImageUrls(request.getImageUrls());

        Post post = Post.builder()
                .author(author)
                .category(category)
                .title(title)
                .body(body)
                .tags(tags)
                .build();
        postRepository.save(post);
        saveImages(post, imageUrls);
        return postQueryService.getPostWithoutViewCount(userId, post.getId());
    }

    @Override
    public CommunityResDTO.PostDetail updatePost(Long userId, Long postId, CommunityReqDTO.PostWrite request) {
        Post post = accessSupport.requireOwnedActivePost(postId, userId);
        PostCategory category = request.getCategory() != null
                ? CommunityValidationSupport.parseCategory(request.getCategory())
                : null;
        String title = request.getTitle() != null ? CommunityValidationSupport.requireTitle(request.getTitle()) : null;
        String body = request.getBody() != null ? CommunityValidationSupport.requireBody(request.getBody()) : null;
        List<String> tags = request.getTags() != null ? CommunityValidationSupport.normalizeTags(request.getTags()) : null;
        post.updateContent(category, title, body, tags);

        if (request.getImageUrls() != null) {
            postImageRepository.deleteByPost_Id(postId);
            saveImages(post, CommunityValidationSupport.normalizeImageUrls(request.getImageUrls()));
        }
        return postQueryService.getPostWithoutViewCount(userId, postId);
    }

    @Override
    public void deletePost(Long userId, Long postId) {
        Post post = accessSupport.requireOwnedActivePost(postId, userId);
        post.softDelete();
    }

    private void saveImages(Post post, List<String> imageUrls) {
        int order = 0;
        for (String url : imageUrls) {
            postImageRepository.save(PostImage.builder()
                    .post(post)
                    .imageUrl(url)
                    .sortOrder(order++)
                    .build());
        }
    }
}
