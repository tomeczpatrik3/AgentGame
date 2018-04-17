package agent;

import java.util.Random;

public class RndUtil {
	private Random rnd;
	private int maxPort, minPort;
	private int maxTimeout, minTimeout;

	public RndUtil(int maxPort, int minPort, int maxTimeout, int minTimeout) {
		this.rnd = new Random();
		this.maxPort = maxPort;
		this.minPort = minPort;
		this.maxTimeout = maxTimeout;
		this.minTimeout = minTimeout;
	}

	public int generatePort() {
		return rnd.nextInt(maxPort - minPort) + minPort;
	}

	public int generateTimeout() {
		return rnd.nextInt(maxTimeout - minTimeout) + minTimeout;
	}

	public int getMaxPort() {
		return maxPort;
	}

	public void setMaxPort(int maxPort) {
		this.maxPort = maxPort;
	}

	public int getMinPort() {
		return minPort;
	}

	public void setMinPort(int minPort) {
		this.minPort = minPort;
	}

	public int getMaxTimeout() {
		return maxTimeout;
	}

	public void setMaxTimeout(int maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	public int getMinTimeout() {
		return minTimeout;
	}

	public void setMinTimeout(int minTimeout) {
		this.minTimeout = minTimeout;
	}

}
