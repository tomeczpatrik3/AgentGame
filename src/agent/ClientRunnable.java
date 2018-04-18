package agent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
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
		Random rnd = new Random();
		while (true) {
			try (Socket client = new Socket(Agent.ADRESS, rndUtil.generatePort()); Scanner socketSc = new Scanner(client.getInputStream()); PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
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
						if (tip == agent.getAgency().getCode()) {
							// Véletlen secret küldése:
							sendMessage(socketPw, agent.getRndSecret(false));
							// Secret fogadása és mentése:
							agent.getSecrets().put(socketSc.nextLine(), true);
							// Kapcsolat zárása:
							client.close();
						}
						// Ha nem azonos ügynökséghez tartoznak:
						else {
							sendMessage(socketPw, "???");
							// sendMessage(socketPw, );
							int agentCodeTip = -1;
							int clientPort = client.getPort();
							int otherAgents = Integer.parseInt(socketSc.nextLine());
							List<Integer> agentCodeTips;
							if (agent.getGuessedNumbers().keySet().contains(clientPort)) {
								agentCodeTips = agent.getGuessedNumbers().get(clientPort);
								// Ha már tudjuk a kódját:
								if (agentCodeTips.size() != otherAgents) {
									sendMessage(socketPw, agent.getFinalNumbers().get(clientPort));
								}
								// Ha még nem:
								else {
									agentCodeTip = rnd.nextInt(otherAgents);
									while (!agentCodeTips.contains(agentCodeTip)) {
										agentCodeTip = rnd.nextInt(otherAgents);
									}
									agentCodeTips.add(agentCodeTip);
									agent.getGuessedNumbers().put(clientPort, agentCodeTips);
									sendMessage(socketPw, agentCodeTip);
								}
								if (client.isConnected()) {
									if (!agent.getFinalNumbers().containsKey(clientPort) && agentCodeTip != -1) {
										agent.getFinalNumbers().put(clientPort, agentCodeTip);
									}
									agent.getSecrets().put(socketSc.nextLine(), true);
								}
							}
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