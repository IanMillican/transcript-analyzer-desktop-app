package ca.ianmillican.domain.pojo;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

public class Student {

	private final String name;
	private final int studentID;
	private final MonthDay DOB;
	
	public Student(String name, int studentID, MonthDay DOB) {
		this.name = name;
		this.studentID = studentID;
		this.DOB = DOB;
	}
	
	public String getName() {
		return name;
	}
	
	public int getStudentID() {
		return studentID;
	}
	
	public MonthDay getDOB() {
		return DOB;
	}
	
	@Override
	public String toString() {
		DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd");
		return "Name: " + name + "\n" +
				"Student ID: " + studentID + "\n" +
				"Date of Birth: " + DOB.format(f);
	}
	
}
