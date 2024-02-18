package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class TournamentJdbcDao implements TournamentDao {

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament";

  private static final String SQL_SELECT_SEARCH = "SELECT "
        + "t.id as \"id\", t.name as \"name\", t.date_of_start as \"date_of_start\", "
        + "t.date_of_end as \"date_of_end\" "
        + "FROM " + TABLE_NAME + " t "
        + "WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%')) "
        + "AND (:startDate IS NULL OR t.date_of_end >= :startDate) "
        + "AND (:endDate IS NULL OR t.date_of_start <= :endDate)";
  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";

  public TournamentJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchDto) {
    LOG.trace("search({})", searchDto);

    var query = SQL_SELECT_SEARCH;
    if (searchDto.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }

    var params = new BeanPropertySqlParameterSource(searchDto);
    return jdbcNamed.query(query, params, this::mapRow);
  }

  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {

    return new Tournament()
            .setId(result.getLong("id"))
            .setName(result.getString("name"))
            .setDateOfStart(result.getDate("date_of_start").toLocalDate())
            .setDateOfEnd(result.getDate("date_of_end").toLocalDate());
  }
}
