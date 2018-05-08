package agent.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
				ServerSocket server;
				//Ha tesztelünk:
				if (agent.isTestMode()) {
					log("----- TESZT MÓD -----");
					server = new ServerSocket(agent.getServerPort());
				}
				//Ha nem:
				else {
					server = createServer();
				}
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
						log("A kliens jól tippelt");
						sendMessage(socketPw, "OK");
						String msg = socketSc.nextLine();
						// Ha különböző ügynökséghez tartoznak:
						if (msg.equals("???")) {
							log("Különböző ügynökséghez tartoznak");
							// Ügynökségen dolgozó ügynökök számának
							// elküldése:
							log("Az ügynökségen dolgozó ügynökök számának elküldése");
							sendMessage(socketPw, agent.getAgency().getAgentsNumber());
							int guessedAgentCode = Integer.parseInt(socketSc.nextLine());
							log("A kliens által tippelt ügynök azonosító - " +guessedAgentCode);
							// Ha helyes volt a tipp:
							if (agent.getAgentCode() == guessedAgentCode) {
								log("Helyes tipp, titok küldése");
								sendMessage(socketPw, agent.getRndSecret(true));
							}
							// Ha nem:
							else {
								log("Helytelen tipp, kapcsolat bontása");
								client.close();
							}
						}
						// Ha azonos ugynökséghez tartoznak:
						else {
							log("Azonos ügynökséghez tartoznak");
							// Fogadja a secretet:
							log("Titok fogadása - "+msg);
							agent.getSecrets().put(msg, true);
							// Elküld egy véletlen secret-et:
							log("Véletlen titok elküldése");
							sendMessage(socketPw, agent.getRndSecret(false));
							// Bontja a kapcsolatot:
							log("Kapcsolat bontása");
							client.close();
						}
					}
					// Ha a kliens tévedett, akkor a szerver bontja a
					// kapcsolatot.
					else {
						log("Helytelen tipp, kapcsolat bontása");
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