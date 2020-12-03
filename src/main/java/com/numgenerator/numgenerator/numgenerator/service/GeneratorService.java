package com.numgenerator.numgenerator.numgenerator.service;

import com.numgenerator.numgenerator.numgenerator.business.NumberGenerator;
import com.numgenerator.numgenerator.numgenerator.models.GoalRequest;
import com.numgenerator.numgenerator.numgenerator.models.Result;
import com.numgenerator.numgenerator.numgenerator.models.ResultState;
import com.numgenerator.numgenerator.numgenerator.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.numgenerator.numgenerator.numgenerator.business.Util.getNextRandomUUID;
import static java.lang.Integer.parseInt;

/**
 * @author adithyar
 */
@Service
public class GeneratorService {

	@Autowired
	private NumberGenerator numberGenerator;

	@Autowired
	private ConcurrentHashMap<String, Result> idToResult;

	@Autowired
	private ConcurrentHashMap<String, String> idToState;

	@Value("${random.lower-bound}")
	private int lowerBound;

	@Value("${random.upper-bound}")
	private int upperBound;

	/**
	 * Method to generate a string of numbers, Creates an async method to start generation but returns a task uuid
	 * immediately
	 * @param goal Upperbound to start num generation
	 * @param steps counter to decrement  each number
	 * @return Task
	 */

	public Task generate(String goal, String steps) {
		int g = parseInt(goal);
		int s = parseInt(steps);
		Task task = new Task();
		task.setTask(getNextRandomUUID());
		idToState.put(task.getTask(), ResultState.IN_PROGRESS.getState());
		numberGenerator.generate(task, g, s, lowerBound, upperBound)
			.whenComplete((nums, t) -> createResultForTask(nums, task));
		return task;
	}

	/**
	 * Sets the resultObject and updates the resultMap for a particular task
	 * @param nums String of generated numbers
	 * @param task Task associated with the number generation
	 */
	private void createResultForTask(String nums, Task task) {
		Result result = new Result();
		result.setResult(nums);
		idToResult.put(task.getTask(), result);
		idToState.put(task.getTask(), ResultState.SUCCESS.getState());
	}


	/**
	 * Gets the Status for a particular task. SUCCESS,INPROGRESS or ERROR
	 * @param udid UDID for the particular task
	 * @return Result SUCCESS,INPROGRESS or ERROR
	 */
	public Result getTaskStatus(String udid) {
		Result result = new Result();
		if(idToState.get(udid) == null){
			result.setResult(ResultState.INVALID_TASK.getState());
			return result;
		}
		result.setResult(idToState.get(udid));
		return result;
	}

	/**
	 * For a given task UUID, this returns the numbers generated for the particular task
	 * @param udid  udid of the associated task
	 * @return Result -> string of numbers for the particular task
	 */
	public Result getResultNumsForTask(String udid) {
		Result result = idToResult.get(udid);
		if(result != null){
			return idToResult.get(udid);
		}else if (idToState.get(udid).equals(ResultState.IN_PROGRESS.getState())){
			Result inprogress = new Result();
			inprogress.setResult(ResultState.IN_PROGRESS.getState());
			return inprogress;
		}
		Result invalidResult = new Result();
		invalidResult.setResult(ResultState.INVALID_TASK.getState());
		return invalidResult;
	}

	/**
	 * Takes in a List of GoalRequests and generates numbers async.
	 * @param requestList List of GoalRequest
	 * @return Task generated for this particular generation
	 */
	public Task bulkGenerate(List<GoalRequest> requestList) {
		Task task = new Task();
		task.setTask(getNextRandomUUID());
		idToState.put(task.getTask(), ResultState.IN_PROGRESS.getState());

		List<CompletableFuture<String>> completableFutureList = requestList.parallelStream()
			.map(request -> numberGenerator.generate(task, parseInt(request.getGoal()), parseInt(request.getStep()), lowerBound, upperBound))
			.collect(Collectors.toList());

		CompletableFuture<Void> allFutures = CompletableFuture
			.allOf(completableFutureList.toArray(new CompletableFuture[0]));

		CompletableFuture<List<String>> allCompletableFuture = allFutures.thenApply(future -> completableFutureList.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList()));
		allCompletableFuture.whenComplete((v,t) -> populateResultString(task,v));
		return task;
	}

	private void populateResultString(Task task, List<String> v) {
		Result result = new Result();
		result.setResult(v.toString());
		idToResult.put(task.getTask(), result);
		idToState.put(task.getTask(), ResultState.SUCCESS.getState());
	}
}
