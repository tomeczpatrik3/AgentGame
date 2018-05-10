package agent.model;

import java.util.ArrayList;
import java.util.List;

public class Agency {
	public static int agencyCount = 0;
	
	private String name;
	private int code;
	private List<Agent> agents;
	
	public Agency(String name, int code) {
		agencyCount++;
		
		this.name = name;
		this.code = code;
		this.agents = new ArrayList<>();
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
}
