package com.example.fastcampusmysql.domain.follow.service;

import com.example.fastcampusmysql.domain.follow.entity.Follow;
import com.example.fastcampusmysql.domain.follow.repository.FollowRepository;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

// 도메인 서비스는 최대한 결합도를 낮추는것이 좋음(추후에 MSA로 쪼갤 수 있을 정도로)
@RequiredArgsConstructor
@Service
public class FollowWriteService {
    final private FollowRepository followRepository;

    public void create(MemberDto from, MemberDto to) {
        Assert.isTrue(!from.id().equals(to.id()), "자기 자신을 팔로우 할 수 없습니다.");

        Follow follow = Follow.builder()
                .fromMemberId(from.id())
                .toMemberId(to.id())
                .build();

        followRepository.save(follow);
    }
}
