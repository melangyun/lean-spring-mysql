package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.service.FollowWriterService;
import com.example.fastcampusmysql.domain.member.service.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CrateFollowerUseCase {
    // 회원에 대한 쓰기 권한이 전혀 없다 - 의존성 분리
    final private MemberReadService memberReadService;
    final private FollowWriterService followWriterService;

    public void execute(Long fromMemberId, Long toMemberId){
        /*
        1. 입력받은 memberId로 회원 조회
        2. FollowWriterService.create(from, to)
        */
        var from = memberReadService.getMember(fromMemberId);
        var to = memberReadService.getMember(toMemberId);
        followWriterService.create(from, to);
    }
}
