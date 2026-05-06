package ca.ianmillican.domain.pojo.transcript;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ca.ianmillican.domain.pojo.Course;
import ca.ianmillican.domain.pojo.degree.CourseReq;

public class CourseAttempt extends Course<CourseAttempt> {
    
	private final String grade;
	private final List<String> transfers;
	private final static Map<String, Float> GRADE_POINTS = Map.of(
			"A+", 4.3f,
			"A", 4.0f,
			"A-", 3.7f,
			"B+", 3.3f,
			"B", 3.0f,
			"B-", 2.7f,
			"C+", 2.3f,
			"C", 2.0f,
			"D", 1.0f,
			"F", 0.0f
			);
	private final static List<String> PRECEDENCE = List.of(
			"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F", "W", "WF", "INC", "CR", "NCR", "N/A"
			);
	public static final Comparator<String> GRADE_COMPARATOR = (g1, g2) -> {
		
		int ind1 = PRECEDENCE.indexOf(g1);
		int ind2 = PRECEDENCE.indexOf(g2);
		
		if(ind1 == -1 && ind2 == -1) {
			return g1.compareTo(g2);
		} else if (ind1 == -1) {
			return 1;
		} else if (ind2 == -1) {
			return -1;
		} else {
			return Integer.compare(ind1, ind2);
		}
	};

	public CourseAttempt(CourseAttempt other) {
		if(other == null) {
			throw new IllegalArgumentException("Cannot copy null CourseAttempt");
		}
		super(other.getSubject(), other.getCourseNum(), other.getName(), other.getCreditHours(), other.isCoop());
		this.grade = other.getGrade();
		this.transfers = new ArrayList<>(other.getTransfers());
	}

	public CourseAttempt(String subject, int num, String name, int creditHours, String grade, List<String> transfers) {
		super(subject, num, name, creditHours, num==0);
		this.grade = grade;
		this.transfers = new ArrayList<>((transfers));
	}
	
	public CourseAttempt(String grade, List<String> transfers, Course<?> course) {
		super(course.getSubject(), course.getCourseNum(), course.getName(), course.getCreditHours(), course.isCoop());
		this.grade = grade;
		this.transfers = transfers;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public List<String> getTransfers() {
		return transfers;
	}

	public float getGradePoints() {
		return GRADE_POINTS.getOrDefault(grade, 0.0f) * super.getCreditHours();
	}
	
	@Override
	public String toString() {
		String initialString = super.getCourseCode() + "\t" + super.getName() + "\t" + grade + "\t" + String.format("%.2f", Double.valueOf(super.getCreditHours())) + "\t" + String.format("%.2f", pointsConverter(super.getCreditHours())) + "\t";
		StringBuilder builder = new StringBuilder(initialString);
		if(!transfers.isEmpty()) {
			for(String t : transfers) {
				builder.append(t + ", ");
			}
			builder.deleteCharAt(builder.length()-1);
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}
	
	private double pointsConverter(int creditHours) {
		switch(grade) {
			case "A+":
				return 4.3*creditHours;
			case "A":
				return 4*creditHours;
			case "A-":
				return 3.7*creditHours;
			case "B+":
				return 3.3*creditHours;
			case "B":
				return 3*creditHours;
			case "B-":
				return 2.7*creditHours;
			case "C+":
				return 2.3*creditHours;
			case "C":
				return 2*creditHours;
			case "D":
				return 1*creditHours;
			default:
				return 0;
		}
	}

	@Override
	public int compareTo(CourseAttempt o) {
		if(o == null) {
			return -1;
		} else if (this.equals(o)) {
			return 0;
		} else if (PRECEDENCE.indexOf(grade) < PRECEDENCE.indexOf(o.getGrade())) {
			return -1;
		} else if (PRECEDENCE.indexOf(grade) == PRECEDENCE.indexOf(o.getGrade())) {
			return 0;
		} else {
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof CourseAttempt c) {
			return this.getCourseCode().equals(c.getCourseCode());
		} else if (o instanceof CourseReq c) {
			return this.getCourseCode().equals(c.getCourseCode());
		} else {
			return false;
		}
	}

}
