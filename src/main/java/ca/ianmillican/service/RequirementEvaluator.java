package ca.ianmillican.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.ianmillican.domain.interfaces.Evaluator;
import ca.ianmillican.domain.pojo.degree.Constraint;
import ca.ianmillican.domain.pojo.degree.CourseReq;
import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.degree.Requirement.TYPE;
import ca.ianmillican.domain.pojo.results.RequirementResult;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;

import java.util.ArrayList;

public class RequirementEvaluator implements Evaluator<RequirementResult> {

    private record gpaData(float gp, float ch) {}
    private record orxorBranchData(int index, RequirementResult result, List<CourseAttempt> coursesUsed) {}
    private final List<String> INVALID_GRADES = List.of("D", "F", "WF", "W", "INC", "NCR", "N/A", "");

    public RequirementEvaluator() {}

    private int countUnsatisfied(RequirementResult req) {
        if(req.getOriginalRequirement().getType() == TYPE.COURSE || req.getOriginalRequirement().getType() == TYPE.CONSTRAINT) {
            return req.isSatisfied() ? 0 : 1;
        } else {
            int count = 0;
            for(RequirementResult child : req.getChildRequirements()) {
                count += countUnsatisfied(child);
            }
            return count;
        }
    }

    private gpaData branchGPATotals(RequirementResult result) {
        if (result.getOriginalRequirement().getType() == TYPE.COURSE) {
            if (!result.isSatisfied()) {
                return new gpaData(0.0f, 0.0f);
            }
            float ch = result.getBestCourseAttempt().getCreditHours();
            float gp = result.getBestCourseAttempt().getGradePoints();
            return new gpaData(gp, ch);
        } else if (result.getOriginalRequirement().getType() == TYPE.AND) {
            float gp = 0.0f;
            float ch = 0.0f;
            for(RequirementResult r : result.getChildRequirements()) {
                gpaData data = branchGPATotals(r);
                gp += data.gp;
                ch += data.ch;
            }
            return new gpaData(gp, ch);
        } else if (result.getOriginalRequirement().getType() == TYPE.OR || result.getOriginalRequirement().getType() == TYPE.XOR) {
            return branchGPATotals(result.getSelectedResult());
        }
        return new gpaData(0.0f, 0.0f);
    }

    private float branchGPA(RequirementResult result) {
        gpaData data = branchGPATotals(result);
        return data.ch > 0 ? data.gp / data.ch : 0.0f;
    }

    @SuppressWarnings("unlikely-arg-type")
    private CourseAttempt matchCourse(Requirement req, List<CourseAttempt> coursePool) {
        CourseReq courseReq = req.getCourse();
        CourseAttempt bestMatch = null;
        for(CourseAttempt ca : coursePool) {
            if (ca.equals(courseReq) && !INVALID_GRADES.contains(ca.getGrade())) {
                if (bestMatch == null) {
                    bestMatch = ca;
                } else if (CourseAttempt.GRADE_COMPARATOR.compare(ca.getGrade(), bestMatch.getGrade()) < 0) {
                    bestMatch = ca;
                }
            }
        }
        return bestMatch;
    }

    private RequirementResult evaluateCourse(Requirement req, List<CourseAttempt> coursePool) {
        CourseAttempt matchedCourse = matchCourse(req, coursePool);
        if(matchedCourse == null) {
            return new RequirementResult(req, false, (CourseAttempt) null);
        } else {
            coursePool.remove(matchedCourse);
            return new RequirementResult(req, true, matchedCourse);
        }
    }

    private RequirementResult evaluateAND(Requirement req, List<CourseAttempt> coursePool) {
        boolean allSatisfied = true;
        List<RequirementResult> childResults = new ArrayList<>();
        for(Requirement child : req.getSubrequirements()) {
            RequirementResult childResult = evaluate(child, coursePool);
            childResults.add(childResult);
            if (!childResult.isSatisfied()) {
                allSatisfied = false;
            }
        }
        return new RequirementResult(req, allSatisfied, childResults, RequirementResult.NO_SELECTION);
    }

    private RequirementResult evaluateOR(Requirement req, List<CourseAttempt> coursePool) {
        
        boolean anySatisfied = false;
        List<RequirementResult> childResults = new ArrayList<>();
        List<orxorBranchData> possibleBranches = new ArrayList<>();
        for(int index=0; index<req.getSubrequirements().size(); index++) {
            List<CourseAttempt> originalPool = new ArrayList<>(coursePool);
            List<CourseAttempt> poolCopy = coursePool.stream().map(CourseAttempt::new).collect(Collectors.toList());
            RequirementResult newEval = evaluate(req.getSubrequirements().get(index), poolCopy);
            List<CourseAttempt> copiesUsed = originalPool.stream().filter(ca -> !poolCopy.contains(ca)).toList();
            childResults.add(newEval);
            if(newEval.isSatisfied()) {
                possibleBranches.add(new orxorBranchData(index, newEval, copiesUsed));
            }
        }

        if(!possibleBranches.isEmpty()) {
            anySatisfied = true;
            orxorBranchData bestBranch = null;
            float bestGPA = -1.0f;
            for(orxorBranchData obd : possibleBranches) {
                float branchGPA = branchGPA(obd.result);
                if(branchGPA > bestGPA) {
                    bestGPA = branchGPA;
                    bestBranch = obd;
                }
            }
            coursePool.removeAll(bestBranch.coursesUsed);
            return new RequirementResult(req, anySatisfied, childResults, bestBranch.index);
        } else {
            int bestIndex = -1;
            int leastUnsatisfied = Integer.MAX_VALUE;
            for(int i=0; i<childResults.size(); i++) {
                int unsatisfiedCount = countUnsatisfied(childResults.get(i));
                if(unsatisfiedCount < leastUnsatisfied) {
                    leastUnsatisfied = unsatisfiedCount;
                    bestIndex = i;
                }
            }
            return new RequirementResult(req, anySatisfied, childResults, bestIndex);
        }
    }

    private RequirementResult evaluateXOR(Requirement req, List<CourseAttempt> coursePool) {

        boolean anySatisfied = false;
        List<RequirementResult> childResults = new ArrayList<>();
        List<orxorBranchData> possibleBranches = new ArrayList<>();
        for(int index=0; index<req.getSubrequirements().size(); index++) {
            List<CourseAttempt> originalPool = new ArrayList<>(coursePool);
            List<CourseAttempt> poolCopy = coursePool.stream().map(CourseAttempt::new).collect(Collectors.toList());
            RequirementResult newEval = evaluate(req.getSubrequirements().get(index), poolCopy);
            List<CourseAttempt> copiesUsed = originalPool.stream().filter(ca -> !poolCopy.contains(ca)).toList();
            childResults.add(newEval);
            if(newEval.isSatisfied()) {
                possibleBranches.add(new orxorBranchData(index, newEval, copiesUsed));
            }
        }

        if(!possibleBranches.isEmpty()) {
            anySatisfied = true;
            orxorBranchData bestBranch = null;
            float bestGPA = -1.0f;
            for(orxorBranchData obd : possibleBranches) {
                float branchGPA = branchGPA(obd.result);
                if(branchGPA > bestGPA) {
                    bestGPA = branchGPA;
                    bestBranch = obd;
                }
            }
            for(orxorBranchData obd : possibleBranches) {
                if(obd.index != bestBranch.index) {
                    coursePool.removeAll(obd.coursesUsed);
                }
            }
            coursePool.removeAll(bestBranch.coursesUsed);
            return new RequirementResult(req, anySatisfied, childResults, bestBranch.index);
        } else {
            int bestIndex = -1;
            int leastUnsatisfied = Integer.MAX_VALUE;
            for(int i=0; i<childResults.size(); i++) {
                int unsatisfiedCount = countUnsatisfied(childResults.get(i));
                if(unsatisfiedCount < leastUnsatisfied) {
                    leastUnsatisfied = unsatisfiedCount;
                    bestIndex = i;
                }
            }
            return new RequirementResult(req, anySatisfied, childResults, bestIndex);
        }

    }

    private RequirementResult evaluateConstraint(Requirement req, List<CourseAttempt> coursePool) {
        Constraint constraint = req.getConstraint();
        int count = constraint.getCount();
        int minCh = constraint.getMinCreditHours();
        int minLevel2000 = constraint.getMinLevel2000();
        int minLevel3000 = constraint.getMinLevel3000();
        int minLevel4000 = constraint.getMinLevel4000();
        List<String> includeSubjects = constraint.getIncludeSubject();
        List<String> excludeSubjects = constraint.getExcludeSubject();
        List<CourseAttempt> filteredCourses = coursePool.stream().filter(ca -> !excludeSubjects.contains(ca.getSubject()) && (includeSubjects.isEmpty() || includeSubjects.contains(ca.getSubject())) && !ca.isCoop() && !INVALID_GRADES.contains(ca.getGrade())).toList();
        filteredCourses = new ArrayList<>(filteredCourses);
        filteredCourses.sort((ca1, ca2) -> Float.compare(ca2.getGradePoints(), ca1.getGradePoints()));
        int curr_count = 0;
        int curr_credit_hour = 0;
        int curr_2000_level = 0;
        int curr_3000_level = 0;
        int curr_4000_level = 0;
        List<CourseAttempt> maybes = new ArrayList<>();
        List<CourseAttempt> includes = new ArrayList<>();

        for(CourseAttempt ca : filteredCourses) {
            if(ca.getCourseNum() >= 4000 && curr_4000_level < minLevel4000) {
                curr_4000_level++;
            } else if(ca.getCourseNum() >= 3000 && curr_3000_level < minLevel3000) {
                curr_3000_level++;
            } else if(ca.getCourseNum() >= 2000 && curr_2000_level < minLevel2000) {
                curr_2000_level++;
            } else {
                maybes.add(ca);
                continue;
            }
            includes.add(ca);
            curr_count++;
            curr_credit_hour += ca.getCreditHours();
        }

        for(CourseAttempt ca : maybes) {
            if(curr_count >= count) {
                break;
            }
            includes.add(ca);
            curr_count++;
            curr_credit_hour += ca.getCreditHours();
        }

        coursePool.removeAll(includes);

        return new RequirementResult(req, curr_count >= count && curr_credit_hour >= minCh, Map.of(count, includes));
    }

    @Override
    public RequirementResult evaluate(Requirement requirement, List<CourseAttempt> coursePool) {
        TYPE type = requirement.getType();
        if(type == TYPE.COURSE) {
            return evaluateCourse(requirement, coursePool);
        } else if (type == TYPE.AND) {
            return evaluateAND(requirement, coursePool);
        } else if (type == TYPE.OR) {
            return evaluateOR(requirement, coursePool);
        } else if (type == TYPE.XOR) {
            return evaluateXOR(requirement, coursePool);
        } else if (type == TYPE.CONSTRAINT) {
            return evaluateConstraint(requirement, coursePool);
        } else {
            throw new IllegalArgumentException("Unknown requirement type: " + type);
        }
    }

    @Override
    public RequirementResult evaluate() {
        throw new UnsupportedOperationException("Requirement evaluation requires "); //To change body of generated methods, choose Tools | Templates.
    }

}
