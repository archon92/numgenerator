package com.numgenerator.numgenerator.numgenerator.controllers;

import com.numgenerator.numgenerator.numgenerator.models.GoalRequest;
import com.numgenerator.numgenerator.numgenerator.models.Result;
import com.numgenerator.numgenerator.numgenerator.models.Task;
import com.numgenerator.numgenerator.numgenerator.service.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author adithyar
 */

@RestController
@RequestMapping("/api")
public class NumGenerator {

	@Autowired
	private GeneratorService generatorService;

	/**
	 * Generates a sequence of numbers based on the GoalRequest
	 * @param request GoalRequest
	 * @return Task
	 */
	@PostMapping("/generate")
	public ResponseEntity<Task> generate(@RequestBody GoalRequest request){
		Task task = generatorService.generate(request.getGoal(),request.getStep());
		return ResponseEntity.accepted().body(task);
	}

	/**
	 * Gets the status of a particular task.
	 * @param UDID UDID of the task
	 * @return Result -> Status of the task SUCCESS,IN_PROGRESS,ERROR
	 */
	@GetMapping("/tasks/{UDID}/status")
	public ResponseEntity<Result> getTaskStatus(@PathVariable String UDID){
		return ResponseEntity.ok(generatorService.getTaskStatus(UDID));
	}

	/**
	 * Gets the generated numbers for a particular task
	 * @param UDID UDID of the task
	 * @param action Action to perform currently just num_ist
	 * @return Result -> String of numbers.
	 */
	@GetMapping("/tasks/{UDID}")
	public ResponseEntity<Result> getTaskStatus(@PathVariable String UDID, @RequestParam String action){
		return ResponseEntity.ok(generatorService.getResultNumsForTask(UDID));
	}

	/**
	 * API to bulk generate numbers.Takes in a List of GoalRequest and generates numbers async
	 * @param goalRequestList List<GoalRequest>
	 * @return Task
	 */
	@PostMapping("/bulkGenerate")
	public ResponseEntity<Task> bulkGenerate(@RequestBody List<GoalRequest> goalRequestList){
		Task task = generatorService.bulkGenerate(goalRequestList);
		return ResponseEntity.accepted().body(task);
	}
}

