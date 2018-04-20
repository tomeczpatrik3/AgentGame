package agent;

import agent.config.Constants;
import agent.model.Agency;
import agent.model.Agent;

public class AgentMain {

	public static void main(String[] args) {
		try {
			int aAgents = Integer.parseInt(args[0]), bAgents = Integer.parseInt(args[1]);
			System.out.println("1. ügynökség ügynökeinek száma: " + aAgents);
			System.out.println("2. ügynökség ügynökeinek száma: " + bAgents);

			int t1 = Integer.parseInt(args[2]), t2 = Integer.parseInt(args[3]);
			Constants.MIN_TIMEOUT = t1;
			Constants.MAX_TIMEOUT = t2;
			System.out.println("\nVárakozási konstansok beállítva:");
			System.out.println("\tAlsó korlát: " + Constants.MIN_TIMEOUT);
			System.out.println("\tFelső korlát: " + Constants.MAX_TIMEOUT);

			Agency aAgency = new Agency("A", 1);
			System.out.println("\nA ügynökség létrehozva!");
			Agency bAgency = new Agency("B", 2);
			System.out.println("B ügynökség létrehozva!");

			Agent agent;
			Thread thread;

			for (int i = 0; i < aAgents; i++) {
				agent = new Agent(aAgency, i + 1);
				aAgency.addAgent(agent);
				agent.startServerThread();
				// thread = createAgentThread(agent);
				// thread.start();
			}
			System.out.println(String.format("A(z) '%s' ügynökséghez tartozó ügynökök felvéve. Ügynökök száma: %d!\n", aAgency.getName(), aAgency.getAgentsNumber()));

			for (int i = 0; i < bAgents; i++) {
				agent = new Agent(bAgency, i + 1);
				bAgency.addAgent(agent);
				agent.startClientThread();
				// thread = createAgentThread(agent);
				// thread.start();
			}
			System.out.println(String.format("A(z) '%s' ügynökséghez tartozó ügynökök felvéve. Ügynökök száma: %d!\n", bAgency.getName(), bAgency.getAgentsNumber()));

		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Hibás paraméterezés!");
			System.exit(1);
		} catch (NumberFormatException ex) {
			System.err.println("Hibás formátum!");
			System.exit(2);
		}
	}

	public static Thread createAgentThread(Agent agent) {
		return new Thread(() -> {
			agent.startClientThread();
			agent.startServerThread();
		});
	}
}
