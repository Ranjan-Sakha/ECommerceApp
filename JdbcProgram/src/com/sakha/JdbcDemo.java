package com.sakha;

//import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JdbcDemo {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//Class.forName("com.mysql.jdbc.Driver");
		//Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
        //Statement stmt=con.createStatement();
        Scanner s = new Scanner(System.in);
        //ResultSet rs=stmt.executeQuery("Select * from STUDENT");
        while(s.hasNext())
        {
        	System.out.println(s.next());
        }
        /*while(rs.next())
        {
        	System.out.println(rs.getInt(1)+" "+rs.getString(2));
        }
        rs.close();
        stmt.close();
        con.close();*/
	}

}
