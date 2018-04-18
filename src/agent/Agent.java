package agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Agent {
	// Beégetett értékek:
	public static final String ADRESS = "localhost";
	public static final int MIN_PORT = 20000;
	public static final int MAX_PORT = 20100;
	public static int agencyAAgents;
	public static int agencyBAgents;

	private Random rnd;

	// Adott ügynök adatai:
	private Agency agency;
	private int agentCode;
	private List<String> names;
	private Map<String, Integer> badTips;
	private Map<String, Boolean> secrets;
	private Map<Integer, List<Integer>> guessedNumbers;
	private Map<Integer, Integer> finalNumbers;

	// Szálak:
	private Thread clientThread;
	private Thread serverThread;

	public Agent(Agency agency, int agentCode, int minTimeout, int maxTimeout) {
		rnd = new Random();
		badTips = new HashMap<>();
		secrets = new HashMap<>();
		guessedNumbers = new HashMap<>();
		setFinalNumbers(new HashMap<>());

		this.agency = agency;
		this.agentCode = agentCode;

		System.out.println(String.format("%s. ügynökséghez tartozó %d. ügynök adatai:", agency.getName(), agentCode));

		readInformation();

		System.out.println("Álnevek: ");
		for (int i = 0; i < names.size(); i++) {
			System.out.println("\t" + names.get(i));
		}
		System.out.println("Titok:\t" + secrets.keySet().toArray()[0]);

		clientThread = new Thread(new ClientRunnable(this, maxTimeout, minTimeout));
		serverThread = new Thread(new ServerRunnable(this, maxTimeout, minTimeout));

	}

	public void startThreads() {
		clientThread.start();
		serverThread.start();
		try {
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException ex) {
			System.err.println(String.format("%s. ügynökséghez tartozó %s. kódú ügynök letartóztatva", agency.getCode(), agentCode));
			System.exit(4);
		}
	}

	private String getFileName() {
		return String.format("agent%d-%d.txt", agency.getCode(), agentCode);
	}

	private void readInformation() {
		try {
			System.out.println("Adatok beolvasás a " + getFileName() + " fájlból!");
			Scanner sc = new Scanner(new File(getFileName()));

			this.names = Arrays.asList(sc.nextLine().split(" "));
			this.secrets.put(sc.nextLine(), true);

		} catch (FileNotFoundException e) {
			System.err.println(String.format("Nem létező fájl (%s)", getFileName()));
			System.exit(5);
		}
	}

	public String getRndName() {
		return this.names.get(this.rnd.nextInt(this.names.size()));
	}

	public String getRndSecret(boolean checkValue) {
		List<String> secretsList = new ArrayList<>();
		secretsList.addAll(secrets.keySet());
		if (hasAvailableSecrets()) {
			if (checkValue) {
				return secretsList.get(this.rnd.nextInt(secretsList.size()));
			} else {
				String tempSecret = secretsList.get(this.rnd.nextInt(secretsList.size()));
				while (secrets.get(tempSecret) == false) {
					tempSecret = secretsList.get(this.rnd.nextInt(secretsList.size()));
				}
				secrets.put(tempSecret, false);
				return tempSecret;
			}
		} else {
			stopThreads();
			return "";
		}
	}

	private void stopThreads() {
		this.clientThread.interrupt();
		this.serverThread.interrupt();
	}

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
}