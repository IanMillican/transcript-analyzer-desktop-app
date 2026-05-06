package ca.ianmillican.domain.interfaces;

import java.util.List;

import ca.ianmillican.domain.pojo.degree.Requirement;
import ca.ianmillican.domain.pojo.transcript.CourseAttempt;

public interface Evaluator<T> {
    
    public T evaluate(Requirement requirement, List<CourseAttempt> coursePool);

    public T evaluate();

}
