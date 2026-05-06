package ca.ianmillican.domain.pojo.degree;

import java.util.List;

public class Constraint {
    
    private final int count;
    private final int minCreditHours;
    private final List<String> includeSubject;
    private final List<String> excludeSubject;
    private final int minLevel2000;
    private final int minLevel3000;
    private final int minLevel4000;

    public Constraint(int count, int minCreditHours, List<String> includeSubject, 
                      List<String> excludeSubject, int minLevel2000, 
                      int minLevel3000, int minLevel4000) {
        this.count = count;
        this.minCreditHours = minCreditHours;
        this.includeSubject = List.copyOf(includeSubject);
        this.excludeSubject = List.copyOf(excludeSubject);
        this.minLevel2000 = minLevel2000;
        this.minLevel3000 = minLevel3000;
        this.minLevel4000 = minLevel4000;
    }

    public int getCount() { return count; }
    public int getMinCreditHours() { return minCreditHours; }
    public List<String> getIncludeSubject() { return includeSubject; }
    public List<String> getExcludeSubject() { return excludeSubject; }
    public int getMinLevel2000() { return minLevel2000; }
    public int getMinLevel3000() { return minLevel3000; }
    public int getMinLevel4000() { return minLevel4000; }
}