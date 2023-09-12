package com.example.fastcampusmysql.domain.follow.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Follow {
    // 항상 name을 가져올텐데, Follow에 name이 있어야 할까? 라는 고민
    // 하지만 follow list에 name이 최신성 반영이 늦으면 이상하다.
    // 비정규화를 했을때 닉네임 업데이트시 follow에 Name을 모두 업데이트 해야한다. > 쉽지 않음

    // 정규화를 할때 어떻게 Name을 가져올 것인가?
    // join을 가장 쉽게 생각하는 경향이 있음
    // 그러나 조인을 할때에는 강결합이 이루어지고, 유연성 있는 설계가 어려워짐 => 이후 아키텍쳐 변경이 어려워짐

    // 쿼리를 한번 더 하는것이 더 바람직할것

    final private Long id;
    final private Long fromMemberId;
    final private Long toMemberId;
    final private LocalDateTime createdAt;

    @Builder // lombok의 빌더패턴은 어떻게 동작하는가?
    public Follow(Long id, Long fromMemberId, Long toMemberId, LocalDateTime createdAt) {
        this.id = id;
        this.fromMemberId = Objects.requireNonNull(fromMemberId);
        this.toMemberId = Objects.requireNonNull(toMemberId);

        // createdAt중복코드가 계속 반복되고 있다.
        // 이는 추상화를 통해 중복을 줄일 수 있다.

        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}
