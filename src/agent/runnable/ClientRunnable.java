package agent.runnable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import agent.config.Constants;
import agent.model.Agency;
import agent.model.Agent;
import agent.util.RndUtil;

public class ClientRunnable extends BaseRunnable implements Runnable {
	private Random rnd;

	public ClientRunnable(Agent agent) {
		super(agent);
		rnd = new Random();
	}

	@Override
	public void run() {
		Socket client;

		while (agent.hasAvailableSecrets() && !agent.getAgency().isGameOver()) {
			try {
				client = createSocket();
				agent.setClientPort(client.getLocalPort());
				
				try (Scanner socketSc = new Scanner(client.getInputStream()); PrintWriter socketPw = new PrintWriter(client.getOutputStream());) {
					log("Sikeres kapcsolodas a szervehez!");

					String name = socketSc.nextLine();
					log("Név fogadva - " + name);

					// <----- PROTOKOL ----->

					// <----- ÜGYNÖKSÉG MEGTIPPELÉSE ----->
					log("Ügynökség tippelése");
					int agencyCodeTip = guessAgencyCode(name);
					sendMessage(socketPw, agencyCodeTip);

					// <----- HA JÓL TIPPELTÜNK ----->
					// Ha nem, akkor a szerver bontja a kapcsolatot
					if (socketSc.hasNextLine() && socketSc.nextLine().equals("OK")) {
						
						log("Helyes tipp az ügynökségre");
						agent.getAgencyCodeTips().get(name).put(agencyCodeTip, true);
						
						// Ha azonos ügynökséghez tartoznak:
						if (agencyCodeTip == agent.getAgency().getCode()) {
							onSameAgency(socketPw, socketSc);
						}

						// Ha nem azonos ügynökséghez tartoznak:
						else {
							onDifferentAgencies(name, socketPw, socketSc);
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				client.close();
				sleepRandomTime();
			//Ha a szerver, amire csatlakozni szeretnénk már nem él
			} catch (ConnectException ex) {
				//System.err.println("A szerver, amire csatlakozni szeretnénk már nem él!");
			} catch (IOException ex) {
				// Ha roszul tippeltünk az ügynökségre:
				log("Helytelen tipp az ügynökségre");
				sleepRandomTime();
			} catch (Exception ex) {
				//Ha valami más történt:
			}
		}
	}

	/**
	 * Alvás véletlen hosszú ideig:
	 */
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
	 * @return
	 */
	private Socket createSocket() {
		System.out.println("Kliens generálása véletlen porton: ");
		while (!agent.getAgency().isGameOver()) {
			try {
				return new Socket(Constants.ADRESS, RndUtil.generatePort(agent.getServerPort()));
			} catch (IOException ex) {
				//System.err.println("A port foglalt volt...");
				continue;
			}
		}
		return null;
	}
	
	/**
	 * Az ügynökség azonosítójának "tippelését" megvalósító függvény:
	 * @param name Az ügynök álneve
	 * @return A tippelt ügynökség azonosító
	 */
	private int guessAgencyCode(String name) {
		int agencyCodeTip;
		// Ha tippeltünk már erre a névre:
		if (agent.getAgencyCodeTips().containsKey(name)) {
			log("Erre a névre tippeltünk már ügynökség kódot");
			if (agent.hasCorrectAgencyTipForName(name)) {
				log("Volt már helyes tippünk!");
				agencyCodeTip = agent.getCorrectAgencyTipForName(name);
				log("Helyes válasz meghatározva - " + agencyCodeTip);
			} else {
				log("Nem volt még helyes tippünk!");
				Set<Integer> badTips = agent.getAgencyCodeTips().get(name).keySet();
				agencyCodeTip = rnd.nextInt(Agency.agencyCount) + 1;
				while (badTips.contains(agencyCodeTip))
					agencyCodeTip = rnd.nextInt(Agency.agencyCount) + 1;
				log("Tippelés - " + agencyCodeTip);
				agent.getAgencyCodeTips().get(name).put(agencyCodeTip, false);
			}
		}
		// Ha még nem tippeltünk erre a névre:
		else {
			log("Nem volt még tippünk!");
			agent.getAgencyCodeTips().put(name, new HashMap<Integer, Boolean>());
			agencyCodeTip = rnd.nextInt(Agency.agencyCount) + 1;
			log("Tippelés - " + agencyCodeTip);
			agent.getAgencyCodeTips().get(name).put(agencyCodeTip, false);
		}
		return agencyCodeTip;
	}
	
	/**
	 * Az ügynök azonosítójának "tippelését" megvalósító függvény:
	 * @param name 			Az ügynök álneve
	 * @param agentsCount 	Az ügynökök száma a másik ügynökségnél
	 * @return
	 */
	private int guessAgentCode(String name, int agentsCount) {
		int agentCodeTip;
		// Ha már tippeltünk erre a névre:
		if (agent.getAgentCodeTips().containsKey(name)) {
			log("Már tippeltünk erre az névre");
			// Ha már tudjuk a kódját:
			if (agent.hasCorrectAgentCodeTipForName(name)) {
				agentCodeTip = agent.getCorrectAgentCodeTipForName(name);
				log("Tudjuk már a kódját - " + agentCodeTip);
			}
			// Ha még nem tudjuk a kódját:
			else {
				log("Még nem tudjuk a kódját");
				Set<Integer> badTips = agent.getAgentCodeTips().get(name).keySet();
				
				agentCodeTip = rnd.nextInt(agentsCount)+1;
				while (badTips.contains(agentCodeTip)) {
					agentCodeTip = rnd.nextInt(agentsCount)+1;
				}
				
				agent.getAgentCodeTips().get(name).put(agentCodeTip, false);
				
				log("A tippünk - " + agentCodeTip);
			}
		
		// Ha még nem tippeltünk erre a névre:
		} else {
			log("Még nem tippeltünk erre az névre");
			agent.getAgentCodeTips().put(name, new HashMap<Integer, Boolean>());
			agentCodeTip = rnd.nextInt(agentsCount)+1;
			log("A tippünk - " + agentCodeTip);
			agent.getAgentCodeTips().get(name).put(agentCodeTip, false);
		}
		
		return agentCodeTip;
	}
	
	/**
	 * Ha azonos ügynökséghez tartoznak:
	 * @param socketPw	A PrintWriter objektum
	 * @param socketSc	A Scanner objektum
	 */
	private void onSameAgency(PrintWriter socketPw, Scanner socketSc) {
		log("Azonos ügynökséghez tartoznak");
		// Véletlen secret küldése:
		log("Véletlen titok elküldése");
		sendMessage(socketPw, agent.getRndSecret(false));
		// Secret fogadása és mentése:
		log("Titok fogadása és mentése");
		agent.getSecrets().put(socketSc.nextLine(), true);
		// Kapcsolat zárása:
		log("Kapcsolat bontása");		
	}
	
	/**
	 * Ha különböző ügynökséghez tartoznak:
	 * @param name		Az ügynök álneve
	 * @param socketPw	A PrintWriter objektum
	 * @param socketSc	A Scanner objektum
	 */
	private void onDifferentAgencies(String name, PrintWriter socketPw, Scanner socketSc) {
		log("Különböző ügynökséghez tartoznak");
		sendMessage(socketPw, "???");
		
		// A másik ügynökségben dolgozó ügynökök
		// számának lekérdezése:
		log("Másik ügynökségben dolgozó ügynökök számának lekérdezése");
		int otherAgents = Integer.parseInt(socketSc.nextLine());

		int agentCodeTip = guessAgentCode(name, otherAgents);
		sendMessage(socketPw, agentCodeTip);

		if (socketSc.hasNextLine()) {
			log("Eltároljuk a kapott titkot");
			agent.getSecrets().put(socketSc.nextLine(), true);
			
			// Ha még nem volt eltárolva a helyes tipp:
			if (!agent.hasCorrectAgentCodeTipForName(name)) {
				log("Még nem volt helyes tipp erre a névre, ezért eltároljuk");
				agent.getAgentCodeTips().get(name).put(agentCodeTip, true);
			}
		}		
	}
	

	@Override
	protected void log(String msg) {
		System.out.println(String.format("Kliens (%d - %d): %s", agent.getAgency().getCode(), agent.getAgentCode(), msg));
	}
}