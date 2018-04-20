package agent.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import agent.config.Constants;
import agent.model.Agent;
import agent.util.RndUtil;

public class ServerRunnable extends BaseRunnable implements Runnable {
	public ServerRunnable(Agent agent) {
		super(agent);
	}

	@Override
	public void run() {
		try (ServerSocket server = new ServerSocket(RndUtil.generatePort());) {
			server.setSoTimeout(Constants.MAX_TIMEOUT);
			while (true) {
				try (Socket client = server.accept(); Scanner socketSc = new Scanner(client.getInputStream()); PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
					// <----- PROTOKOL ----->
					// A szerver elküldi az álnevei közül az egyiket
					// véletlenszerűen.
					sendMessage(socketPw, agent.getRndName());

					int tip = Integer.parseInt(socketSc.nextLine());
					// Különben a szerver elküldi az OK szöveget.
					if (tip == agent.getAgency().getCode()) {
						sendMessage(socketPw, "OK");
						String msg = socketSc.nextLine();
						// Ha különböző ügynökséghez tartoznak:
						if (msg.equals("???")) {
							// Ügynökségen dolgozó ügynökök számának
							// elküldése:
							sendMessage(socketPw, agent.getAgency().getAgentsNumber());
							int guessedAgentCode = Integer.parseInt(socketSc.nextLine());
							// Ha helyes volt a tipp:
							if (agent.getAgentCode() == guessedAgentCode) {
								sendMessage(socketPw, agent.getRndSecret(true));
							}
							// Ha nem:
							else {
								client.close();
							}
						}
						// Ha azonos ugynökséghez tartoznak:
						else {
							// Fogadja a secretet:
							agent.getSecrets().put(msg, true);
							// Elküld egy véletlen secret-et:
							sendMessage(socketPw, agent.getRndSecret(false));
							// Bontja a kapcsolatot:
							client.close();
						}
					}
					// Ha a kliens tévedett, akkor a szerver bontja a
					// kapcsolatot.
					else {
						client.close();
					}

					// client.wait();
				} catch (SocketTimeoutException ex) {
					System.out.println("Időtúllépés, újrapróbálkozás!");
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				// Játék végének a vizsgálata:
				if (!agent.isHasAvailableSecrets()) {
					agent.stopServerThread();
				}
				
				System.out.println("-------------");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}