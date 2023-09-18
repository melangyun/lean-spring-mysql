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

        // id()와 getId()는 뭐가 다른가?
        // id()는 record의 getter를 의미한다.
        // getId()는 entity의 getter를 의미한다.
        // record는 getter를 자동으로 만들어주지만, entity는 getter를 만들어주지 않는다.
        // record는 getter를 만들어주는 이유는 불변성을 보장하기 위해서이다.
        // entity는 getter를 만들어주지 않는 이유는 불변성을 보장하지 않기 때문이다.

        Follow follow = Follow.builder()
                .fromMemberId(from.id())
                .toMemberId(to.id())
                .build();

        followRepository.save(follow);
    }
}
