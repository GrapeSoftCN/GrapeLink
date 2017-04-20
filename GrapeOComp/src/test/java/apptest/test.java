package apptest;

import httpServer.booter;

public class test {
	public static void main(String[] args) {
		booter booter = new booter();
		try {
			System.out.println("GrapeC!");
			System.setProperty("AppName", "GrapeC");
			booter.start(1003);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
