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

	// Adott ügynök adatai:
	private Agency agency;
	private int agentCode;
	
	private List<String> names;
	private Map<String, Boolean> secrets;
	private Map<String, Map<Integer, Boolean>> agencyCodeTips;
	private Map<String, Map<Integer, Boolean>> agentCodeTips;

	// Szálak:
	private Thread clientThread;
	private Thread serverThread;
	
	//Teszteléshez:
	private int serverPort, clientPort;
	private boolean testMode;

	/**
	 * Az alapesetben használt konstruktor:
	 * @param agency
	 * @param agentCode
	 */
	public Agent(Agency agency, int agentCode) {
		initalizeAgent(agency, agentCode);
		this.serverPort = -1;
		this.clientPort = -1;
		this.testMode = false;
	}
	
	/**
	 * A teszteléshez használt konstruktor:
	 * @param agency
	 * @param agentCode
	 */
	public Agent(Agency agency, int agentCode, int serverPort, int clientPort) {
		initalizeAgent(agency, agentCode);
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.testMode = true;
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
	 * Adatok beolvasása fájlból:
	 */
	private void readInformation() {
		System.out.println("Adatok beolvasás a " + getFileName() + " fájlból!");
		try (Scanner sc = new Scanner(new File(getFileName()));) {

			this.names = Arrays.asList(sc.nextLine().split(" "));
			this.secrets.put(sc.nextLine(), true);

		} catch (FileNotFoundException e) {
			System.err.println(String.format("Nem létező fájl (%s)", getFileName()));
			System.exit(5);
		}
	}

	/**
	 * Véletlenszerű álnév visszaadása:
	 * @return
	 */
	public String getRndName() {
		return this.names.get(this.rnd.nextInt(this.names.size()));
	}

	/**
	 * Véletlenszerű titok visszaadása:
	 * @param checkValue
	 * @return
	 */
	public String getRndSecret(boolean checkValue) {
		List<String> secretsList = new ArrayList<>();
		secretsList.addAll(secrets.keySet());

		if (!checkValue) {
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
	 * Titkok megjelentése:
	 */
	public void printSecrets() {
		for (String secret : secrets.keySet()) {
			System.out.printf("%s ", secret);
		}
		System.out.println();
	}
	
	/**
	 * Ellenőrzi, hogy van-e olyan titok amit még nem árultunk el
	 * @return
	 */
	public boolean hasAvailableSecrets() {
		return this.secrets.containsValue(true);
	}

	/**
	 * 
	 * @param name
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
	 * A helyes ügynökségkódot meghatározó függvény
	 * (ELŐBB: hasCorrectTipForName függvénnyel ellenőrzés)
	 * @param name
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
	
	public int getCorrectAgentCodeTipForName(String name) {
		int tip = 0;
		for (int agentCode: agentCodeTips.get(name).keySet()) {
			if (agentCodeTips.get(name).get(agentCode) == true) {
				tip = agentCode;
			}
		}
		return tip;
	}
	
	public void logInformations() {
		String logInfo = "#################LOG#################\n" +
		String.format("%d ügynökség - %d ügynök ", agency.getCode(), agentCode) +
		"\n Titkok:";
		System.out.println(logInfo);
		secrets.keySet().stream().forEach(secret -> System.out.println(secret));
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

	public boolean isTestMode() {
		return testMode;
	}
}