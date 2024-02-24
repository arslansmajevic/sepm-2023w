package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;

@Repository
public class TournamentJdbcDao implements TournamentDao {

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME_TOURNAMENT = "tournament";
  private static final String TABLE_NAME_PARTICIPATION = "participation";
  private static final String TABLE_NAME_RACE = "race";

  private static final String SQL_SELECT_SEARCH = "SELECT "
        + "t.id as \"id\", t.name as \"name\", t.date_of_start as \"date_of_start\", "
        + "t.date_of_end as \"date_of_end\" "
        + "FROM " + TABLE_NAME_TOURNAMENT + " t "
        + "WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%')) "
        + "AND (:startDate IS NULL OR t.date_of_end >= :startDate) "
        + "AND (:endDate IS NULL OR t.date_of_start <= :endDate)";
  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private static final String SQL_INSERT_TOURNAMENT = "INSERT INTO " + TABLE_NAME_TOURNAMENT
          + " (name, date_of_start, date_of_end)"
          + " VALUES (?, ?, ?)";

  private static final String SQL_INSERT_PARTICIPANT = "INSERT INTO " + TABLE_NAME_PARTICIPATION
          + " (tournament_id, horse_id, entry)"
          + " VALUES (?, ?, ?)";
  private static final String SQL_INSERT_RACE = "INSERT INTO " + TABLE_NAME_RACE
          + " (first_place, second_place, winner, tournament_id, round)"
          + " VALUES (?, ?, ?, ?, ?)";
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

  @Override
  public Tournament create(TournamentCreateDto tournament) {
    KeyHolder keyHolderTournament = new GeneratedKeyHolder();
    KeyHolder keyHolderParticipant = new GeneratedKeyHolder();
    KeyHolder keyHolderRace = new GeneratedKeyHolder();


    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT_TOURNAMENT, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, tournament.name());
      ps.setDate(2, Date.valueOf(tournament.startDate()));
      ps.setDate(3, Date.valueOf(tournament.endDate()));
      return ps;
    }, keyHolderTournament);

    long newTournamentId = keyHolderTournament.getKey().longValue();
    int entryCount = 1;

    for (HorseSelectionDto participant : tournament.participants()) {

      int finalEntryCount = entryCount;
      jdbcTemplate.update(connection -> {

        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_PARTICIPANT, Statement.RETURN_GENERATED_KEYS);

        ps.setLong(1, newTournamentId);
        ps.setLong(2, participant.id());
        ps.setLong(3, finalEntryCount);
        return ps;
      }, keyHolderParticipant);
      entryCount++;
    }

    for (int i = 0; i < tournament.participants().length; i = i + 2) {
      int place = i;
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_RACE, Statement.RETURN_GENERATED_KEYS);

        ps.setLong(1, tournament.participants()[place].id());
        ps.setLong(2, tournament.participants()[place + 1].id());
        ps.setNull(3, Types.INTEGER);
        ps.setLong(4, newTournamentId);
        ps.setLong(5, 1);

        return ps;
      }, keyHolderRace);
    }

    return new Tournament()
            .setId(newTournamentId)
            .setDateOfStart(tournament.startDate())
            .setName(tournament.name())
            .setDateOfEnd(tournament.endDate());
  }

  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {

    return new Tournament()
            .setId(result.getLong("id"))
            .setName(result.getString("name"))
            .setDateOfStart(result.getDate("date_of_start").toLocalDate())
            .setDateOfEnd(result.getDate("date_of_end").toLocalDate());
  }
}
