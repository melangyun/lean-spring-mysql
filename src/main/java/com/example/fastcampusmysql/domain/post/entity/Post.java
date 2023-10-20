package com.example.fastcampusmysql.domain.post.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

// 이미지 업로드 게시물 수정 내용이 빠져있음. 추후 고치는 프로젝트를 하는게 좋을듯
@Getter
public class Post {
    final private Long id;

    final private Long memberId;

    final private String contents;

    final private LocalDate createdDate;

    private Long likeCount;

    final private LocalDateTime createdAt;

    private Long version;

    @Builder
    public Post(Long id, Long memberId, String contents, LocalDate createdDate, Long likeCount, LocalDateTime createdAt, Long version) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.contents = Objects.requireNonNull(contents);
        this.createdDate = createdDate == null ? LocalDate.now() : createdDate;
        this.likeCount = likeCount == null ? 0L : likeCount;
        // 별도의 마이그레이션 배치 || null이면 기본값을 채워주는 전략
        this.version = version == null ? 0L : version;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public void incrementLikeCount() {
        likeCount++;
    }
}
