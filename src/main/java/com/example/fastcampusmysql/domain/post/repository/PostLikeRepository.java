package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.domain.post.entity.PostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikeRepository {
    final private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String TABLE = "PostLike";

    private static final RowMapper<PostLike> POST_LIKE_ROW_MAPPER = (rs, rowNum) -> PostLike.builder()
            .id(rs.getLong("id"))
            .memberId(rs.getLong("memberId"))
            .postId(rs.getLong("postId"))
            .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
            .build();

    public PostLike save(PostLike postLike) {
        if (postLike.getId() == null) {
            return insert(postLike);
        }
        return update(postLike);
    }

    public Long count(Long postId){
        var sql = """
                SELECT COUNT(*) FROM %s WHERE postId = :postId
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
                .addValue("postId", postId);
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    private PostLike update(PostLike postLike) {
        var sql = """
                UPDATE %s
                SET
                    memberId = :memberId,
                    postId = :postId,
                    createdAt = :createdAt
                WHERE id = :id
                """.formatted(TABLE);
        var params = new BeanPropertySqlParameterSource(postLike);
        jdbcTemplate.update(sql, params);
        return postLike;
    }

    private PostLike insert(PostLike postLike) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        var params = new BeanPropertySqlParameterSource(postLike);
        var id = jdbcInsert.executeAndReturnKey(params);
        return PostLike.builder()
                .id(id.longValue())
                .memberId(postLike.getMemberId())
                .postId(postLike.getPostId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }
}
