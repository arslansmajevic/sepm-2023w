package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.impl.TournamentJdbcDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final TournamentDao dao;
  private final TournamentMapper mapper;

  public TournamentServiceImpl(TournamentDao dao, TournamentMapper mapper) {
    this.dao = dao;
    this.mapper = mapper;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchDto) {
    var tournaments = dao.search(searchDto);
    return tournaments.stream()
            .map(mapper::entityToListDto)
            .sorted((t1, t2) -> t2.dateOfStart().compareTo(t1.dateOfStart()));
  }
}
