package agent.runnable;

import java.io.PrintWriter;

import agent.model.Agent;

public abstract class BaseRunnable {
	protected Agent agent;
	protected boolean isOver;

	public BaseRunnable(Agent agent) {
		this.agent = agent;
		this.isOver = false;
	}

	protected void sendMessage(PrintWriter pw, String msg) {
		pw.println(msg);
		pw.flush();
	}

	protected void sendMessage(PrintWriter pw, int msg) {
		pw.println(msg);
		pw.flush();
	}
	
	protected abstract void log(String msg);
}
