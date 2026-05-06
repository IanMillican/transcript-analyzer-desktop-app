package ca.ianmillican.dataaccess.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ianmillican.domain.interfaces.Parser;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;

public class CourseParser implements Parser<CourseAttempt> {

	@Override
	public CourseAttempt parse(String filePath) throws Exception {
		// Not implemented for single file parsing
		throw new UnsupportedOperationException("Single file parsing is not supported for CourseParser.");
	}

	@Override
	public List<CourseAttempt> parse(List<String> t) throws Exception {
		List<CourseAttempt> courses = new ArrayList<>();
		Pattern p = Pattern.compile(
			    "^([A-Z]{2,10})\\*([A-Z0-9]{2,5})\\s+(.+?)\\s+(?:(WF|INC|NCR|CR|[A-D][+-]?|F|W)\\s+)?"
			  + "(\\d{1,3}\\.\\d{2})(?:\\s+\\d{1,3}\\.\\d{2})?(?:\\s+([A-Z]{2,4}(?:\\s+[A-Z]{2,4})*))?\\s*$"
			);

		for(String line: t) {
			Matcher m = p.matcher(line);
			if(m.matches()) {
				String subject = m.group(1); // Required
				String code = m.group(2); // Required
				String title = m.group(3); // Required
				String grade = m.group(4); // Not Required
				if(grade == null) {
					grade = "";
				}
				String creditHoursString = m.group(5); // Required
				int creditHours = Integer.parseInt(creditHoursString.split("\\.")[0]);
				String transfers = m.group(6);
				List<String> transfersList = 
						(transfers == null || transfers.isBlank())
						? new ArrayList<>() : Arrays.asList(transfers.trim().split("\\s+"));
				if(isNumeric(code)) {
					int num = Integer.parseInt(code);
					CourseAttempt newCourse = new CourseAttempt(subject, num, title, creditHours, grade, transfersList);
					courses.add(newCourse);
				} else {
					CourseAttempt newCourse = new CourseAttempt(subject, 0, title, creditHours, grade, transfersList);
					courses.add(newCourse);
				}
			}
		}
		return courses;
	}

	private boolean isNumeric(String code) {
		return code.matches("\\d{4}");
	}
    
}
