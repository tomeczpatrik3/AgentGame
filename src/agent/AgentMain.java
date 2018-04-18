package agent;

import java.util.ArrayList;
import java.util.List;

public class AgentMain {

	public static void main(String[] args) {
		try {
			int aAgents = Integer.parseInt(args[0]), bAgents = Integer
					.parseInt(args[1]);
			System.out.println("1. ügynökség ügynökeinek száma: " + aAgents);
			System.out.println("2. ügynökség ügynökeinek száma: " + bAgents);

			int t1 = Integer.parseInt(args[2]), t2 = Integer.parseInt(args[3]);
			System.out.println("Várakozás hosszának alsó korlátja: " + t1);
			System.out.println("Várakozás hosszának felső korlátja: " + t2);
			
			Agency aAgency = new Agency("A", 1, aAgents);
			Agency bAgency = new Agency("B", 2, bAgents);


		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Hibás paraméterezés!");
			System.exit(1);
		} catch (NumberFormatException ex) {
			System.err.println("Hibás formátum!");
			System.exit(2);
		}
	}
}
