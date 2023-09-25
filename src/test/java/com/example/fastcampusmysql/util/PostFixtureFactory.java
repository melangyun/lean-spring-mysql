package com.example.fastcampusmysql.util;

import com.example.fastcampusmysql.domain.post.entity.Post;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.time.LocalDate;

import static org.jeasy.random.FieldPredicates.*;

/* HL: 참조
https://github.com/j-easy/easy-random/wiki/excluding-fields
https://en.wikipedia.org/wiki/Specification_pattern
specification pattern
 */

public class PostFixtureFactory {
    static public EasyRandom get(Long memberId, LocalDate firstDate, LocalDate lastDate) {
        var idPredicate = named("id")
                .and(ofType(Long.class))
                .and(inClass(Post.class));

        var memberIdPredocate = named("memberId")
                .and(ofType(Long.class))
                .and(inClass(Post.class));

        var param = new EasyRandomParameters()
                .excludeField(idPredicate)
                .dateRange(firstDate, lastDate)
                .randomize(memberIdPredocate, () -> memberId);
        return new EasyRandom(param);
    }
}
