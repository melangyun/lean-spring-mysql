package com.example.fastcampusmysql.domain.post;

import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;
import com.example.fastcampusmysql.util.PostFixtureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.stream.IntStream;

// SpringBootTest 에서 Transactional annotaion 사용시 코드 롤백이 된다.
// 또, 아래 코드는 전체 테스트코드 동작시 함께 테스트 되어선 안된다.
@SpringBootTest
public class PostBulkInsertTest {
    @Autowired
    private PostRepository postRepository;

    // TODO: 부하테스트 리펙토링 가능
    // API 단 부터 부하테스트도 하면 좋음
    @Test
    public void bulkInsert() {
        /*
        spring jdbc template 의 saveAll 호출시 save가 loop을 이루면서 호출된다.
        따라서 pk가 autoIncrement인 경우에는 bulkInsert를 사용하는 것이 좋다.
        */
        var easyRandom = PostFixtureFactory.get(
                1L,
                LocalDate.of(2023, 9, 1),
                LocalDate.of(2023, 9, 30)
        );

        var posts = IntStream.range(0, 10_000 * 100)
                .parallel()
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .toList();

        postRepository.bulkInsert(posts);
    }
}
