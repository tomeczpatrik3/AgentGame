package agent;

public class Agency {
	private String name;
	private int code;
	private int agents;
	
	public Agency(String name, int code, int agents) {
		this.name = name;
		this.code = code;
		this.agents = agents;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAgents() {
		return agents;
	}

	public void setAgents(int agents) {
		this.agents = agents;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
