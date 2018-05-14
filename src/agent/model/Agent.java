package agent.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import agent.runnable.ClientRunnable;
import agent.runnable.ServerRunnable;

public class Agent {
	private Random rnd;

	/*Az ügynökség*/
	private Agency agency;
	/*Az ügynök azonosítója*/
	private int agentCode;
	/*A szerver és kliens portok*/
	private int serverPort, clientPort;
	
	/*Az ügynök álnevei*/
	private List<String> names;
	/*Az ügynökhöz tartozó titkok, és hogy elárulta-e már
	 *True: 	még nem árulta el 
	 *False: 	már elárulta valakinek
	 **/
	private Map<String, Boolean> secrets;
	/*Az egyes nevekhez tartozó ügynökség azonosító tippek*/
	private Map<String, Map<Integer, Boolean>> agencyCodeTips;
	/*Az egyes nevekhez tartozó ügynök azonosító tippek*/
	private Map<String, Map<Integer, Boolean>> agentCodeTips;

	// Szálak:
	private Thread clientThread;
	private Thread serverThread;

	/**
	 * Az alapesetben használt konstruktor:
	 * @param agency
	 * @param agentCode
	 */
	public Agent(Agency agency, int agentCode) {
		initalizeAgent(agency, agentCode);
		this.serverPort = -1;
		this.clientPort = -1;
	}
	
	/**
	 * Az objektum inicalizálását végző függvény:
	 */
	private void initalizeAgent(Agency agency, int agentCode) {
		rnd = new Random();
		agencyCodeTips = new HashMap<>();
		secrets = new HashMap<>();
		agentCodeTips = new HashMap<>();

		this.agency = agency;
		this.agentCode = agentCode;

		System.out.println(String.format("\n%s. ügynökséghez tartozó %d. ügynök adatai:", agency.getName(), agentCode));

		readInformation();
		
		agency.addScerets(secrets.keySet());

		System.out.println("Álnevek: ");
		for (int i = 0; i < names.size(); i++) {
			System.out.println("\t" + names.get(i));
		}
		System.out.println("Titkok:");
		printSecrets();
		
		serverThread = new Thread(new ServerRunnable(this));
		clientThread = new Thread(new ClientRunnable(this));
	}

	/**
	 * Szálak indítása:
	 */
	public void startThreads() {
		startServerThread();
		startClientThread();
	}
	
	/**
	 * Kliens szál indítása:
	 */
	public void startClientThread() {
		System.out.println(String.format("%d - %d ügynök kliens szál indítása...", agency.getCode(), this.agentCode));
		clientThread.start();		
	}
	
	/**
	 * Szerver szál indítása:
	 */
	public void startServerThread() {
		System.out.println(String.format("%d - %d ügynök szerver szál indítása...", agency.getCode(), this.agentCode));
		serverThread.start();		
	}

	/**
	 * Az ügynökhöz tartozó fájlnév meghatározása:
	 * @return
	 */
	private String getFileName() {
		return String.format("agent%d-%d.txt", agency.getCode(), agentCode);
	}

	/**
	 * Adatok beolvasása és tárolása:
	 */
	private void readInformation() {
		System.out.println("Adatok beolvasás a " + getFileName() + " fájlból!");
		try (Scanner sc = new Scanner(new File(getFileName()));) {

			this.names = Arrays.asList(sc.nextLine().split(" "));
			String[] scretsWords = sc.nextLine().split(" ");
			for (String secretWord: scretsWords)
				this.secrets.put(secretWord, true);

		} catch (FileNotFoundException e) {
			System.err.println(String.format("Nem létező fájl (%s)", getFileName()));
			System.exit(5);
		}
	}

	/**
	 * Véletlenszerű álnév kiválasztása:
	 * @return A kiválasztott álnév
	 */
	public String getRndName() {
		return this.names.get(this.rnd.nextInt(this.names.size()));
	}

	/**
	 * Véletlenszerű titok kiválasztása:
	 * @param checkIfUntold Ellenőrizzük-e hogy továbbadtuk-e már egy másik ügynökségnek
	 * @return A kiválasztott titok
	 */
	public String getRndSecret(boolean checkIfUntold) {
		List<String> secretsList = new ArrayList<>();
		secretsList.addAll(secrets.keySet());

		if (!checkIfUntold) {
			return secretsList.get(this.rnd.nextInt(secretsList.size()));
		} else {
			String tempSecret = secretsList.get(this.rnd.nextInt(secretsList.size()));
			while (secrets.get(tempSecret) == false) {
				tempSecret = secretsList.get(this.rnd.nextInt(secretsList.size()));
			}
			secrets.put(tempSecret, false);

			return tempSecret;
		}
	}

	/**
	 * Titkok megjelenítése:
	 */
	public void printSecrets() {
		for (String secret : secrets.keySet()) {
			System.out.printf("%s ", secret);
		}
		System.out.println();
	}
	
	/**
	 * A függvény amely ellenőrzi, hogy van-e olyan titok amit még nem árultunk el
	 * @return Igaz ha van, hamis ha nincs
	 */
	public boolean hasAvailableSecrets() {
		return this.secrets.containsValue(true);
	}

	/**
	 * A függvény amely ellenőrzi, hogy van-e helyes tippünk
	 * a névhez tartozó ügynökség azonosítóra
	 * @param name A név
	 * @return
	 */
	public boolean hasCorrectAgencyTipForName(String name) {
		boolean hasCorrectTip = false;
		for (int agencyCode: agencyCodeTips.get(name).keySet()) {
			if (agencyCodeTips.get(name).get(agencyCode) == true) {
				hasCorrectTip = true;
				break;
			}
		}
		return hasCorrectTip;
	}
	
	/**
	 * Az adott névhez tartozó helyes ügynökség azonosítót meghatározó függvény
	 * (ELŐBB: hasCorrectAgencyTipForName függvénnyel ellenőrzés)
	 * @param name A név
	 * @return
	 */
	public int getCorrectAgencyTipForName(String name) {
		int tip = 0;
		for (int agencyCode: agencyCodeTips.get(name).keySet()) {
			if (agencyCodeTips.get(name).get(agencyCode) == true) {
				tip = agencyCode;
			}
		}
		return tip;
	}
	
	/**
	 * A függvény amely ellenőrzi, hogy van-e helyes tippünk
	 * a névhez tartozó ügynök azonosítóra
	 * @param name A név
	 * @return
	 */	
	public boolean hasCorrectAgentCodeTipForName(String name) {
		boolean hasCorrectTip = false;
		for (int agentCode: agentCodeTips.get(name).keySet()) {
			if (agentCodeTips.get(name).get(agentCode) == true) {
				hasCorrectTip = true;
				break;
			}
		}
		return hasCorrectTip;		
	}
	
	/**
	 * Az adott névhez tartozó helyes ügynök azonosítót meghatározó függvény
	 * (ELŐBB: hasCorrectAgentCodeTipForName függvénnyel ellenőrzés)
	 * @param name A név
	 * @return
	 */
	public int getCorrectAgentCodeTipForName(String name) {
		int tip = 0;
		for (int agentCode: agentCodeTips.get(name).keySet()) {
			if (agentCodeTips.get(name).get(agentCode) == true) {
				tip = agentCode;
			}
		}
		return tip;
	}
	
	/**
	 * A függvény, amellyel lekérdezhető, hogy tevékenykedik-e még az ügynök:
	 * @return
	 */
	public boolean isJailed() {
		return !hasAvailableSecrets();
	}
	
	/*-----------GETTERS & SETTERS--------------*/
	public Agency getAgency() {
		return agency;
	}

	public int getAgentCode() {
		return agentCode;
	}

	public List<String> getNames() {
		return names;
	}

	public Map<String, Boolean> getSecrets() {
		return secrets;
	}
	
	public Map<String, Map<Integer, Boolean>> getAgencyCodeTips() {
		return agencyCodeTips;
	}

	public Map<String, Map<Integer, Boolean>> getAgentCodeTips() {
		return agentCodeTips;
	}

	public Thread getClientThread() {
		return clientThread;
	}

	public Thread getServerThread() {
		return serverThread;
	}

	public int getServerPort() {
		return serverPort;
	}
	
	public int getClientPort() {
		return clientPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
}