package com.sakha.login;

import java.util.Scanner;

public class LoginGui {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Plz Follow Instruction");
		System.out.println("---------------------------");
		Scanner scan = new Scanner(System.in);
		boolean bool=true;
		
		//Asking Every time until User Says No
		while(bool){
		System.out.println("For Login enter(L), For New Registration enter (R), For Exit enter(E)");
		
		String str = scan.next();
		
		//Check whether User want to LOgin,Register or Exit.
		if (!str.equalsIgnoreCase("L") && !str.equalsIgnoreCase("R"))
			System.out.println("Thank You! You Are Sign Out");
		else {

			System.out.println("Enter Your Name");
			String name = scan.next();
			System.out.println("Enter Your Password");
			String psd = scan.next();
			LoginRegistration lr = new LoginRegistration();

			if (str.equalsIgnoreCase("R")) {
				lr.registration(name, psd);
			}
			if (str.equalsIgnoreCase("L")) {
				lr.login(name, psd);

			}
		}
		System.out.println("Do you want to continue.(Y/N)");
		String str1=scan.next();
		if(str1.equalsIgnoreCase("y"))
			bool=true;
		else
			bool=false;
		}
		System.out.println("We will meet soon......BYE");
		scan.close();
	}

}
