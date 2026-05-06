package ca.ianmillican.domain.pojo.transcript;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ianmillican.domain.pojo.Student;

public class Transcript {
	
	private final Student student;
	private final List<Term> terms;
	private final LocalDate DOI;
	
	public Transcript(List<Term> terms, Student student, LocalDate DOI) {
		this.terms = terms;
		this.student = student;
		this.DOI = DOI;
	}
	
	public List<Term> getTerms() {
		return List.copyOf(terms);
	}
	
	public String getStudentName() {
		return student.getName();
	}
	
	public int getStudentID() {
		return student.getStudentID();
	}
	
	public LocalDate getDOI() {
		return DOI;
	}
	
	// @Override
	// public String toString() {
	// 	return student + "\n" +
	// 			printTerms();
	// }
	
	// private String printTerms() {
	// 	StringBuilder builder = new StringBuilder();
	// 	for(Term t : terms) {
	// 		builder.append(t + "\n");
	// 	}
	// 	builder.deleteCharAt(builder.length() - 1);
	// 	return builder.toString();
	// }
	
	public List<CourseAttempt> getCourses(String degree) {
		List<CourseAttempt> result = new ArrayList<>();
		for(Term t : terms) {
			result.addAll(t.getCourses(degree));
		}
		return result;
	}

	@Override
	public String toString() {
		return this.prettyPrint();
	}

	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append("=== TRANSCRIPT ===\n");
		sb.append("Student:    ").append(student.getName()).append("\n");
		sb.append("Student ID: ").append(student.getStudentID()).append("\n");
		sb.append("Date:       ").append(DOI).append("\n");
		sb.append("\n");

		for (Term t : terms) {
			sb.append("--- ").append(t.getTerm()).append(" ").append(t.getYear())
			.append(" | ").append(t.getDegree())
			.append(" | ").append(t.getLocation())
			.append(" ---\n");

			for (CourseAttempt ca : t.getCourses(t.getDegree())) {
				sb.append(String.format("  %-10s %-40s %-5s %2d ch",
					ca.getCourseCode(),
					ca.getName(),
					ca.getGrade(),
					ca.getCreditHours()));
				if (!ca.getTransfers().isEmpty()) {
					sb.append("  [transfers: ").append(String.join(", ", ca.getTransfers())).append("]");
				}
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
