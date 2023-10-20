package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.post.service.PostWriteService;
import com.example.fastcampusmysql.domain.post.service.TimelineWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreatePostUseCase {
    final private PostWriteService postWriteService;

    final private FollowReadService followReadService;

    final private TimelineWriteService timelineWriteService;

    public Long execute(PostCommand postCommand) {
        var postId = postWriteService.create(postCommand);
        var followerIds = followReadService.getFollowers(postCommand.memberId()).stream()
                .map(follow -> follow.getToMemberId())
                .toList();
        // 이부분은 비동기로 처리해도 된다. (반드시 처리되야 할 부분이 아님) -> 같은 트렌젝션이 아니여도 무관
        timelineWriteService.deliveryToTimeline(postId, followerIds);
        return postId;
    }
}
