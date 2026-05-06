package ca.ianmillican.domain.pojo.degree;

import ca.ianmillican.domain.pojo.Course;

public class CourseReq extends Course<CourseReq> {

	public CourseReq(String subject, int num, String name, int creditHours, boolean coop) {
		super(subject, num, name, creditHours, coop);
	}

	@Override
	public String toString() {
		return super.getCourseCode()+": "+super.getName();
	}
	
}
