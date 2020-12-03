package com.numgenerator.numgenerator.numgenerator.business;

import java.util.Random;
import java.util.UUID;

/**
 * @author adithyar
 */
public class Util {

	/**
	 * Simple util method to get next randomUUID
	 * @return randomUUID string
	 */
	public static String getNextRandomUUID(){
		return UUID.randomUUID().toString();
	}
}
