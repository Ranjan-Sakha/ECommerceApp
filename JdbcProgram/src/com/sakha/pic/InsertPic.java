package com.sakha.pic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertPic {
  
	int a=86;
	public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException {
		// TODO Auto-generated method stub
		/*Class.forName("com.mysql.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost/user","root","root");
        PreparedStatement pstmt=con.prepareStatement("insert into comlogo values(?,?,?)");
        pstmt.setInt(1,101);
        pstmt.setString(2,"SakhaTech");
        FileInputStream fis= new FileInputStream(new File("/home/ranjan/Downloads/index.jpeg"));
        pstmt.setBlob(3, fis);//either use setBlob or setBinaryStream
        int i=pstmt.executeUpdate();*/
		InsertPic ip= new InsertPic();
		ip.meth(9009);
	}
	public  void meth(int a) {
		// TODO Auto-generated method stub
		System.out.println(this.a);

	}

}
