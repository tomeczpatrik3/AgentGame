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

	public Agent(Agency agency, int agentCode) {
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
		System.out.println("Titok:\t" + secrets.keySet().toArray()[0]);

		clientThread = new Thread(new ClientRunnable(this));
		serverThread = new Thread(new ServerRunnable(this));

	}

	public void startClientThread() {
		clientThread.start();
		try {
			clientThread.join();
		} catch (InterruptedException ex) {
			System.err.println(String.format("%s. ügynökséghez tartozó %s. kódú ügynök letartóztatva", agency.getCode(), agentCode));
			System.exit(4);
		}
	}

	public void startServerThread() {
		serverThread.start();
		try {
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
		System.out.println("Adatok beolvasás a " + getFileName() + " fájlból!");
		try (Scanner sc = new Scanner(new File(getFileName()));) {

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

	public void printSecrets() {
		for (String secret : secrets.keySet()) {
			System.out.printf("%s ", secret);
		}
		System.out.println();
	}

	public void stopServerThread() {
		this.serverThread.interrupt();
	}

	public void stopClientThread() {
		this.clientThread.interrupt();
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

	public boolean isHasAvailableSecrets() {
		return hasAvailableSecrets;
	}

	public void setHasAvailableSecrets(boolean hasAvailableSecrets) {
		this.hasAvailableSecrets = hasAvailableSecrets;
	}
}