package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME_HORSE = "horse";
  private static final String TABLE_NAME_RACE = "race";
  private static final String TABLE_NAME_BREED = "breed";
  private static final String TABLE_NAME_PARTICIPATION = "participation";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME_HORSE + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT  "
          + "    h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\""
          + "    , h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME_HORSE + " h LEFT JOIN " + TABLE_NAME_BREED + " b ON (h.breed_id = b.id)"
          + " WHERE (:name IS NULL OR UPPER(h.name) LIKE UPPER('%'||:name||'%'))"
          + "  AND (:sex IS NULL OR :sex = sex)"
          + "  AND (:bornEarliest IS NULL OR :bornEarliest <= h.date_of_birth)"
          + "  AND (:bornLatest IS NULL OR :bornLatest >= h.date_of_birth)"
          + "  AND (:breed IS NULL OR UPPER(b.name) LIKE UPPER('%'||:breed||'%'))";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME_HORSE
      + " SET name = ?"
      + "  , sex = ?"
      + "  , date_of_birth = ?"
      + "  , height = ?"
      + "  , weight = ?"
      + "  , breed_id = ?"
      + " WHERE id = ?";

  private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME_HORSE
          + " (name, sex, date_of_birth, height, weight, breed_id)"
          + " VALUES (?, ?, ?, ?, ?, ?)";
  private static final String SQL_SEARCH_TOURNAMENT_HORSE = "SELECT  "
          + " h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\","
          + " h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME_HORSE + " h"
          + " LEFT JOIN breed b ON (h.breed_id = b.id)"
          + " LEFT JOIN race r ON r.first_place = h.id OR r.second_place = h.id"
          + " WHERE r.tournament_id = ?";
  private static final String SQL_SEARCH_RACES_ON_TOURNAMENT = "SELECT  "
          + " r.id as \"id\", r.first_place as \"first_place\", r.second_place as \"second_place\","
          + " r.winner as \"winner\", r.tournament_id as \"tournament_id\","
          + " r.round as \"round\""
          + " FROM " + TABLE_NAME_RACE + " r"
          + " WHERE tournament_id = ?";
  private static final String SQL_SEARCH_PARTICIPATIONS_ON_TOURNAMENT = "SELECT  "
          + " p.id as \"id\", p.tournament_id as \"tournament_id\", p.horse_id as \"horse_id\","
          + " p.entry as \"entry\""
          + " FROM " + TABLE_NAME_PARTICIPATION + " p"
          + " WHERE tournament_id = ?";
  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME_HORSE + " WHERE id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;


  public HorseJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }

  @Override
  public Horse create(HorseDetailDto horse) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, horse.name());
      ps.setString(2, horse.sex().toString());
      ps.setDate(3, Date.valueOf(horse.dateOfBirth()));
      ps.setDouble(4, horse.height());
      ps.setDouble(5, horse.weight());

      if (horse.breed() != null) {
        ps.setLong(6, horse.breed().id());
      } else {
        // ps.setLong(6, -1); // not really the best way of doing it
        ps.setNull(6, Types.INTEGER);
      }

      return ps;
    }, keyHolder);

    long newId = keyHolder.getKey().longValue();

    Horse createdHorse = new Horse()
            .setId(newId)
            .setName(horse.name())
            .setDateOfBirth(horse.dateOfBirth())
            .setSex(horse.sex())
            .setHeight(horse.height())
            .setWeight(horse.weight());

    if (horse.breed() != null) {
      createdHorse.setBreedId(horse.breed().id());
    } else {
      createdHorse.setBreedId(null);
    }

    return createdHorse;
  }

  @Override
  public String delete(long id, String name) {
    jdbcTemplate.update(SQL_DELETE, id);

    return "Horse %d %s was deleted successfully".formatted(id, name);
  }

  @Override
  public Collection<Horse> getTournamentHorses(Long tournamentId) {
    LOG.trace("getTournamentHorses({})", tournamentId);

    return jdbcTemplate.query(SQL_SEARCH_TOURNAMENT_HORSE, this::mapRow, tournamentId);
  }

  @Override
  public Collection<Race> getHorseRaces(Long tournamentId) {
    LOG.trace("getHorseRacesOnThisTournamentId({})", tournamentId);

    return jdbcTemplate.query(SQL_SEARCH_RACES_ON_TOURNAMENT, this::mapRaceRow, tournamentId);
  }

  @Override
  public Collection<Participation> getTournamentParticipations(Long tournamentId) {
    LOG.trace("getTournamentParticipations({})", tournamentId);


    return jdbcTemplate.query(SQL_SEARCH_PARTICIPATIONS_ON_TOURNAMENT, this::mapParticipationRow, tournamentId);
  }

  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);
    params.registerSqlType("sex", Types.VARCHAR);

    return jdbcNamed.query(query, params, this::mapRow);
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
        horse.breed() != null ? horse.breed().id() : null,
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        .setBreedId(horse.breed() != null ? horse.breed().id() : -1);
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setSex(Sex.valueOf(result.getString("sex")))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setHeight(result.getFloat("height"))
        .setWeight(result.getFloat("weight"))
        .setBreedId(result.getLong("breed_id"))
        ;
  }

  private Race mapRaceRow(ResultSet resultSet, int rownum) throws SQLException {
    return new Race()
            .setRaceId(resultSet.getLong("id"))
            .setFirstPlace(resultSet.getLong("first_place"))
            .setSecondPlace(resultSet.getLong("second_place"))
            .setWinner(resultSet.getLong("winner"))
            .setRound(resultSet.getLong("round"))
            .setTournamentId(resultSet.getLong("tournament_id"));
  }

  private Participation mapParticipationRow(ResultSet resultSet, int rownum) throws SQLException {
    return new Participation()
            .setId(resultSet.getLong("id"))
            .setHorseId(resultSet.getLong("horse_id"))
            .setTournamentId(resultSet.getLong("tournament_id"))
            .setEntry((int) resultSet.getLong("entry"));
  }

}
