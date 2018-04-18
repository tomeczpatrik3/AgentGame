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
				// Erre a kliens elküldi azt, hogy szerinte a szerver melyik
				// ügynökséghez tartozik.
				// HELYES VÁLASZ:
				int tip;
				if (agent.getBadTips().containsKey(name)) {

					int wrongAgencyCode = agent.getBadTips().get(name);
					tip = (wrongAgencyCode == 1 ? 2 : 1);
					sendMessage(socketPw, tip);
				}
				// TIPP:
				else {
					tip = rnd.nextInt(2) + 1;
					sendMessage(socketPw, tip);
				}
				//
				if (client.isConnected()) {

					// Ha jól tippelt:
					if (socketSc.nextLine().equals("OK")) {
						// Ha azonos ügynökséghez tartoznak:
						if (tip == agent.getAgencyCode()) {
							// Véletlen secret küldése:
							sendMessage(socketPw, agent.getRndSecret(false));
							// Secret fogadása és mentése:
							agent.getSecret().put(socketSc.nextLine(), true);
							// Kapcsolat zárása:
							client.close();
						}
						// Ha nem azonos ügynökséghez tartoznak:
						else {
							sendMessage(socketPw, "???");
							sendMessage(socketPw, );
						}
					}
				}

				// Thread.sleep(rndUtil.generateTimeout());
			} catch (IOException ex) {
				// Thread.sleep(rndUtil.generateTimeout());
			}
		}
	}
}