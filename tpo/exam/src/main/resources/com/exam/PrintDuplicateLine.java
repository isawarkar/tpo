package com.exam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PrintDuplicateLine {

	public static void main(String[] args) {
		stripDuplicatesFromFile("D:/TPO_WORKSPACE/tpo/tpo/FB/resources/com/fb/fbresource.properties");
		stripDuplicatesFromFile("D:/TPO_WORKSPACE/tpo/tpo/FB/resources/com/fb/fbresource_in.properties");
		stripDuplicatesFromFile("D:/TPO_WORKSPACE/tpo/tpo/FB/resources/com/fb/fbmessages.properties");
		stripDuplicatesFromFile("D:/TPO_WORKSPACE/tpo/tpo/FB/resources/com/fb/fbmessages_in.properties");
			
		createNewFile1("D:/TPO_WORKSPACE/tpo/tpo/FB/resources/com/fb/fbmessages.properties","D:/fbmessages.txt");
		createNewFile1("D:/TPO_WORKSPACE/tpo/tpo/FB/resources/com/fb/fbresource.properties","D:/fbresource.txt");
		
	}

	public static void stripDuplicatesFromFile(String filename) {
		try {
			System.out.println(filename);
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			Set<String> lines = new HashSet<String>(10000); // maybe should be
															// bigger
			String line;
			while ((line = reader.readLine()) != null) {
				String ar[] = line.split("=");
				if (ar.length > 1)
					if (lines.contains(ar[1])) {
						System.out.println(ar[1]);
					} else {
						lines.add(ar[1]);
					}
			}
			reader.close();
			/*
			 * BufferedWriter writer = new BufferedWriter(new
			 * FileWriter(filename)); for (String unique : lines) {
			 * writer.write(unique); writer.newLine(); } writer.close();
			 */
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void createNewFile1(String filename,String fileLoc) {
		File file = new File(fileLoc);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			BufferedReader reader = new BufferedReader(new FileReader(filename));

			String line;
			while ((line = reader.readLine()) != null) {
				String ar[] = line.split("=");
				if (ar.length > 1) {
					writer.write(ar[0]);
					writer.newLine();
				}
			}
			reader.close();
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	public static void createNewFile2(String filename) {
		File file = new File("D:\\in.txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			BufferedReader reader = new BufferedReader(new FileReader(filename));

			String line;
			while ((line = reader.readLine()) != null) {
				String ar[] = line.split("=");
				if (ar.length > 1) {
					writer.write(ar[0]);
					writer.newLine();
				}
			}
			reader.close();
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}
