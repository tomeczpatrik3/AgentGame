package agent;

import java.io.File;
import java.io.FileNotFoundException;
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

	private Random rnd;

	// Adott ügynök adatai:
	private int agencyCode;
	private int agentCode;
	private List<String> names;
	private Map<String, Integer> badTips;
	private String secret;

	// Szálak:
	private Thread clientThread;
	private Thread serverThread;

	public Agent(int agencyCode, int agentCode, int minTimeout, int maxTimeout) {
		rnd = new Random();
		badTips = new HashMap<>();

		this.agencyCode = agencyCode;
		this.agentCode = agentCode;

		System.out.println(String.format(
				"%d. ügynökséghez tartozó %d. ügynök adatai:", agencyCode,
				agentCode));

		readInformation();

		System.out.println("Álnevek: ");
		for (int i = 0; i < names.size(); i++) {
			System.out.println("\t" + names.get(i));
		}
		System.out.println("Titok:\t" + secret);

		clientThread = new Thread(new ClientRunnable(this, maxTimeout,
				minTimeout));
		serverThread = new Thread(new ServerRunnable(this, maxTimeout,
				minTimeout));

	}

	public void startThreads() {
		clientThread.start();
		serverThread.start();
		try {
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException ex) {
			System.err.println("Thread interrupted");
			System.exit(4);
		}
	}

	private String getFileName() {
		return String.format("agent%d-%d.txt", agencyCode, agentCode);
	}

	private void readInformation() {
		try {
			System.out.println("Adatok beolvasás a " + getFileName()
					+ " fájlból!");
			Scanner sc = new Scanner(new File(getFileName()));

			this.names = Arrays.asList(sc.nextLine().split(" "));
			this.secret = sc.nextLine();

		} catch (FileNotFoundException e) {
			System.err.println(String.format("Nem létező fájl (%s)",
					getFileName()));
			System.exit(5);
		}
	}

	public String getRndName() {
		return this.names.get(this.rnd.nextInt(this.names.size()));
	}

	public int getAgencyCode() {
		return agencyCode;
	}

	public void setAgencyCode(int agencyCode) {
		this.agencyCode = agencyCode;
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

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Map<String, Integer> getBadTips() {
		return badTips;
	}

	public void setBadTips(Map<String, Integer> badTips) {
		this.badTips = badTips;
	}
}