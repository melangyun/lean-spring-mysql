package com.example.fastcampusmysql.domain.post.service;

import com.example.fastcampusmysql.domain.post.entity.Timeline;
import com.example.fastcampusmysql.domain.post.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TimelineWriteService {
    private final TimelineRepository timelineRepository;

    public void deliveryToTimeline(Long postId, List<Long> memberIds) {
        var timelines = memberIds.stream()
                .map(memberId -> Timeline.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .build())
                .toList();
        timelineRepository.saveAll(timelines);
    }
}
