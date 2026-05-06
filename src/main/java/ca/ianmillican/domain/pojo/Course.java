package ca.ianmillican.domain.pojo;

public class Course<T extends Course<T>> implements Comparable<T> {

	private final String subject;
	private final int num;
	private final String name;
	private final int creditHours;
	private final boolean coop;
	
	public Course(String subject, int num, String name, int creditHours, boolean coop) {
		this.subject = subject;
		this.num = num;
		this.name = name;
		this.creditHours = creditHours;
		this.coop = coop;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public int getCourseNum() {
		return num;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCreditHours() {
		return creditHours;
	}
	
	public String getCourseCode() {
		return subject + num;
	}
	
	public boolean isCoop() {
		return coop;
	}
	
	@Override
	public String toString() {
		return getCourseCode() + ": " + name + " Credit Hours: " + creditHours;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {throw new NullPointerException("Can not compare Course to null object");}
		if(!(o instanceof Course<?> c)) {
			return false;
		}
		return c.getCourseCode().equals(this.getCourseCode());
	}
	
	@Override
	public int hashCode() {
		return getCourseCode().hashCode();
	}

	@Override
	public int compareTo(T o) {
		if(o == null) {throw new NullPointerException("Can not compare Course to null object");}
		return this.getCourseCode().compareTo(o.getCourseCode());
	}

}
