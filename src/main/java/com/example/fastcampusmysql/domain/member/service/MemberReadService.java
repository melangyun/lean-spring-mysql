package com.example.fastcampusmysql.domain.member.service;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.dto.MemberNicknameHistoryDto;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import com.example.fastcampusmysql.domain.member.repository.MemberNicknameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberReadService {
    final private MemberRepository memberRepository;
    final private MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    public MemberDto getMember(Long id) {
        var member = memberRepository.findById(id).orElseThrow();
        return toMemberDto(member);
    }

    public MemberDto toMemberDto(Member member) {
        return new MemberDto(member.getId(), member.getEmail(), member.getName(), member.getNickname(), member.getBirthday());
    }

    public List<MemberNicknameHistoryDto> getNickNameHistory(Long memberId) {
        return memberNicknameHistoryRepository.findAllByMemberId(memberId)
                .stream()
                .map(this::toNickNameHistoryDto)
                .toList();
    }

    public List<MemberDto> getMembers(List<Long> ids) {
        return memberRepository.findAllByIds(ids)
                .stream()
                .map(this::toMemberDto)
                .toList();
    }

    private MemberNicknameHistoryDto toNickNameHistoryDto(MemberNicknameHistory history) {
        return new MemberNicknameHistoryDto(history.getId(), history.getMemberId(), history.getNickname(), history.getCreatedAt());
    }
}
