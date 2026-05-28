package edu.university.cms.patterns.behavioral.command;

public interface CourseCommand<T> {

    String name();

    T execute();
}
