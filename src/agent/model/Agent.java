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
	private boolean hasAvailableSecrets;
	private List<String> names;
	private Map<String, Integer> badTips;
	private Map<String, Boolean> secrets;
	private Map<Integer, List<Integer>> guessedNumbers;
	private Map<Integer, Integer> finalNumbers;

	// Szálak:
	private Thread clientThread;
	private Thread serverThread;
	private ClientRunnable clientRunnable;
	private ServerRunnable serverRunnable;
	
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
		badTips = new HashMap<>();
		secrets = new HashMap<>();
		guessedNumbers = new HashMap<>();
		setFinalNumbers(new HashMap<>());

		this.agency = agency;
		this.agentCode = agentCode;

		this.setHasAvailableSecrets(true);

		System.out.println(String.format("\n%s. ügynökséghez tartozó %d. ügynök adatai:", agency.getName(), agentCode));

		readInformation();

		System.out.println("Álnevek: ");
		for (int i = 0; i < names.size(); i++) {
			System.out.println("\t" + names.get(i));
		}
		System.out.println("Titkok:");
		printSecrets();
	}

	/**
	 * Szálak indítása:
	 */
	public void startThreads() {
		System.out.println(String.format("%d - %d ügynök szerver szál indítása...", agency.getCode(), this.agentCode));
		serverRunnable = new ServerRunnable(this);
		serverThread = new Thread(serverRunnable);
		serverThread.start();;
		
		System.out.println(String.format("%d - %d ügynök kliens szál indítása...", agency.getCode(), this.agentCode));
		clientRunnable = new ClientRunnable(this);
		clientThread = new Thread(clientRunnable);	
		clientThread.start();
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

		if (checkValue) {
			return secretsList.get(this.rnd.nextInt(secretsList.size()));
		} else {
			String tempSecret = secretsList.get(this.rnd.nextInt(secretsList.size()));
			while (secrets.get(tempSecret) == false) {
				tempSecret = secretsList.get(this.rnd.nextInt(secretsList.size()));
			}
			secrets.put(tempSecret, false);
			if (!hasAvailableSecrets()) {
				setHasAvailableSecrets(false);
			}
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

	
	/*-----------GETTERS & SETTERS--------------*/
	
	
	private boolean hasAvailableSecrets() {
		return this.secrets.containsValue(true);
	}

	public int getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(int agentCode) {
		this.agentCode = agentCode;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public Map<String, Integer> getBadTips() {
		return badTips;
	}

	public void setBadTips(Map<String, Integer> badTips) {
		this.badTips = badTips;
	}

	public Map<String, Boolean> getSecrets() {
		return secrets;
	}

	public void setSecrets(Map<String, Boolean> secrets) {
		this.secrets = secrets;
	}

	public Map<Integer, List<Integer>> getGuessedNumbers() {
		return guessedNumbers;
	}

	public void setGuessedNumbers(Map<Integer, List<Integer>> guessedNumbers) {
		this.guessedNumbers = guessedNumbers;
	}

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	public Map<Integer, Integer> getFinalNumbers() {
		return finalNumbers;
	}

	public void setFinalNumbers(Map<Integer, Integer> finalNumbers) {
		this.finalNumbers = finalNumbers;
	}

	public boolean isHasAvailableSecrets() {
		return hasAvailableSecrets;
	}

	public void setHasAvailableSecrets(boolean hasAvailableSecrets) {
		this.hasAvailableSecrets = hasAvailableSecrets;
	}
	
	public ServerRunnable getServerRunnable() {
		return this.serverRunnable;
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