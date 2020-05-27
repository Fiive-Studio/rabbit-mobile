package diso.rabbit.model;

import diso.rabbit.data.Course;

/**
 * Created by pabdiava on 5/02/2016.
 */
public class CourseModel {
    Course course;

    public CourseModel(Course obj) {
        course = obj;
    }

    public long getId() {
        return course.getId();
    }

    public void setId(long id) {
        course.setId(id);
    }

    public String getName() {
        return course.getName();
    }

    public void setName(String name) {
        course.setName(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
