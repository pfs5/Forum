package hr.fer.objects;

import java.util.Random;

public class Password {

	public String code(String input) {
		String output = "";
		Random random = new Random();

		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			int RepNum;
			if ((int) currentChar > 99)
				RepNum = ((int) currentChar) / 100;
			else
				RepNum = ((int) currentChar) / 10;
			String addition = "";
			for (int j = 0; j < RepNum; j++) {
				addition = addition + "" + random.nextInt(10);
			}
			output = output + "" + (int) currentChar + "" + addition;
		}
		return output;
	}

	public String decode(String input) {
		String output = "";

		for (int i = 0; i < input.length();) {
			char no1 = input.charAt(i);
			char no2 = input.charAt(i + 1);

			if (no1 == '1') {
				char no3 = input.charAt(i + 2);
				String current = no1 + "" + no2 + "" + no3;
				int ascii = Integer.parseInt(current);
				char decodedChar = (char) ascii;
				output = output + "" + decodedChar;
				i += (ascii / 100) + 3;
			} else {
				String current = no1 + "" + no2;
				int ascii = Integer.parseInt(current);
				char decodedChar = (char) ascii;
				output = output + "" + decodedChar;
				i += (ascii / 10) + 2;
			}

		}

		return output;
	}
}