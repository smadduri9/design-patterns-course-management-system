package edu.university.cms.patterns.structural.composite;

import java.util.List;
import java.util.UUID;

public interface CourseComponent {

    UUID getId();

    String getTitle();

    String getComponentType();

    List<CourseComponent> getChildren();
}
