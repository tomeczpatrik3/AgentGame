package agent.util;

import java.util.Random;

import agent.config.Constants;

public class RndUtil {
	public static Random rnd;

	/**
	 * Véletlenszerű port generálása:
	 * @return
	 */
	public static int generatePort() {
		rnd = new Random();
		return rnd.nextInt(Constants.MAX_PORT - Constants.MIN_PORT) + Constants.MIN_PORT;
	}
	
	/**
	 * Véletlenszerű, a megadott porttól különbpző port generálása:
	 * @param port
	 * @return
	 */
	public static int generatePort(int port) {
		rnd = new Random();
		int rndPort = rnd.nextInt(Constants.MAX_PORT - Constants.MIN_PORT) + Constants.MIN_PORT;
		while (rndPort == port) {
			rndPort = rnd.nextInt(Constants.MAX_PORT - Constants.MIN_PORT) + Constants.MIN_PORT;
		}
		return rndPort;
	}

	/**
	 * Véletlenszerű timeout generálása:
	 * @return
	 */
	public static int generateTimeout() {
		rnd = new Random();
		return rnd.nextInt(Constants.MAX_TIMEOUT - Constants.MIN_TIMEOUT) + Constants.MIN_TIMEOUT;
	}
}
