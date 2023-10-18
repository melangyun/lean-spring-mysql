package com.example.fastcampusmysql.application.usecase;

import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.entity.Timeline;
import com.example.fastcampusmysql.domain.post.service.PostReadService;
import com.example.fastcampusmysql.domain.post.service.TimelineReadService;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTimelinePostsUseCase {

    final private FollowReadService followReadService;
    final private PostReadService postReadService;
    final private TimelineReadService timelineReadService;

    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
        /*
        1. memberId로 follow 조회한다.
        2. follow한 사람들의 post를 조회한다.
        */
        var followers = followReadService.getFollows(memberId);
        var followingMemberIds = followers.stream()
                .map(follow -> follow.getToMemberId())
                .toList();
        return postReadService.getPosts(followingMemberIds, cursorRequest);
    }

    public PageCursor<Post> executeByTimeline(Long memberId, CursorRequest cursorRequest) {
        /*
        1. timeline 조회한다.
        2. 1번에 해당하는 게시물을 조회
        (조인으로 진행하는것도 물론 가능)
        */
        var pageTimelines = timelineReadService.getTimelines(memberId, cursorRequest);
        var postIds = pageTimelines.contents().stream()
                .map(Timeline::getPostId)
                .toList();
        var posts = postReadService.findAllByInIds(postIds);

        return new PageCursor<>(pageTimelines.nextCursorRequest(), posts);
    }
}
