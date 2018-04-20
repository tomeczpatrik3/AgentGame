package agent.runnable;

import java.io.PrintWriter;
import agent.model.Agent;

public abstract class BaseRunnable {
	protected Agent agent;

	public BaseRunnable(Agent agent) {
		this.agent = agent;
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
