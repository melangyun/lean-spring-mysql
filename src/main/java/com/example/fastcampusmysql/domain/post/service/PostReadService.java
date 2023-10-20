package com.example.fastcampusmysql.domain.post.service;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.dto.PostDto;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostLikeRepository;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;
import com.example.fastcampusmysql.util.CursorRequest;
import com.example.fastcampusmysql.util.PageCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class PostReadService {
    final private PostRepository postRepository;
    final private PostLikeRepository postLikeRepository;

    public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {
        /*
        반환 값 -> 리스트 (작성일자, 작성회원, 작성 게시물 횟수)
        groupby 이용
        SELECT *
        FROM Post
        WHERE momemberId = :memberId and createdDate between :firstDate and :lastDate
        group by createdDate memberId
        */
        return postRepository.groupByCreatedDate(request);
    }

    public Page<PostDto> getPosts(Long memberId, Pageable pageable) {
        // page Interface에 map이있음
        return postRepository.findAllByMemberId(memberId, pageable).map(this::toDto);
    }

    public PageCursor<PostDto> getPostDtos(List<Long> memberIds, CursorRequest cursorRequest) {
        var posts = findAllBy(memberIds, cursorRequest);
        long nextKey = getNextKey(posts);
        var postDtos = posts.stream().map(this::toDto).toList();
        // 쓰기 성능은 얻었지만.. 읽기 성능은 떨어짐 (트레이드 오프)
        // count query를 매번?
        return new PageCursor<>(cursorRequest.next(nextKey), postDtos);
    }

    public PageCursor<Post> getPosts(Long memberId, CursorRequest cursorRequest) {
        var posts = findAllBy(memberId, cursorRequest);
        long nextKey = getNextKey(posts);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    public PageCursor<Post> getPosts(List<Long> memberIds, CursorRequest cursorRequest) {
        var posts = findAllBy(memberIds, cursorRequest);
        long nextKey = getNextKey(posts);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }

    public List<Post> findAllByInIds(List<Long> postIds) {
        return postRepository.findAllByIdIn(postIds);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId, false).orElseThrow();
    }

    private PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getMemberId(),
                post.getContents(),
                postLikeRepository.count(post.getId()),
                post.getCreatedAt()
        );
    }

    private long getNextKey(List<Post> posts) {
        return posts.stream()
                .mapToLong(Post::getId)
                .min()
                .orElse(CursorRequest.NONE_KEY);
    }


    private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()) {
            return postRepository.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(
                    cursorRequest.key(),
                    memberId,
                    cursorRequest.size()
            );
        }

        return postRepository.findAllByMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
    }

    private List<Post> findAllBy(List<Long> memberIds, CursorRequest cursorRequest) {
        if (cursorRequest.hasKey()) {
            return postRepository.findAllByLessThanIdAndMemberIdInAndOrderByIdDesc(
                    cursorRequest.key(),
                    memberIds,
                    cursorRequest.size()
            );
        }

        return postRepository.findAllByMemberIdInAndOrderByIdDesc(memberIds, cursorRequest.size());
    }


}
