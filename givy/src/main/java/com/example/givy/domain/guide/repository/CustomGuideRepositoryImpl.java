package com.example.givy.domain.guide.repository;

import com.example.givy.domain.guide.dto.res.GuideResDTO;
import com.example.givy.domain.guide.entity.QGuide;
import com.example.givy.domain.guide.enums.GuideCategory;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomGuideRepositoryImpl implements CustomGuideRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GuideResDTO.GuideSummaryDTO> findGuides(
            GuideCategory category,
            Pageable pageable
    ) {

        QGuide g = QGuide.guide;

        List<GuideResDTO.GuideSummaryDTO> content = queryFactory
                .select(Projections.constructor(
                        GuideResDTO.GuideSummaryDTO.class,
                        g.guideId,
                        g.title,
                        g.imageUrl,
                        g.likeCount,
                        g.storeCount,
                        g.category
                ))
                .from(g)
                .where(categoryEq(category))
                .orderBy(g.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression categoryEq(GuideCategory category) {
        return category != null
                ? QGuide.guide.category.eq(category)
                : null;
    }
}
