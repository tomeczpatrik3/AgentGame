package agent.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import agent.config.Constants;
import agent.model.Agent;
import agent.util.RndUtil;

public class ClientRunnable extends BaseRunnable implements Runnable {
	public ClientRunnable(Agent agent) {
		super(agent);
	}

	@Override
	public void run() {
		Random rnd = new Random();
		Socket client;

		while (!isOver) {
			try {
				//Ha tesztelünk:
				if (agent.isTestMode()) {
					log("----- TESZT MÓD -----");
					client = new Socket(Constants.ADRESS, agent.getClientPort());
				}
				//Ha nem:
				else
					client = createSocket();
				try (Scanner socketSc = new Scanner(client.getInputStream());
						PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
					log("Sikeres kapcsolodas a szervehez!");

					String name = socketSc.nextLine();
					log("Név fogadva - " + name);

					// <----- PROTOKOL ----->
					// Erre a kliens elküldi azt, hogy szerinte a szerver melyik
					// ügynökséghez tartozik.
					// HELYES VÁLASZ:
					log("Ügynökség tippelése");
					int tip;
					if (agent.getBadTips().containsKey(name)) {
						int wrongAgencyCode = agent.getBadTips().get(name);
						tip = (wrongAgencyCode == 1 ? 2 : 1);
						log("Helyes válasz meghatározva - " + tip);
						sendMessage(socketPw, tip);
					}
					// TIPP:
					else {
						tip = rnd.nextInt(2) + 1;
						log("Tippelés - " + tip);
						sendMessage(socketPw, tip);
					}
					//
					if (client.isConnected()) {

						// Ha jól tippelt:
						if (socketSc.hasNextLine() && socketSc.nextLine().equals("OK")) {
							log("Helyes tipp");
							// Ha azonos ügynökséghez tartoznak:
							if (tip == agent.getAgency().getCode()) {
								log("Azonos ügynökséghez tartoznak");
								// Véletlen secret küldése:
								log("Véletlen titok elküldése");
								sendMessage(socketPw, agent.getRndSecret(false));
								// Secret fogadása és mentése:
								log("Titok fogadása és mentése");
								agent.getSecrets().put(socketSc.nextLine(), true);
								// Kapcsolat zárása:
								log("Kapcsolat bontása");
								client.close();
							}

							// Ha nem azonos ügynökséghez tartoznak:
							else {
								log("Különböző ügynökséghez tartoznak");
								sendMessage(socketPw, "???");
								// sendMessage(socketPw, );
								int agentCodeTip = -1;
								int clientPort = client.getPort();
								// A másik ügynökségben dolgozó ügynökök
								// számának
								// lekérdezése:
								int otherAgents = Integer.parseInt(socketSc.nextLine());
								List<Integer> agentCodeTips;
								// Ha már tippeltünk erre a szerverre:
								if (agent.getGuessedNumbers().containsKey(clientPort)) {
									agentCodeTips = agent.getGuessedNumbers().get(clientPort);
									// Ha már tudjuk a kódját:
									if (agent.getFinalNumbers().containsKey(clientPort)) {
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
								} else {
									agentCodeTip = rnd.nextInt(otherAgents);
									agent.getGuessedNumbers().put(clientPort, Arrays.asList(agentCodeTip));
									sendMessage(socketPw, agentCodeTip);
								}
								// Ha helyesen tippeltünk:
								if (client.isConnected()) {
									if (!agent.getFinalNumbers().containsKey(clientPort) && agentCodeTip != -1) {
										agent.getFinalNumbers().put(clientPort, agentCodeTip);
									}
									if (socketSc.hasNextLine())
										agent.getSecrets().put(socketSc.nextLine(), true);
								}

							}
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				// Játék végének a vizsgálata:
				if (!agent.isHasAvailableSecrets()) {
					isOver = true;
				}
				
				sleepRandomTime();
			} catch (IOException ex) {
				sleepRandomTime();
			}
		}
	}

	public void sleepRandomTime() {
		System.out.println("Alvás véletlen hosszú ideig...");
		try {
			Thread.sleep(RndUtil.generateTimeout());
		} catch (InterruptedException ex) {
			System.err.println("Szüneteltetés megszakítva!");
		}
	}

	/**
	 * Kliens generálása véletlen porton:
	 * 
	 * @return
	 */
	private Socket createSocket() {
		System.out.println("Kliens generálása véletlen porton: ");
		while (true) {
			try {
				return new Socket(Constants.ADRESS, RndUtil.generatePort());
			} catch (IOException ex) {
				System.err.println("A port foglalt volt...");
				continue;
			}
		}
	}

	@Override
	protected void log(String msg) {
		System.out.println("Kliens: " + msg);
	}
}