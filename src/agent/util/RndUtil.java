package agent.util;

import java.util.Random;

import agent.config.Constants;

public class RndUtil {
	public static Random RND = new Random();

	public static int generatePort() {
		return RND.nextInt(Constants.MAX_PORT - Constants.MIN_PORT) + Constants.MIN_PORT;
	}

	public static int generateTimeout() {
		return RND.nextInt(Constants.MAX_TIMEOUT - Constants.MIN_TIMEOUT) + Constants.MIN_TIMEOUT;
	}
}
