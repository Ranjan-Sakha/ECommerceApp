package com.sakha.login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnector {

	// Define all the private variable
	private Connection con = null;
	private static String cName = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost/user";
	private static String uName = "root";
	private static String pwd = "root";
	ArrayList<String> al1 = new ArrayList<String>();
	ArrayList<String> al2 = new ArrayList<String>();

	// Creating the connection with DataBase and storing the value in ArrayList
	public Connection connector() {
		try {
			Class.forName(cName);
			Connection con = DriverManager.getConnection(url, uName, pwd);
			Statement stmt1 = con.createStatement();
			ResultSet rs = stmt1.executeQuery("Select * from New_Student");
			while (rs.next()) {
				al1.add(rs.getString(1));
				al2.add(rs.getString(2));
			}
			return con;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return con;

	}

	// Validating the existing user
	public String loginValid(String sname, String psd) {
		for (int i = 0; i < al1.size(); i++) {
			if (al1.get(i).equalsIgnoreCase(sname) && al2.get(i).equals(psd))
				return "Valid User";
		}
		return "InValid User";
	}

	// Validating the New User Data.If Correct then Store them in DataBase.
	public String registrationValid(String name, String psd, Connection con) {
		int count = 0;

		// Checking with previously stored data.If matched Increment the count
		// value.
		for (int i = 0; i < al1.size(); i++) {
			if (al1.get(i).equalsIgnoreCase(name)) {
				count++;
				break;
			}
		}

		// If Everything is ok then stored into DataBase
		if (count == 0) {
			try {
				PreparedStatement pstmt = con.prepareStatement("insert into New_Student values(?,?)");
				pstmt.setString(1, name);
				pstmt.setString(2, psd);
				pstmt.executeUpdate();
				return "Registration Successful";
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
        
		//else returned error message	
		} else
			return "User Already Exists";
		return "Some Internal Problem ! Plz Try Again";
	}

}
