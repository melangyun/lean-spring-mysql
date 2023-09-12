package com.example.fastcampusmysql.domain.member.repository;

import com.example.fastcampusmysql.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class MemberRepository {

    final private NamedParameterJdbcTemplate jdbcTemplate;

    static final private String TABLE = "Member";


    public Optional<Member> findById(Long id) {
  /*
    id를 통해 member를 찾아서 반환
    SELECT * FROM Member WHERE id = ?
  */
        String sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        // TODO: 추후 JPA를 통하여 반환하도록 수정
        // JPA 설정에서 네이밍 전략 룰을 사용할 수 있다.
        // BeanPropertyRowMapper<Member> rowMapper = new BeanPropertyRowMapper<>(Member.class);

        // 매핑 로직을 없엘수도 있다. 하지만 맴버의 Setter가 모두 public이어야 한다.
        // Setter를 여는것은 굉장히 고민이 필요하다.
        RowMapper<Member> rowMapper = (ResultSet rs, int rowNum) -> Member.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .nickname(rs.getString("nickname"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .createdAt(rs.getDate("createdAt").toLocalDate())
                .build();
        // 0개 이거나 1개가 나올 수 있다.
        var member = jdbcTemplate.query(sql, params, rowMapper).stream().findFirst().orElse(null);
        return Optional.ofNullable(member);
    }

    public Member save(Member member) {
        /*
        member id를 보고 갱신 또는 삽입을 결정
        반환 값은 맴버의 id를 포함한 member
        */
        if (member.getId() == null) {
            return insert(member);
        }
        return update(member);
    }

    public List<Member> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        /*
        ids를 통해 members를 찾아서 반환
        SELECT * FROM Member WHERE id IN (?,?,?)
        */
        // id가 빈 리스트 일 경우 문제 발생
        String sql = String.format("SELECT * FROM %s WHERE id IN (:ids)", TABLE);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ids", ids);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> Member.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .nickname(rs.getString("nickname"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .createdAt(rs.getDate("createdAt").toLocalDate())
                .build());
    }

    private Member insert(Member member) {
        SimpleJdbcInsert simpleJobInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("Member")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource params = new BeanPropertySqlParameterSource(member);
        var id = simpleJobInsert.executeAndReturnKey(params).longValue();
        // 한번 만들어진 객체의 id값은 불변이기 때문에 새로 생성해 주어야함

        // TODO: 추후 JPA를 통하여 반환하도록 수정
        return Member.builder()
                .id(id)
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .birthday(member.getBirthday())
                .createdAt(member.getCreatedAt())
                .build();
    }

    private Member update(Member member) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate.getJdbcTemplate());
        var sql = String.format("UPDATE %s SET email = :email, name = :name, nickname = :nickname, birthday = :birthday, createdAt = :createdAt WHERE id = :id", TABLE);
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(member));
        return member;
    }
}
