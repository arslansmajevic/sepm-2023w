package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.BreedMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.BreedDao;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.service.BreedValidator;
import at.ac.tuwien.sepr.assignment.individual.service.service.BreedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BreedServiceImpl implements BreedService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private BreedDao dao;
  private BreedMapper mapper;
  private final BreedValidator breedValidator;

  public BreedServiceImpl(BreedDao dao, BreedMapper mapper, BreedValidator breedValidator) {
    this.dao = dao;
    this.mapper = mapper;
    this.breedValidator = breedValidator;
  }

  @Override
  public Stream<BreedDto> allBreeds() {
    LOG.trace("allBreeds()");
    return dao.allBreeds()
        .stream()
        .map(mapper::entityToDto);
  }

  @Override
  public Stream<BreedDto> findBreedsByIds(Set<Long> breedIds) {
    LOG.trace("findBreedsByIds({})", breedIds);
    return dao.findBreedsById(breedIds)
        .stream()
        .map(mapper::entityToDto);
  }

  @Override
  public Stream<BreedDto> search(BreedSearchDto searchParams) {
    LOG.trace("search({})", searchParams);
    return dao.search(searchParams)
        .stream()
        .map(mapper::entityToDto);
  }

  @Override
  public BreedDto create(BreedDto breed) throws ValidationException {
    LOG.trace("create({})", breed);

    breedValidator.validateForCreate(breed, allBreeds());

    return dao.create(breed);
  }
}
