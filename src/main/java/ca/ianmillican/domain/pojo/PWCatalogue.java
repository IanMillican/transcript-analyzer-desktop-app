package ca.ianmillican.domain.pojo;

import java.util.List;
import java.util.regex.Pattern;

public class PWCatalogue {
    
    private List<String> pCourses;
    private List<String> wCourses;
    private static Pattern courseCodeFormat = Pattern.compile("[A-Z]{2,4}[0-9]{4}");

    public PWCatalogue(List<String> pCourses, List<String> wCourses) {
        this.pCourses = pCourses;
        this.wCourses = wCourses;
    }

    public boolean isPCourse(String courseCode) {
        if(!courseCodeFormat.matcher(courseCode).find()) {
            throw new IllegalArgumentException("Provided string, "+courseCode+", is not a valid course code");
        }
        return pCourses.contains(courseCode);
    }

    public boolean isWCourse(String courseCode) {
        if(!courseCodeFormat.matcher(courseCode).find()) {
            throw new IllegalArgumentException("Provided string, "+courseCode+", is not a valid course code");
        }
        return wCourses.contains(courseCode);
    }

}
