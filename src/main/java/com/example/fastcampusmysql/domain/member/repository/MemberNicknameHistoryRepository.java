package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;


@RequiredArgsConstructor
@Repository
public class MemberNicknameHistoryRepository {

    final private NamedParameterJdbcTemplate jdbcTemplate;

    static final private String TABLE = "MemberNickNameHistory";


    public MemberNicknameHistory save(MemberNicknameHistory history) {
        if (history.getId() == null) {
            return insert(history);
        }
        throw new UnsupportedOperationException();
    }

    private MemberNicknameHistory insert(MemberNicknameHistory history) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(history);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        // 한번 만들어진 객체의 id값은 불변이기 때문에 새로 생성해 주어야함

        // TODO: 추후 JPA를 통하여 반환하도록 수정
        return MemberNicknameHistory.builder()
                .id(id)
                .memberId(history.getMemberId())
                .nickname(history.getNickname())
                .createdAt(history.getCreatedAt())
                .build();
    }

    public List<MemberNicknameHistory> findAllByMemberId(Long memberId) {
        String sql = String.format("SELECT * FROM %s WHERE memberId = :memberId", TABLE);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> MemberNicknameHistory.builder()
                .id(rs.getLong("id"))
                .memberId(rs.getLong("memberId"))
                .nickname(rs.getString("nickname"))
                .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .build());
    }
}
