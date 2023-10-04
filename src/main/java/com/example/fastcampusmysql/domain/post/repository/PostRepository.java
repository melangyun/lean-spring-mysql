package com.example.fastcampusmysql.domain.post.repository;

import com.example.fastcampusmysql.util.PageHelper;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    final private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String TABLE = "Post";

    private static final RowMapper<DailyPostCount> DAILY_POST_COUNT_MAPPER = (ResultSet rs, int rowNum) -> new DailyPostCount(
            rs.getLong("memberId"),
            rs.getObject("createdDate", LocalDate.class),
            rs.getLong("count")
    );

    private static final RowMapper<Post> ROW_MAPPER = (ResultSet rs, int rowNum) -> Post.builder()
            .id(rs.getLong("id"))
            .memberId(rs.getLong("memberId"))
            .contents(rs.getString("contents"))
            .createdDate(rs.getObject("createdDate", LocalDate.class))
            .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
            .build();

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }
        throw new UnsupportedOperationException("POST UPDATE IS NOT SUPPORTED");
    }

    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        // 데이터가 많아지면 느려지는 코드
        var sql = """
                SELECT createdDate, memberId, count(*) as count
                FROM %s
                WHERE memberId = :memberId and createdDate between :firstDate and :lastDate
                GROUP BY createdDate, memberId
                """.formatted(TABLE);
        var params = new BeanPropertySqlParameterSource(request);
        return jdbcTemplate.query(sql, params, DAILY_POST_COUNT_MAPPER);
    }

    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {
        // 보통은 파라미터로 sort를 받지 않음 => sort에 따라서 인덱스가 결정될 수 있기 때문에.
        // 쇼핑몰 같은 경우에는 정해진 몇개의 기준을 정해두고 선택 할 수 있음
        System.out.println("pageable.getSort() = " + PageHelper.orderBy(pageable.getSort()));

        var sql = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY %s
                LIMIT :size
                OFFSET :offset
                """, TABLE, PageHelper.orderBy(pageable.getSort()));

        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        var posts = jdbcTemplate.query(sql, params, ROW_MAPPER);
        return new PageImpl<>(posts, pageable, getCount(memberId));
    }

    public int[] bulkInsert(List<Post> posts) {
        var sql = """
                INSERT INTO %s (memberId, contents, createdDate, createdAt)
                VALUES (:memberId, :contents, :createdDate, :createdAt)
                """.formatted(TABLE);

        SqlParameterSource[] params = posts.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        return jdbcTemplate.batchUpdate(sql, params);
    }


    private Post insert(Post post) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName(TABLE)
                .usingGeneratedKeyColumns("id");

        var params = new BeanPropertySqlParameterSource(post);
        var id = jdbcInsert.executeAndReturnKey(params).longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private Long getCount(Long memberId) {
        var sql = """
                SELECT count(*)
                FROM %s
                WHERE memberId = :memberId
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId);
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }


}
