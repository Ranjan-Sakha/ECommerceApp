package com.sakha;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class CallableDemo {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
		CallableStatement cs= con.prepareCall("call getname(?,?)");
		cs.setInt(1,100);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);
		cs.executeQuery();
		String sname=cs.getString(2);
		System.out.println(sname);
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery("Show tables");
		ResultSetMetaData rsmd=rs.getMetaData();
		while(rs.next())
		{
			System.out.println(rs.getString(1));
		}
		
		
	     String[] types = { "TABLE" };

	     rs = con.getMetaData().getTables("user", null, "%", types);

	     String tableName = "";

	     while (rs.next()) {

	       tableName = rs.getString(3);

	       System.out.println("Table Name = " + tableName);

	     }
	     
		//System.out.println("Dis "+rsmd.getT);
	     Scanner s = new Scanner(System.in);
	     System.out.println("Enter Table Name:");
	     String Tname=s.nextLine();
	     ResultSet rs1=st.executeQuery("Select * from "+Tname);	
	     ResultSetMetaData rsmd1=rs1.getMetaData();
	     int i=rsmd1.getColumnCount();
	     for(int k=1;k<=i;k++)
	     {
	    	 System.out.println(rsmd1.getColumnName(k)+"/t");
	     }
	     while(rs1.next())
	     {
	         System.out.println(rs1.getString());
		

	}

}
