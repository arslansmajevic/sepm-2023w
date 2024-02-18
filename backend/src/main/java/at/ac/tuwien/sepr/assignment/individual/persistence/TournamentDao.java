package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;

import java.util.Collection;

public interface TournamentDao {

  Collection<Tournament> search(TournamentSearchDto searchDto);
}
