package agent;

import java.io.PrintWriter;

public abstract class BaseRunnable {
	protected RndUtil rndUtil;
	protected Agent agent;

	public BaseRunnable(Agent agent, RndUtil rndUtil) {
		this.agent = agent;
		this.rndUtil = rndUtil;
	}

	public BaseRunnable(Agent agent, int maxTimeout, int minTimeout) {
		this.agent = agent;
		this.rndUtil = new RndUtil(Agent.MAX_PORT, Agent.MIN_PORT, maxTimeout,
				minTimeout);
	}

	protected void sendMessage(PrintWriter pw, String msg) {
		pw.println(msg);
		pw.flush();
	}

	protected void sendMessage(PrintWriter pw, int msg) {
		pw.println(msg);
		pw.flush();
	}
}
