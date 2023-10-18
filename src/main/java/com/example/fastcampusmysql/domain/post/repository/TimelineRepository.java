package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.post.entity.Timeline;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TimelineRepository {

    final private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String TABLE = "timeline";

    private static final RowMapper<Timeline> ROW_MAPPER = (rs, rowNum) -> new Timeline(
            rs.getLong("id"),
            rs.getLong("memberId"),
            rs.getLong("postId"),
            rs.getTimestamp("createdAt").toLocalDateTime()
    );

    public Timeline save(Timeline timeline) {
        if (timeline.getId() == null) {
            return insert(timeline);
        }
        throw new UnsupportedOperationException("Timeline UPDATE IS NOT SUPPORTED");
    }

    public List<Timeline> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var sql = """
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """.formatted(TABLE);
        var params = new BeanPropertySqlParameterSource(memberId);
        return jdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    /*정말 중복인가? 아니면 우연하게 발생한 중복인가? 고민해볼 필요가 있음
    POST와 코드가 같지만, generic으로 뽑아낼 수 있지만 이제 비용이 높아짐
    이해하기 어려워지고, 정렬 순서를 바꿔야 하거나할때 더 복잡해짐*/
    public List<Timeline> findAllByLessThanIdAndMemberIdOrderByIdDesc(Long id, Long memberId, int size) {
        var sql = """
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id DESC
                LIMIT :size
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("id", id)
                .addValue("size", size);

        return jdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public void saveAll(List<Timeline> timelines) {
        var sql = """
                INSERT INTO %s (memberId, postId, createdAt)
                VALUES (:memberId, :postId, :createdAt)
                """.formatted(TABLE);
        var params = timelines.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, params);
    }

    private Timeline insert(Timeline timeline) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        var params = new BeanPropertySqlParameterSource(timeline);
        var id = jdbcInsert.executeAndReturnKey(params);

        return Timeline.builder()
                .id(id.longValue())
                .memberId(timeline.getMemberId())
                .postId(timeline.getPostId())
                .createdAt(timeline.getCreatedAt())
                .build();
    }

}
