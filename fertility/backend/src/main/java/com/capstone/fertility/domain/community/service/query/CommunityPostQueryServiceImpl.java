package com.capstone.fertility.domain.community.service.query;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.entity.PostImage;
import com.capstone.fertility.domain.community.entity.PostView;
import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.enums.PostSortType;
import com.capstone.fertility.domain.community.repository.*;
import com.capstone.fertility.domain.community.exception.CommunityException;
import com.capstone.fertility.domain.community.exception.code.CommunityErrorCode;
import com.capstone.fertility.domain.community.support.CommunityAccessSupport;
import com.capstone.fertility.domain.community.support.CommunityMapper;
import com.capstone.fertility.domain.community.support.CommunityValidationSupport;
import com.capstone.fertility.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostQueryServiceImpl implements CommunityPostQueryService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final CommunityPostRepository postRepository;
    private final CommunityPostImageRepository postImageRepository;
    private final CommunityPostLikeRepository postLikeRepository;
    private final CommunityPostBookmarkRepository postBookmarkRepository;
    private final CommunityPostViewRepository postViewRepository;
    private final CommunityAccessSupport accessSupport;
    private final CommunityMapper mapper;

    @Override
    public CommunityResDTO.PostPage listPosts(
            Long viewerUserId,
            String category,
            String sort,
            String query,
            boolean bookmarkedOnly,
            int page,
            int size
    ) {
        int pageSize = normalizeSize(size);
        PostCategory postCategory = parseCategoryOrNull(category);
        PostSortType sortType = PostSortType.fromQuery(sort);
        Set<Long> blocked = accessSupport.blockedAuthorIds(viewerUserId);

        Page<Post> postPage = postRepository.searchPosts(
                viewerUserId,
                postCategory,
                sortType,
                query,
                bookmarkedOnly,
                blocked,
                PageRequest.of(Math.max(page, 0), pageSize)
        );

        List<Long> postIds = postPage.getContent().stream().map(Post::getId).toList();
        Set<Long> likedIds = postIds.isEmpty()
                ? Set.of()
                : postLikeRepository.findLikedPostIds(viewerUserId, postIds);
        Set<Long> bookmarkedIds = postIds.isEmpty()
                ? Set.of()
                : postBookmarkRepository.findBookmarkedPostIds(viewerUserId, postIds);

        Map<Long, List<PostImage>> imagesByPost = loadImages(postIds);

        List<CommunityResDTO.PostSummary> summaries = postPage.getContent().stream()
                .map(post -> mapper.toSummary(
                        post,
                        imagesByPost.getOrDefault(post.getId(), List.of()),
                        mapper.isLiked(likedIds, post.getId()),
                        mapper.isBookmarked(bookmarkedIds, post.getId()),
                        viewerUserId
                ))
                .toList();

        return CommunityResDTO.PostPage.builder()
                .content(summaries)
                .page(postPage.getNumber())
                .size(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .hasNext(postPage.hasNext())
                .build();
    }

    @Override
    @Transactional
    public CommunityResDTO.PostDetail getPost(Long viewerUserId, Long postId) {
        Post post = requireVisiblePost(viewerUserId, postId);
        recordViewIfAbsent(post, viewerUserId);
        return toDetail(post, viewerUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityResDTO.PostDetail getPostWithoutViewCount(Long viewerUserId, Long postId) {
        Post post = requireVisiblePost(viewerUserId, postId);
        return toDetail(post, viewerUserId);
    }

    private Post requireVisiblePost(Long viewerUserId, Long postId) {
        Post post = accessSupport.requireActivePost(postId);
        Set<Long> blocked = accessSupport.blockedAuthorIds(viewerUserId);
        if (blocked.contains(post.getAuthor().getId())) {
            throw new CommunityException(CommunityErrorCode.POST_NOT_FOUND);
        }
        return post;
    }

    private CommunityResDTO.PostDetail toDetail(Post post, Long viewerUserId) {
        Long postId = post.getId();
        List<String> imageUrls = postImageRepository.findByPost_IdOrderBySortOrderAsc(postId).stream()
                .map(PostImage::getImageUrl)
                .toList();
        boolean liked = postLikeRepository.existsByPost_IdAndUser_Id(postId, viewerUserId);
        boolean bookmarked = postBookmarkRepository.existsByPost_IdAndUser_Id(postId, viewerUserId);
        return mapper.toDetail(post, imageUrls, liked, bookmarked, viewerUserId);
    }

    private void recordViewIfAbsent(Post post, Long viewerUserId) {
        if (postViewRepository.findByPost_IdAndUser_Id(post.getId(), viewerUserId).isEmpty()) {
            User viewer = accessSupport.requireUser(viewerUserId);
            postViewRepository.save(PostView.builder().post(post).user(viewer).build());
            post.incrementViewCount();
        }
    }

    private Map<Long, List<PostImage>> loadImages(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<PostImage>> map = new HashMap<>();
        for (Long postId : postIds) {
            map.put(postId, postImageRepository.findByPost_IdOrderBySortOrderAsc(postId));
        }
        return map;
    }

    private PostCategory parseCategoryOrNull(String category) {
        if (category == null || category.isBlank() || "ALL".equalsIgnoreCase(category.trim())) {
            return null;
        }
        return CommunityValidationSupport.parseCategory(category);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
