package at.ac.tuwien.sepr.assignment.individual.service.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;

import java.util.stream.Stream;

public interface TournamentService {

  Stream<TournamentListDto> search(TournamentSearchDto searchDto);
}
