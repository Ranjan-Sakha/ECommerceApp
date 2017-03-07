package com.sakha;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBMetaDataDemo {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
		DatabaseMetaData db=con.getMetaData();
		System.out.println(db.getDatabaseProductName());
		System.out.println(db.getDatabaseProductVersion());
		System.out.println(db.getSQLKeywords());
		System.out.println(db.getURL());

	}

}
