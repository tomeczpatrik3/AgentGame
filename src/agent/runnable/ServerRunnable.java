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
		isOver = false;
	}

	@Override
	public void run() {
		while (!isOver) {
			try {
				ServerSocket server = createServer();
				server.setSoTimeout(Constants.MAX_TIMEOUT);
				
				try (Socket client = server.accept(); Scanner socketSc = new Scanner(client.getInputStream()); PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
					System.out.println("Szerver: Sikeresen kapcsolodott egy kliens!");
					
					// <----- PROTOKOL ----->
					// A szerver elküldi az álnevei közül az egyiket
					// véletlenszerűen.
					System.out.println("Szerver: Véletlen név elküldése!");
					sendMessage(socketPw, agent.getRndName());

					//Tipp fogadása:
					int tip = Integer.parseInt(socketSc.nextLine());
					log("Fogadott tipp - "+tip);
					
					// Ha a kliens jól tippelt, a szerver elküldi az OK szöveget.
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
					
				} catch (SocketTimeoutException ex) {
					System.err.println("Időtúllépés, újrapróbálkozás!");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// Játék végének a vizsgálata:
				if (!agent.isHasAvailableSecrets()) {
					isOver = true;
				}

				server.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Szerver generálása véletlen porton:
	 * 
	 * @return
	 */
	private ServerSocket createServer() {
		System.out.println("Szerver generálása véletlen porton: ");
		while (true) {
			try {
				return new ServerSocket(RndUtil.generatePort());
			} catch (IOException ex) {
				System.err.println("A port foglalt volt...");
				continue;
			}
		}
	}
	
	@Override
	protected void log(String msg) {
		System.out.println("Szerver: "+msg);
	}
}