package com.example.fastcampusmysql.domain.follow.service;

import com.example.fastcampusmysql.domain.follow.entity.Follow;
import com.example.fastcampusmysql.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowReadService {
    final private FollowRepository followRepository;

    public List<Follow> getFollows(Long fromMemberId) {
        return followRepository.findAllByMemberId(fromMemberId);
    }

    public List<Follow> getFollowers(Long toMemberId) {
        return followRepository.findAllFollowersByMemberId(toMemberId);
    }

}
