package diso.rabbit.model;


import diso.rabbit.data.Day;

/**
 * Created by pabdiava on 9/02/2016.
 */
public class DayModel {
    private long Id;
    private String Name;

    public DayModel(Day obj) {
        setId(obj.getId());
        setName(obj.getName());
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
