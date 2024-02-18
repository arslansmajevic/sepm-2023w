package at.ac.tuwien.sepr.assignment.individual.entity;

import java.time.LocalDate;

public class Tournament {
  private Long id;
  private String name;
  private LocalDate dateOfStart;
  private LocalDate dateOfEnd;

  public Long getId() {
    return id;
  }

  public Tournament setId(Long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Tournament setName(String name) {
    this.name = name;
    return this;
  }

  public LocalDate getDateOfStart() {
    return dateOfStart;
  }

  public Tournament setDateOfStart(LocalDate dateOfStart) {
    this.dateOfStart = dateOfStart;
    return this;
  }

  public LocalDate getDateOfEnd() {
    return dateOfEnd;
  }

  public Tournament setDateOfEnd(LocalDate dateOfEnd) {
    this.dateOfEnd = dateOfEnd;
    return this;
  }

  @Override
  public String toString() {
    return "Tournament{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", startDate=" + dateOfStart
            + ", endDate=" + dateOfEnd
            + '}';
  }
}
