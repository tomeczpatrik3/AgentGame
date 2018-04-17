package agent;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerRunnable extends BaseRunnable implements Runnable {
	public ServerRunnable(Agent agent, RndUtil rndUtil) {
		super(agent, rndUtil);
	}

	public ServerRunnable(Agent agent, int maxTimeout, int minTimeout) {
		super(agent, maxTimeout, minTimeout);
	}

	@Override
	public void run() {
		while (true) {
			try {
				ServerSocket server = new ServerSocket(rndUtil.generatePort());
				server.setSoTimeout(rndUtil.getMaxTimeout());
				Socket client = server.accept();
				Scanner socketSc = new Scanner(client.getInputStream());
				PrintWriter socketPw = new PrintWriter(client.getOutputStream());

				sendMessage(socketPw, agent.getRndName());

				int tip = Integer.parseInt(socketSc.nextLine());
				if (tip == agent.getAgencyCode()) {
					sendMessage(socketPw, "OK");
				} else {
					client.close();
				}

				// client.wait();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}
}