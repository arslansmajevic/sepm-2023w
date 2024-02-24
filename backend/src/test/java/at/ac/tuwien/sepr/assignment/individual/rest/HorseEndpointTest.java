package at.ac.tuwien.sepr.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest extends TestBase {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class)
        .<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult)
        .hasSize(32)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth)
        .contains(
            tuple(-1L, "Wendy", Sex.FEMALE, LocalDate.of(2019, 8, 5)),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10)),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6)),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20)));
  }

  @Test
  public void searchByBreedWelFindsThreeHorses() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .queryParam("breed", "Wel")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var horsesIterator = objectMapper.readerFor(HorseListDto.class)
        .<HorseListDto>readValues(body);
    assertNotNull(horsesIterator);
    var horses = new ArrayList<HorseListDto>();
    horsesIterator.forEachRemaining(horses::add);
    // We don't have height and weight of the horses here, so no reason to test for them.
    assertThat(horses)
        .extracting("id", "name", "sex", "dateOfBirth", "breed.name")
        .as("ID, Name, Sex, Date of Birth, Breed Name")
        .containsExactlyInAnyOrder(
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6), "Welsh Cob"),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20), "Welsh Pony")
        );
  }

  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .queryParam("bornEarliest", LocalDate.of(2017, 3, 5).toString())
            .queryParam("bornLatest", LocalDate.of(2018, 10, 10).toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var horsesResult = objectMapper.readerFor(HorseListDto.class)
        .<HorseListDto>readValues(body);
    assertNotNull(horsesResult);

    var horses = new ArrayList<HorseListDto>();
    horsesResult.forEachRemaining(horses::add);

    assertThat(horses)
        .hasSize(4)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
        .containsExactlyInAnyOrder(
            tuple(-24L, "Rocky", Sex.MALE, LocalDate.of(2018, 8, 19),
                "Dartmoor Pony"),
            tuple(-26L, "Daisy", Sex.FEMALE, LocalDate.of(2017, 12, 1),
                "Hanoverian"),
            tuple(-31L, "Leo", Sex.MALE, LocalDate.of(2017, 3, 5),
                "Haflinger"),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10),
                "Welsh Cob"));
  }

  @Test
  public void validationOnNewInvalidHorses() throws Exception {

    HorseDetailDto noNameHorse = new HorseDetailDto(null, null, Sex.FEMALE, LocalDate.of(2002, 6, 21), 2.5f, 300, null);
    var response = sendPostRequest(noNameHorse);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse name was not defined");
    System.out.println(response.getContentAsString());

    HorseDetailDto noSexHorse = new HorseDetailDto(null, "Rary", null, LocalDate.of(2002, 6, 21), 2.5f, 300, null);
    response = sendPostRequest(noSexHorse);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse sex was not defined");

    HorseDetailDto noDateHorse = new HorseDetailDto(null, "Rary", Sex.MALE, null, 2.5f, 300, null);
    response = sendPostRequest(noDateHorse);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse date of birth is not defined");

    HorseDetailDto noHeightHorse = new HorseDetailDto(null, "Rary", Sex.MALE, LocalDate.of(2002, 6, 21), 0, 300, null);
    response = sendPostRequest(noHeightHorse);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse height can not be 0 / is not defined");

    HorseDetailDto noWeightHorse = new HorseDetailDto(null, "Rary", Sex.MALE, LocalDate.of(2002, 6, 21), 2.5f, 0, null);
    response = sendPostRequest(noWeightHorse);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse weight can not be 0 / is not defined");

    HorseDetailDto noWeightAndNoNameHorse = new HorseDetailDto(null, null, Sex.MALE, LocalDate.of(2002, 6, 21), 2.5f, 0, null);
    response = sendPostRequest(noWeightAndNoNameHorse);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse weight can not be 0 / is not defined");
    assertThat(response.getContentAsString()).contains("Horse name was not defined");
    System.out.println(response.getContentAsString());

    HorseDetailDto notExistingBreedHorse = new HorseDetailDto(null,
            "Baltazar",
            Sex.MALE,
            LocalDate.of(2002, 6, 21), 2.5f, 300,
            new BreedDto(-502L, "really random"));
    response = sendPostRequest(notExistingBreedHorse);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("No such breed Id: -502");
    System.out.println(response.getContentAsString());

    HorseDetailDto nullOnIdOfBreed = new HorseDetailDto(null, "Baltazar", Sex.MALE, LocalDate.of(2002, 6, 21), 2.5f, 300, new BreedDto(0, "Andalusian"));
    response = sendPostRequest(nullOnIdOfBreed);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("No such breed Id: 0");
    System.out.println(response.getContentAsString());

  }

  @Test
  public void addNewValidHorses() throws Exception {

    HorseDetailDto horse1 = new HorseDetailDto(null, "Lola", Sex.FEMALE, LocalDate.of(2002, 6, 21), 2.5f, 300, null);
    var response = sendPostRequest(horse1);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    System.out.println("Response: " + response.getContentAsString());

    List<HorseListDto> newCreatedHorse = objectMapper.readerFor(HorseListDto.class)
            .<HorseListDto>readValues(response.getContentAsByteArray()).readAll();

    assertThat(newCreatedHorse).isNotNull();
    assertThat(newCreatedHorse)
            .hasSize(1)
            .extracting(HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, HorseListDto::breed)
            .contains(
                    tuple("Lola", Sex.FEMALE, LocalDate.of(2002, 6, 21), null));

    HorseDetailDto horse2 = new HorseDetailDto(502L, "Nanny with id ignored", Sex.FEMALE, LocalDate.of(2010, 6, 21), 1.5f, 150, null);
    response = sendPostRequest(horse2);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    System.out.println("Response: " + response.getContentAsString());

    newCreatedHorse = objectMapper.readerFor(HorseListDto.class)
            .<HorseListDto>readValues(response.getContentAsByteArray()).readAll();

    assertThat(newCreatedHorse).isNotNull();
    assertThat(newCreatedHorse)
            .hasSize(1)
            .extracting(HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, HorseListDto::breed)
            .contains(
                    tuple("Nanny with id ignored", Sex.FEMALE, LocalDate.of(2010, 6, 21), null));

    HorseDetailDto horse3 = new HorseDetailDto(3L, "Nanny", Sex.FEMALE, LocalDate.of(2010, 6, 21), 1.5f, 150, new BreedDto(-1, null));
    response = sendPostRequest(horse3);

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    System.out.println("Response: " + response.getContentAsString());

    newCreatedHorse = objectMapper.readerFor(HorseListDto.class)
            .<HorseListDto>readValues(response.getContentAsByteArray()).readAll();

    assertThat(newCreatedHorse).isNotNull();
    assertThat(newCreatedHorse)
            .hasSize(1)
            .extracting(HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
            .containsExactlyInAnyOrder(
                    tuple("Nanny", Sex.FEMALE, LocalDate.of(2010, 6, 21),
                            "Andalusian"));
  }

  @Test
  public void editHorseWithInvalidContent() throws Exception {
    HorseDetailDto horse1 = new HorseDetailDto(null, "Lola", Sex.FEMALE, LocalDate.of(2002, 6, 21), 2.5f, 300, null);
    var response = sendPostRequest(horse1);
    System.out.println("Created Horse: " + response.getContentAsString());

    HorseDetailDto newCreatedHorse = objectMapper.readerFor(HorseDetailDto.class)
            .<HorseDetailDto>readValue(response.getContentAsByteArray());

    response = sendPutRequest(
            new HorseDetailDto(null, "Lola updated", Sex.FEMALE, LocalDate.of(2002, 6, 21), 0, 0, null),
            newCreatedHorse.id());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse height can not be 0 / is not defined");
    assertThat(response.getContentAsString()).contains("Horse weight can not be 0 / is not defined");

    response = sendPutRequest(
            new HorseDetailDto(null, null, null, LocalDate.of(2002, 6, 21), 2.5f, 300, null),
            newCreatedHorse.id());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    assertThat(response.getContentAsString()).contains("Horse name was not defined");

    response = sendPutRequest(
            new HorseDetailDto(null, "Lola updated", Sex.FEMALE, LocalDate.of(2002, 6, 21), 2.5f, 300, new BreedDto(-1, null)),
            -5000);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    System.out.println(response.getContentAsString());
    // assertThat(response.getContentAsString()).contains("No such breed Id");

    response = sendPutRequest(
            new HorseDetailDto(null, "Lola updated", Sex.FEMALE, LocalDate.of(2002, 6, 21), 2.5f, 300, null),
            -5000);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    System.out.println(response.getContentAsString());

    response = sendPutRequest(
            new HorseDetailDto(null, "Lola updated", Sex.FEMALE, LocalDate.of(2002, 6, 21), 0, 0, new BreedDto(-5000, null)),
            newCreatedHorse.id());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  private MockHttpServletResponse sendPostRequest(HorseDetailDto horse) throws Exception {
    var body = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(horse)))
            .andReturn();

    return body.getResponse();
  }

  private MockHttpServletResponse sendPutRequest(HorseDetailDto horse, long id) throws Exception {
    var body = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(horse)))
            .andReturn();

    return body.getResponse();
  }
}
