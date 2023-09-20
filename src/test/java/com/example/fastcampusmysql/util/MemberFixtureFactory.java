package com.example.fastcampusmysql.util;

import com.example.fastcampusmysql.domain.member.entity.Member;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.number.LongRandomizer;

public class MemberFixtureFactory {
    // object mother 패턴
    // https://velog.io/@gwichanlee/Test-Data-%EB%A7%8C%EB%93%A4%EA%B8%B0-Builder-vs-Object-Mother
    static public Member create() {
        // https://github.com/j-easy/easy-random
        // 유명 기업에서 easy-random을 이용하여 어떻게 짜는지 코드도 볼 수 있다.
        var seed = new LongRandomizer().getRandomValue();
        var param = new EasyRandomParameters()
                .seed(seed);
        return new EasyRandom(param).nextObject(Member.class);
    }
}
