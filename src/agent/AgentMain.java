package agent;

import java.util.ArrayList;
import java.util.List;

import agent.config.Constants;
import agent.model.Agency;
import agent.model.Agent;

public class AgentMain {
	
	public static void main(String[] args) {
		List<Thread> agentThreads = new ArrayList<>();
		
		try {
			//Ügynökségekhez tartozó ügynökök számai:
			int aAgents = Integer.parseInt(args[0]), bAgents = Integer.parseInt(args[1]);
			System.out.println("1. ügynökség ügynökeinek száma: " + aAgents);
			System.out.println("2. ügynökség ügynökeinek száma: " + bAgents);

			//Timeout értékek:
			int t1 = Integer.parseInt(args[2]), t2 = Integer.parseInt(args[3]);
			Constants.MIN_TIMEOUT = t1;
			Constants.MAX_TIMEOUT = t2;
			System.out.println("\nVárakozási konstansok beállítva:");
			System.out.println("\tAlsó korlát: " + Constants.MIN_TIMEOUT);
			System.out.println("\tFelső korlát: " + Constants.MAX_TIMEOUT);

			//Ügynökségek létrehozása, inicializálása:
			Agency aAgency = initialzeAgency("A", 1, aAgents);
			Agency bAgency = initialzeAgency("B", 2, bAgents);

			//Threadek létrehozása és tárolása egy listában:
			agentThreads.addAll(createThreads(aAgency.getAgents()));
			agentThreads.addAll(createThreads(bAgency.getAgents()));
			
			//Ügynökök indítása:
			for (Thread thread: agentThreads) {
				thread.start();
			}
			
			//Amíg tart a játék várunk:
			while (
					aAgency.hasAvailableAgent() 
					&& bAgency.hasAvailableAgent()
					&& !aAgency.hasAllTheSecrets(bAgency.getSecrets()) 
					&& !bAgency.hasAllTheSecrets(aAgency.getSecrets())
			) {
				Thread.sleep(1000);
			}
			
			//Hogy minden szál szabályosan álljon le:
			aAgency.setGameOver(true);
			bAgency.setGameOver(true);
			
			//Információk logolása:
			aAgency.log();
			bAgency.log();
			
			//Győztes kihírdetése:
			announceTheWinner(aAgency, bAgency);
			
			System.out.println("Játék vége!");
			
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Hibás paraméterezés!");
			System.exit(1);
		} catch (NumberFormatException ex) {
			System.err.println("Hibás formátum!");
			System.exit(2);
		} catch (InterruptedException e) {
			System.err.println("Alvás megszakítva...");
		}
	}

	/**
	 * A függvény, amely létrehoz az adott ügynökhöz egy szálat:
	 * @param agent
	 * @return
	 */
	public static Thread createAgentThread(Agent agent) {
		return new Thread(() -> {
			agent.startThreads();
		});
	}
	
	/**
	 * Az ügynökségek létrehozásáért és az ügynökök felvételéért felelős függvény:
	 * @param name
	 * @param code
	 * @param agents
	 * @return
	 */
	private static Agency initialzeAgency(String name, int code, int agents) {
		Agency agency = new Agency(name, code);
		System.out.println(String.format("\n%s ügynökség létrehozva!", name));
		
		//Az ügynökség ügynökeinek létrehozása:
		for (int i = 0; i < agents; i++) {
			agency.addAgent(new Agent(agency, i + 1));
		}
		System.out.println(String.format("A(z) '%s' ügynökséghez tartozó ügynökök felvéve. Ügynökök száma: %d!\n", name, agents));
		
		return agency;
	}
	
	/**
	 * Adott ügynök listázhoz Thread lista létrehozása:
	 * @param agents
	 * @return
	 */
	private static List<Thread> createThreads(List<Agent> agents) {
		List<Thread> threads = new ArrayList<>();
		
		for (Agent agent: agents) 
			threads.add(createAgentThread(agent));
		
		return threads;
	}
	
	private static void announceTheWinner(Agency aAgency, Agency bAgency) {
		if (aAgency.hasAllTheSecrets(bAgency.getSecrets())) {
			System.out.println("A ügynökség nyert!");
		} else if (bAgency.hasAllTheSecrets(aAgency.getSecrets())) {
			System.out.println("B ügynökség nyert!");
		} else if (aAgency.hasAvailableAgent()) {
			System.out.println("A ügynökség nyert!");
		} else if (bAgency.hasAvailableAgent()) {
			System.out.println("B ügynökség nyert!");
		}
	}
}
