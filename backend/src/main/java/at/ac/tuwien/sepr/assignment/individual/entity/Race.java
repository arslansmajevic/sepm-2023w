package at.ac.tuwien.sepr.assignment.individual.entity;

public class Race {
  private Long raceId;
  private Long firstPlace;
  private Long secondPlace;
  private Long winner;
  private Long tournamentId;
  private Long round;

  public Long getRaceId() {
    return raceId;
  }

  public Race setRaceId(Long raceId) {
    this.raceId = raceId;
    return this;
  }

  public Long getFirstPlace() {
    return firstPlace;
  }

  public Race setFirstPlace(Long firstPlace) {
    this.firstPlace = firstPlace;
    return this;
  }

  public Long getSecondPlace() {
    return secondPlace;
  }

  public Race setSecondPlace(Long secondPlace) {
    this.secondPlace = secondPlace;
    return this;
  }

  public Long getWinner() {
    return winner;
  }

  public Race setWinner(Long winner) {
    this.winner = winner;
    return this;
  }

  public Long getTournamentId() {
    return tournamentId;
  }

  public Race setTournamentId(Long tournamentId) {
    this.tournamentId = tournamentId;
    return this;
  }

  public Long getRound() {
    return round;
  }

  @Override
  public String toString() {
    return "Race{"
            + "raceId=" + raceId
            + ", firstPlace=" + firstPlace
            + ", secondPlace=" + secondPlace
            + ", winner=" + winner
            + ", tournamentId=" + tournamentId
            + ", round=" + round
            + '}';
  }

  public Race setRound(Long round) {
    this.round = round;
    return this;
  }
}
