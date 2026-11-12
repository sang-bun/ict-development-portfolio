package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.entity.PostBookmark;
import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.enums.PostSortType;
import com.capstone.fertility.domain.community.enums.PostStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommunityPostQueryRepositoryImpl implements CommunityPostQueryRepository {

    private final EntityManager em;

    @Override
    public Page<Post> searchPosts(
            Long viewerUserId,
            PostCategory category,
            PostSortType sort,
            String query,
            boolean bookmarkedOnly,
            Set<Long> blockedAuthorIds,
            Pageable pageable
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Post> cq = cb.createQuery(Post.class);
        Root<Post> root = cq.from(Post.class);
        root.fetch("author", JoinType.INNER);
        List<Predicate> predicates = buildPredicates(cb, cq, root, viewerUserId, category, query, bookmarkedOnly, blockedAuthorIds);
        cq.select(root).where(predicates.toArray(Predicate[]::new)).distinct(true);
        applyOrder(cb, cq, root, sort);

        TypedQuery<Post> typedQuery = em.createQuery(cq);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Post> content = typedQuery.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Post> countRoot = countCq.from(Post.class);
        List<Predicate> countPredicates = buildPredicates(cb, countCq, countRoot, viewerUserId, category, query, bookmarkedOnly, blockedAuthorIds);
        countCq.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(Predicate[]::new));
        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<Post> root,
            Long viewerUserId,
            PostCategory category,
            String query,
            boolean bookmarkedOnly,
            Set<Long> blockedAuthorIds
    ) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status"), PostStatus.ACTIVE));

        if (category != null) {
            predicates.add(cb.equal(root.get("category"), category));
        }
        if (blockedAuthorIds != null && !blockedAuthorIds.isEmpty()) {
            predicates.add(cb.not(root.get("author").get("id").in(blockedAuthorIds)));
        }
        if (query != null && !query.isBlank()) {
            String pattern = "%" + query.trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("body")), pattern),
                    cb.like(cb.lower(root.get("tags").as(String.class)), pattern)
            ));
        }
        if (bookmarkedOnly && viewerUserId != null) {
            Subquery<Long> bookmarked = cq.subquery(Long.class);
            Root<PostBookmark> bm = bookmarked.from(PostBookmark.class);
            bookmarked.select(bm.get("post").get("id"));
            bookmarked.where(cb.equal(bm.get("user").get("id"), viewerUserId));
            predicates.add(root.get("id").in(bookmarked));
        }
        return predicates;
    }

    private void applyOrder(CriteriaBuilder cb, CriteriaQuery<Post> cq, Root<Post> root, PostSortType sort) {
        List<Order> orders = new ArrayList<>();
        if (sort == PostSortType.COMMENTS) {
            orders.add(cb.desc(root.get("commentCount")));
            orders.add(cb.desc(root.get("createdAt")));
        } else if (sort == PostSortType.POPULAR) {
            Expression<Integer> cappedViews = cb.<Integer>selectCase()
                    .when(cb.gt(root.get("viewCount"), 500), 500)
                    .otherwise(root.get("viewCount"));
            Expression<Double> score = cb.sum(
                    cb.sum(
                            cb.prod(root.get("likeCount").as(Double.class), 3.0),
                            cb.prod(root.get("commentCount").as(Double.class), 2.0)
                    ),
                    cb.prod(cappedViews.as(Double.class), 0.1)
            );
            orders.add(cb.desc(score));
            orders.add(cb.desc(root.get("createdAt")));
        } else {
            orders.add(cb.desc(root.get("createdAt")));
        }
        cq.orderBy(orders);
    }
}
