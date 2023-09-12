package com.example.fastcampusmysql.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class Member {
    final private Long id;
    final private String email;
    private String name;
    private String nickname;
    private LocalDate birthday;

    final private LocalDate createdAt; // 디버깅에 도움

    final private static int NICKNAME_MAX_LENGTH = 10;

    @Builder
    public Member(Long id, String email, String name, String nickname, LocalDate birthday, LocalDate createdAt) {
        this.id = id; // JPA 에서는 id가 null이면 insert, null이 아니면 update 하기때문에 nullable 하게 만들어야함
        this.email = Objects.requireNonNull(email);
        this.name = Objects.requireNonNull(name);
        this.birthday = Objects.requireNonNull(birthday);

        validateNickName(nickname);
        this.nickname = Objects.requireNonNull(nickname);

        this.createdAt = createdAt == null ? LocalDate.now() : createdAt;
    }

    public void updateNickname(String to) {
        Objects.requireNonNull(to);
        validateNickName(to);
        this.nickname = to;
    }

    private void validateNickName(String nickname) {
        Assert.isTrue(
                nickname.length() <= NICKNAME_MAX_LENGTH,
                "닉네임은 " + NICKNAME_MAX_LENGTH + "자를 넘을 수 없습니다."
        );
    }
}
