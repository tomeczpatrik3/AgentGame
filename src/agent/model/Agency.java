package agent.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Agency {
	// Beégetett információ, 2db ügynökségünk van:
	public static int agencyCount = 2;

	private String name;
	private int code;
	
	public boolean isGameOver;

	private List<Agent> agents;
	// Az összes titok, amit az ügynökök tárolnak (ismétlődések nélkül):
	private List<String> secrets;

	public Agency(String name, int code) {
		this.name = name;
		this.code = code;
		
		isGameOver = false;

		this.agents = new ArrayList<>();
		this.secrets = new ArrayList<>();
	}

	/**
	 * Van-e még olyan ügynök aki nem került börtönbe?
	 * 
	 * @return
	 */
	public boolean hasAvailableAgent() {
		boolean hasAvailableAgent = false;
		for (Agent agent : agents)
			hasAvailableAgent |= !agent.isJailed();
		return hasAvailableAgent;
	}

	/**
	 * Van-e olyan ügynök aki az ellenfél összes titkát ismeri?
	 * 
	 * @param secrets
	 *            Az ellenfél összes titka
	 * @return
	 */
	public boolean hasAllTheSecrets(List<String> secrets) {
		boolean l = false;
		for (Agent agent : agents) {
			int counter = 0;
			for (String secret : secrets) {
				if (!agent.getSecrets().keySet().contains(secret))
					break;
				else
					counter++;
			}
			if (counter == secrets.size()){
				l = true;
				break;
			}
		}
		
		return l;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void addAgent(Agent agent) {
		agents.add(agent);
	}

	public int getAgentsNumber() {
		return agents.size();
	}

	public List<String> getSecrets() {
		return this.secrets;
	}

	public void addScerets(Set<String> secrets) {
		for (String secret : secrets) {
			if (!this.secrets.contains(secret))
				this.secrets.add(secret);
		}
	}
	
	public void log() {
		System.out.println("-----------------------------------");
		System.out.println(String.format("%s AGENCY:", name));
		
		System.out.println(String.format("Ügynökök száma: %d", this.getAgentsNumber()));
		
		System.out.println("Az összes titok (sajátok):");
		for(String secret: secrets)
			System.out.println(String.format("-- %s", secret));
		
		System.out.println("Az ügynökök adatai:");
		for (Agent agent: agents) {
			System.out.println(String.format("Kód: %s", agent.getAgentCode()));
			System.out.println("Nevek:");
			for (String name: agent.getNames())
				System.out.println(String.format("-- %s", name));
			System.out.println("Titkok:");
			for (String secret: agent.getSecrets().keySet())
				System.out.println(String.format("-- %s", secret));
		}
		
		
	}
	
}
