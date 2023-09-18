package com.example.fastcampusmysql.controller;

import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.post.service.PostReadService;
import com.example.fastcampusmysql.domain.post.service.PostWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 Controller 어노테이션과 RestController 어노테이션의 차이점은?
 Controller 어노테이션은 View를 반환하는 경우에 사용한다.
 RestController 어노테이션은 Json을 반환하는 경우에 사용한다.
 RestController 어노테이션은 Controller 어노테이션과 ResponseBody 어노테이션을 합친 것과 같다.
 @RequestBody 어노테이션은 요청의 body를 자바 객체로 변환해주는 역할을 한다.
*/

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    final private PostWriteService postWriteService;
    final private PostReadService postReadService;

    @PostMapping()
    public Long create(PostCommand command) {
        return postWriteService.create(command);
    }

    @GetMapping("/daily-post-counts")
    public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request) {
        System.out.println("request = " + request);
        return postReadService.getDailyPostCount(request);
    }
}
