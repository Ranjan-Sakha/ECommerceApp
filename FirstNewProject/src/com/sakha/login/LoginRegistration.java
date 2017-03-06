package com.sakha.login;

public class LoginRegistration {

	public void login(String name, String psd) {
		DBConnector db = new DBConnector();
		db.connector();
		System.out.println(db.loginValid(name, psd));
	}

	public void registration(String name, String psd) {
		DBConnector db = new DBConnector();

		System.out.println(db.registrationValid(name, psd, db.connector()));
	}

}
