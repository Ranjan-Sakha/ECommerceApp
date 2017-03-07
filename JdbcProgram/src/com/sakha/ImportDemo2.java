package com.sakha;
import java.sql.*;
public class ImportDemo2 {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
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
