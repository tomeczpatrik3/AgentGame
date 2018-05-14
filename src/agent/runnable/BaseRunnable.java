package agent.runnable;

import java.io.PrintWriter;

import agent.model.Agent;

public abstract class BaseRunnable {
	protected Agent agent;

	public BaseRunnable(Agent agent) {
		this.agent = agent;
	}

	/**
	 * Az üzenet küldésére használt függvény
	 * @param pw  A PrintWriter objektum
	 * @param msg Az üzenet (String)
	 */
	protected void sendMessage(PrintWriter pw, String msg) {
		pw.println(msg);
		pw.flush();
	}

	/**
	 * Az üzenet küldésére használt függvény
	 * @param pw  A PrintWriter objektum
	 * @param msg Az üzenet (int)
	 */
	protected void sendMessage(PrintWriter pw, int msg) {
		pw.println(msg);
		pw.flush();
	}
	
	/**
	 * A konzolra történő kiírást megvalósító függvény
	 * @param msg Az üzenet
	 */
	protected abstract void log(String msg);
}
