package com.example.fastcampusmysql.domain.follow.repository;

import com.example.fastcampusmysql.domain.follow.entity.Follow;
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
public class FollowRepository {
    final private NamedParameterJdbcTemplate jdbcTemplate;

    static final private String TABLE = "Follow";

    public Follow save(Follow follow) {
        if (follow.getId() == null) {
            return insert(follow);
        }
        throw new UnsupportedOperationException();
    }

    private Follow insert(Follow follow) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(follow);
        var id = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        return Follow.builder()
                .id(id)
                .fromMemberId(follow.getFromMemberId())
                .toMemberId(follow.getToMemberId())
                .createdAt(follow.getCreatedAt())
                .build();
    }

    public List<Follow> findAllByMemberId(Long id) {
        String sql = String.format("SELECT * FROM %s WHERE fromMemberId = :id", TABLE);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> Follow.builder()
                .id(rs.getLong("id"))
                .fromMemberId(rs.getLong("fromMemberId"))
                .toMemberId(rs.getLong("toMemberId"))
                .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .build());
    }
}
