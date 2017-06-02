package test;

import httpServer.booter;

public class testflink {
	public static void main(String[] args) {
		booter booter = new booter();
		System.out.println("Grapeflink!");
		try {
			System.setProperty("AppName", "Grapeflink");
			booter.start(6003);
		} catch (Exception e) {

		}
	}
}
