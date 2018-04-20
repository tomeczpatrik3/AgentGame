package agent.util;

import java.util.Random;

import agent.config.Constraint;

public class RndUtil {
	public static Random RND = new Random();

	public static int generatePort() {
		return RND.nextInt(Constraint.MAX_PORT - Constraint.MIN_PORT) + Constraint.MIN_PORT;
	}

	public static int generateTimeout() {
		return RND.nextInt(Constraint.MAX_TIMEOUT - Constraint.MIN_TIMEOUT) + Constraint.MIN_TIMEOUT;
	}
}
