package com.numgenerator.numgenerator.numgenerator.business;

import com.numgenerator.numgenerator.numgenerator.models.ResultState;
import com.numgenerator.numgenerator.numgenerator.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author adithyar
 */
@Component
public class NumberGenerator implements Generator {

	@Autowired
	private ConcurrentHashMap<String, String> idToState;

	/**
	 * Generates a stream of numbers with random intervals between generations.Returns a string at the end.
	 * @param task The task associated with the current generation
	 * @param goal Integer from which to start generation of numbers
	 * @param steps decrement count per interval generation
	 * @param lowerBound Random interval lowerBound
	 * @param upperBound Random interval upperBound
	 * @return A single string of generated numbers;
	 */
	@Override
	public CompletableFuture<String> generate(Task task, int goal, int steps, int lowerBound, int upperBound) {
		return CompletableFuture.supplyAsync(() -> {
			int g = goal;
			List<String> nums = new LinkedList<>();
			Random random = new Random();
			try {
				while (g >= 0){
					nums.add(Integer.toString(g));
					g -= steps;
					random.nextInt((upperBound - lowerBound)+ lowerBound);
					TimeUnit.SECONDS.sleep(random.nextInt((upperBound - lowerBound)+ lowerBound));
				}
			}catch (Exception e){
				idToState.put(task.getTask(), ResultState.ERROR.getState());
				e.printStackTrace();
				return null;
			}
			return String.join(",",nums);
		});
	}
}
