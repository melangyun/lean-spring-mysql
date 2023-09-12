package com.example.fastcampusmysql.controller;

import com.example.fastcampusmysql.application.usecase.CrateFollowerUseCase;
import com.example.fastcampusmysql.application.usecase.GetFollowingMemberUseCase;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/followers")
public class FollowerController {
    private final CrateFollowerUseCase createFollowerUseCase;
    private final GetFollowingMemberUseCase getFollowerUseCase;

    @PostMapping("/{fromId}/{toId}")
    public void register(@PathVariable Long fromId, @PathVariable Long toId) {
        createFollowerUseCase.execute(fromId, toId);
    }

    @GetMapping("/members/{id}")
    public List<MemberDto> getFollowers(@PathVariable Long id) {
        return getFollowerUseCase.execute(id);
    }
}
