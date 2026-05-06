package ca.ianmillican.domain.pojo.transcript;

import java.util.List;

public class Term {
	
	private final String location;
	private final String term;
	private final int year;
	private final List<CourseAttempt> courses;
	private final String degree;
	
	public Term(String term, int year, String degree, List<CourseAttempt> courses, String location) {
		this.term = term;
		this.year = year;
		this.courses = courses;
		this.degree = degree;
		this.location = location;
	}
	
	public String getTerm() {
		return term;
	}
	
	public int getYear() {
		return year;
	}
	
	public List<CourseAttempt> getCourses(String degrere) {
		if(this.degree.equals(degrere)) {
			return List.copyOf(courses);
		} else {
			return List.copyOf(courses).stream().filter(ca -> ca.getTransfers().contains(degrere)).toList();
		}
	}
	
	public void addCourse(CourseAttempt c) {
		courses.add(c);
	}
	
	public String getDegree() {
		return degree;
	}
	
	public String getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return year+"/"+term + "\t" + degree + "\t" + location + "\n" + 
			printCourses();
	}
	
	private String printCourses() {
		StringBuilder builder = new StringBuilder();
		for(CourseAttempt c: courses) {
			builder.append(c + "\n");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
}