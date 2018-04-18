package agent;

import java.io.IOException;
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
		try (ServerSocket server = new ServerSocket(rndUtil.generatePort());) {
			server.setSoTimeout(rndUtil.getMaxTimeout());
			while (true) {
				try (Socket client = server.accept(); Scanner socketSc = new Scanner(client.getInputStream()); PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
					// A szerver elküldi az álnevei közül az egyiket
					// véletlenszerűen.
					sendMessage(socketPw, agent.getRndName());

					int tip = Integer.parseInt(socketSc.nextLine());
					// Különben a szerver elküldi az OK szöveget.
					if (tip == agent.getAgency().getCode()) {
						sendMessage(socketPw, "OK");
						String msg = socketSc.nextLine();
						if (msg.equals("???")) {
							sendMessage(socketPw, agent.getAgency().getAgents());
							int guessedAgentCode = Integer.parseInt(socketSc.nextLine());
							if (agent.getAgentCode() == guessedAgentCode) {
								sendMessage(socketPw, agent.getRndSecret(true));

							} else {
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
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				if (!agent.isHasAvailableSecrets()) {
					agent.stopServerThread();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}