package com.sakha;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportDemo {
    
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
	Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
    Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("Select * from SakhaUser");
    while(rs.next())
    {
    	System.out.println(rs.getString(1)+" "+rs.getString(2));
    }
    rs.close();
    stmt.close();
    con.close();
}

}


