package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.service.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetFollowingMemberUseCase {
    final private MemberReadService memberReadService;
    final private FollowReadService followReadService;

    public List<MemberDto> execute(Long memberId) {
        /*
        1. memberId로 회원 조회
        2. FollowReadService.getFollowingMember(memberId)
        3. MemberReadService.getMember(memberId)
        4. MemberDto로 변환
        5. List<MemberDto> 반환
        */
        var follows = followReadService.getFollows(memberId);
        var memberIds = follows.stream()
                .map(follow -> follow.getToMemberId())
                .toList();
        return memberReadService.getMembers(memberIds);
    }
}
