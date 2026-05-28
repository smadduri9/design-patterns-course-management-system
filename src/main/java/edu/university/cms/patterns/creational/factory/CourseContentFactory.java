package edu.university.cms.patterns.creational.factory;

import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CourseModule;
import edu.university.cms.domain.Rubric;

public interface CourseContentFactory {

    CourseModule createStarterModule();

    Rubric createStarterRubric();

    Assignment createStarterAssignment(Rubric rubric);
}
