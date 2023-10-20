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
import java.util.Optional;

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
            .likeCount(rs.getLong("likeCount"))
            .version(rs.getLong("version"))
            .createdDate(rs.getObject("createdDate", LocalDate.class))
            .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
            .build();

    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        }
        return update(post);
    }

    public Optional<Post> findById(Long id, Boolean requiredLock) {
        var sql = "SELECT * FROM %s WHERE id = :id" .formatted(TABLE);
        if(requiredLock) {
            sql += " FOR UPDATE";
        }
        var params = new MapSqlParameterSource()
                .addValue("id", id);
        var nullablePost = jdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
        return Optional.ofNullable(nullablePost);
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

    public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return jdbcTemplate.query(query, params, ROW_MAPPER);
    }

    public List<Post> findAllByMemberIdInAndOrderByIdDesc(List<Long> memberIds, int size) {
        if (memberIds.isEmpty()) {
            return List.of();
        }

        var params = new MapSqlParameterSource()
                .addValue("memberIds", memberIds)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds)
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return jdbcTemplate.query(query, params, ROW_MAPPER);
    }

    public List<Post> findAllByLessThanIdAndMemberIdInAndOrderByIdDesc(Long id, List<Long> memberIds, int size) {
        if (memberIds.isEmpty()) {
            return List.of();
        }

        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberIds", memberIds)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId in (:memberIds) and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return jdbcTemplate.query(query, params, ROW_MAPPER);
    }

    public List<Post> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberId", memberId)
                .addValue("size", size);

        String query = String.format("""
                SELECT *
                FROM %s
                WHERE memberId = :memberId and id < :id
                ORDER BY id DESC
                LIMIT :size
                """, TABLE);

        return jdbcTemplate.query(query, params, ROW_MAPPER);
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

    private Post update(Post post) {
        var sql = String.format("""
                UPDATE %s set 
                    memberId = :memberId, 
                    contents = :contents, 
                    createdDate = :createdDate, 
                    createdAt = :createdAt, 
                    likeCount = :likeCount,
                    version = version + 1
                WHERE id = :id and version = :version
                """, TABLE);

        SqlParameterSource params = new BeanPropertySqlParameterSource(post);
        var updatedCount = jdbcTemplate.update(sql, params);
        if (updatedCount == 0) {
            throw new RuntimeException("not updated");
        }
        return post;
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


    public List<Post> findAllByIdIn(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        var sql = """
                SELECT *
                FROM %s
                WHERE id in (:ids)
                """.formatted(TABLE);
        var params = new MapSqlParameterSource()
                .addValue("ids", ids);
        return jdbcTemplate.query(sql, params, ROW_MAPPER);
    }
}
