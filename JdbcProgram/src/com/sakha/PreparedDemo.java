package com.sakha;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class PreparedDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
        PreparedStatement pstmt=con.prepareStatement("insert into Student_info values(?,?,?)");
        

	}
		catch(Exception e)
		{
			System.out.println(e);
		}
		}

}
