package com.numgenerator.numgenerator.numgenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numgenerator.numgenerator.numgenerator.models.GoalRequest;
import com.numgenerator.numgenerator.numgenerator.models.Result;
import com.numgenerator.numgenerator.numgenerator.models.ResultState;
import com.numgenerator.numgenerator.numgenerator.models.Task;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
@Slf4j
class NumgeneratorApplicationTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	@SneakyThrows
	public void generateNumbers(){
		GoalRequest goalRequest = new GoalRequest();
		goalRequest.setGoal("10");
		goalRequest.setStep("2");
		ObjectMapper mapper = new ObjectMapper();
		String body = mapper.writeValueAsString(goalRequest);

		//Make initial call to generate nums
		String response = this.mockMvc.perform(post("/api/generate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.task", notNullValue()))
			.andReturn()
			.getResponse()
			.getContentAsString();
		Task task = mapper.readValue(response, Task.class);

		//Check status
		Result status =
			mapper.readValue(mockMvc.perform(get("/api/tasks/" + task.getTask() + "/status"))
				.andReturn()
				.getResponse()
				.getContentAsString(), Result.class);

		assertEquals(ResultState.IN_PROGRESS.getState(),status.getResult());

		//Get status, check if task is still in progress
		//Wait till task completes
		String result = "";
		Result result1;
		do {
			result = mockMvc.perform(get("/api/tasks/"+task.getTask()+"/status")).andReturn().getResponse().getContentAsString();
			result1 = mapper.readValue(result,Result.class);
			log.info("Current status for task {} is {} - Checking after 2000", task.getTask(),result1.getResult());
			sleep(2000);
		}while (result1.getResult().equals(ResultState.IN_PROGRESS.getState()));


		//Check numberList returned
		String numListResponse = mockMvc.perform(get("/api/tasks/"+task.getTask()+"?action=get_numlist"))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		Result numbers = mapper.readValue(numListResponse,Result.class);
		String expectedResult = "10,8,6,4,2,0";

		assertEquals(expectedResult,numbers.getResult());

		//Check status to be success for task
		String statusString =
			mockMvc.perform(get("/api/tasks/"+task.getTask()+"/status")).andReturn().getResponse().getContentAsString();
		Result res = mapper.readValue(statusString,Result.class);

		assertEquals(ResultState.SUCCESS.getState(),res.getResult());

	}

	@Test
	@SneakyThrows
	public void bulkGenerate(){
		GoalRequest goalRequest = new GoalRequest();
		goalRequest.setGoal("10");
		goalRequest.setStep("2");

		GoalRequest goalRequest2 = new GoalRequest();
		goalRequest2.setGoal("4");
		goalRequest2.setStep("2");

		List<GoalRequest> goalRequestList = new ArrayList<>();
		goalRequestList.add(goalRequest);
		goalRequestList.add(goalRequest2);

		ObjectMapper mapper = new ObjectMapper();
		String body = mapper.writeValueAsString(goalRequestList);
		System.out.println(body);

		//Make initial call to generate nums
		String response =
			mockMvc.perform(post("/api/bulkGenerate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.task", notNullValue()))
			.andReturn()
			.getResponse()
			.getContentAsString();
		Task task = mapper.readValue(response, Task.class);

		//Check status
		Result status =
			mapper.readValue(mockMvc.perform(get("/api/tasks/" + task.getTask() + "/status"))
				.andReturn()
				.getResponse()
				.getContentAsString(), Result.class);

		assertEquals(ResultState.IN_PROGRESS.getState(),status.getResult());

		//Get status, check if task is still in progress
		//Wait till task completes
		String result = "";
		Result result1;
		do {
			result = mockMvc.perform(get("/api/tasks/"+task.getTask()+"/status")).andReturn().getResponse().getContentAsString();
			result1 = mapper.readValue(result,Result.class);
			log.info("Current status for task {} is {} - Checking after 2000", task.getTask(),result1.getResult());
			sleep(2000);
		}while (result1.getResult().equals(ResultState.IN_PROGRESS.getState()));


		//Check numberList returned
		String numListResponse = mockMvc.perform(get("/api/tasks/"+task.getTask()+"?action=get_numlist"))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		Result numbers = mapper.readValue(numListResponse,Result.class);
		System.out.println(numbers);

		String expectedResult = "[10,8,6,4,2,0, 4,2,0]";

		assertEquals(expectedResult,numbers.getResult());

		//Check status to be success for task
		String statusString =
			mockMvc.perform(get("/api/tasks/"+task.getTask()+"/status")).andReturn().getResponse().getContentAsString();
		Result res = mapper.readValue(statusString,Result.class);

		assertEquals(ResultState.SUCCESS.getState(),res.getResult());

	}

	@Test
	@SneakyThrows
	public void testInvalidTaskIdStatus(){
		GoalRequest goalRequest = new GoalRequest();
		goalRequest.setGoal("10");
		goalRequest.setStep("2");
		ObjectMapper mapper = new ObjectMapper();
		String body = mapper.writeValueAsString(goalRequest);

		//Make initial call to generate nums
		String response = mockMvc.perform(post("/api/generate").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.task", notNullValue()))
			.andReturn()
			.getResponse()
			.getContentAsString();
		Task task = mapper.readValue(response, Task.class);

		String dummyTask = "12345";
		//Check status
		Result status =
			mapper.readValue(mockMvc.perform(get("/api/tasks/" + dummyTask + "/status"))
				.andReturn()
				.getResponse()
				.getContentAsString(), Result.class);

		assertEquals(ResultState.INVALID_TASK.getState(),status.getResult());
	}

}
