package agent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ClientRunnable extends BaseRunnable implements Runnable {
	public ClientRunnable(Agent agent, RndUtil rndUtil) {
		super(agent, rndUtil);
	}

	public ClientRunnable(Agent agent, int maxTimeout, int minTimeout) {
		super(agent, maxTimeout, minTimeout);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket client = new Socket(Agent.ADRESS, rndUtil.generatePort());
				Scanner socketSc = new Scanner(client.getInputStream());
				PrintWriter socketPw = new PrintWriter(client.getOutputStream());
				Random rnd = new Random();

				String name = socketSc.nextLine();
				// Ha már tippeltünk, de rosszul:
				if (agent.getBadTips().containsKey(name)) {
					int wrongAgencyCode = agent.getBadTips().get(name);
					int newTip = (wrongAgencyCode == 1 ? 2 : 1);
					sendMessage(socketPw, newTip);
				} else {
					sendMessage(socketPw, rnd.nextInt(2) + 1);
				}

				// Thread.sleep(rndUtil.generateTimeout());
			} catch (IOException ex) {
				// Thread.sleep(rndUtil.generateTimeout());
			}
		}
	}
}