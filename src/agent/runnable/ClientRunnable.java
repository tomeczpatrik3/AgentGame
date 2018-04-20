package agent.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import agent.config.Constraint;
import agent.model.Agent;
import agent.util.RndUtil;

public class ClientRunnable extends BaseRunnable implements Runnable {
	public ClientRunnable(Agent agent) {
		super(agent);
	}

	@Override
	public void run() {
		Random rnd = new Random();
		while (true) {
			try (Socket client = new Socket(Constraint.ADRESS, RndUtil.generatePort()); Scanner socketSc = new Scanner(client.getInputStream()); PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
				String name = socketSc.nextLine();

				// <----- PROTOKOL ----->
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
							// A másik ügynökségben dolgozó ügynökök számának
							// lekérdezése:
							int otherAgents = Integer.parseInt(socketSc.nextLine());
							List<Integer> agentCodeTips;
							// Ha már tippeltünk erre a szerverre:
							if (agent.getGuessedNumbers().containsKey(clientPort)) {
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

								// Ha helyesen tippeltünk:
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

			// Játék végének a vizsgálata:
			if (!agent.isHasAvailableSecrets()) {
				agent.stopClientThread();
			}
		}
	}
}