package com.sakha;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

public class NameSortDemo {
   
	static TreeMap<String,Integer> tm = new TreeMap<String,Integer>();
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("Select * from Student_info order by sname");
        //TreeSet<String> ts= new TreeSet<String>();
        while(rs.next())
        {
        	//int id=rs.getInt(1);
        	//String name=rs.getString(2);
        	//sortMeth(id,name);
        	//ts.add(rs.getString(2));
        	System.out.println(rs.getInt(1)+" "+rs.getString(2));
        }
       // display();
        rs.close();
        stmt.close();
        con.close();

	}
	
	public static void sortMeth(int id,String name)
	{
		
		tm.put(name, id);
		
	}
   
	public static void display()
	{
		System.out.println(tm);
	}
}
