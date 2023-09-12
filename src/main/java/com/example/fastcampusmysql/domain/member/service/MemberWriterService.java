package com.example.fastcampusmysql.domain.member.service;

import com.example.fastcampusmysql.domain.member.dto.RegisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import com.example.fastcampusmysql.domain.member.repository.MemberNicknameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MemberWriterService {
    private final MemberRepository memberRepository;
    private final MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    public Member create(RegisterMemberCommand command) {
        /*
        목표 - 회원정보(이름, 닉네임, 이메일, 생년월일)를 입력받아서 DB에 저장
         - 닉네임은 10자를 넘을 수 없음

        파라미터 - memberRegisterCommand
        val member = Member.of(memberRegisterCommand)
        memberRepository.save(member);
        */
        Member member = Member.builder()
                .email(command.email())
                .name(command.name())
                .nickname(command.nickname())
                .birthday(command.birthday())
                .build();
        var newMember = memberRepository.save(member);
        this.saveNickNameHistory(newMember);
        return newMember;
    }

    public void changeNickName(Long id, String nickname) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.updateNickname(nickname);

        this.saveNickNameHistory(member);
        memberRepository.save(member);
    }

    private void saveNickNameHistory(Member member) {
        MemberNicknameHistory history = MemberNicknameHistory.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .createdAt(LocalDateTime.now())
                .build();
        memberNicknameHistoryRepository.save(history);
    }
}
