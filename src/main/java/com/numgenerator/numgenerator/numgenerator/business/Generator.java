package com.numgenerator.numgenerator.numgenerator.business;

import com.numgenerator.numgenerator.numgenerator.models.Task;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author adithyar
 */
public interface Generator {
	public CompletableFuture<String> generate(Task task, int goal, int steps, int lowerBound, int upperBound);
}
