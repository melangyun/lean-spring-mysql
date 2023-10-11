package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.service.PostReadService;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTimelinePostsUsecase {

    final private FollowReadService followReadService;
    final private PostReadService postReadService;

    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
        /*
        1. memberId로 follow 조회한다.
        2. follow한 사람들의 post를 조회한다.
        */
        var followers = followReadService.getFollows(memberId);
        var follosingMemberIds = followers.stream()
                .map(follow -> follow.getToMemberId())
                .toList();
        return postReadService.getPosts(follosingMemberIds, cursorRequest);
    }
}
