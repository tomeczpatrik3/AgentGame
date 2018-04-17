package agent;

import java.util.ArrayList;
import java.util.List;

public class AgentMain {
	private static List<Agent> agencyAAgents = new ArrayList<>();
	private static List<Agent> agencyBAgents = new ArrayList<>();

	public static void main(String[] args) {
		try {
			int aAgents = Integer.parseInt(args[0]), bAgents = Integer
					.parseInt(args[1]);
			System.out.println("1. ügynökség ügynökeinek száma: " + aAgents);
			System.out.println("2. ügynökség ügynökeinek száma: " + bAgents);

			int t1 = Integer.parseInt(args[2]), t2 = Integer.parseInt(args[3]);
			System.out.println("Várakozás hosszának alsó korlátja: " + t1);
			System.out.println("Várakozás hosszának felső korlátja: " + t2);

			System.out.println("Az 1. ügynökség ügynökeinek létrehozása:\n");
			for (int i = 0; i < aAgents; i++) {
				agencyAAgents.add(new Agent(1, i + 1, t1, t2));
			}

			System.out.println("A 2. ügynökség ügynökeinek létrehozása:\n");
			for (int i = 0; i < bAgents; i++) {
				agencyBAgents.add(new Agent(2, i + 1, t1, t2));
			}

		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Hibás paraméterezés!");
			System.exit(1);
		} catch (NumberFormatException ex) {
			System.err.println("Hibás formátum!");
			System.exit(2);
		}
	}
}
