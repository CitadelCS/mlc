package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import mlc.procedures.Algorithm;

public class TestAlgorithm
{
	public static void main(String[] args) throws Exception
	{
		PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\Jacob\\eclipse-workspace\\Minimum_Length_Corridor\\src\\test\\log.txt"));
		System.setOut(out);
		File source = new File(args[0]);
		Algorithm a1 = new Algorithm(source);
		a1.run();
	}
	/*
	 * Variables used in the Algorithm are listed below.
	 */
	// Graph[] graphs;
	// int numOfGraphs = 0;
	// boolean temp = true;

	/*
	 * System.out.println("How many graphs will be analyzed?"); Scanner sc = new
	 * Scanner(System.in); while(temp) { try { numOfGraphs = sc.nextInt(); temp =
	 * false; } catch(InputMismatchException e){ throw new
	 * InputMismatchException("An integer was not entered."); } } graphs = new
	 * Graph[numOfGraphs]; for(int i = 0; i < graphs.length; i++) { Runnable r = new
	 * Graph(); Thread t = new Thread(r); }
	 */

}
