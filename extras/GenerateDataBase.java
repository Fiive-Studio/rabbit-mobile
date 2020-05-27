package diso.rabbit.data;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

import java.io.Console;

/**
 * Created by pabdiava on 03/03/2015.
 */
public class GenerateDataBase {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "diso.rabbit.data");
        CreateEntities(schema);

        try {
            new DaoGenerator().generateAll(schema, "D:\\Diaz\\DISO\\Test\\GreenDaoTest\\src-gen");
        }catch (Exception e) {
            System.out.print(e.toString());
        }
    }

    private static void CreateEntities(Schema schema){
        /****************** Class *************************/
        Entity courses = schema.addEntity("Course");
        courses.addIdProperty().getProperty();
        courses.addStringProperty("Name").notNull();

        /****************** Alarms *************************/
        Entity alarms = schema.addEntity("Alarm");
        alarms.addIdProperty();
        alarms.addStringProperty("Title").notNull();
        alarms.addStringProperty("Description");
        alarms.addDateProperty("AlarmDate").notNull();
        Property idClassProperty1 = alarms.addLongProperty("IdCourse").getProperty();

        alarms.addToOne(courses, idClassProperty1);

        /******************** Days ***********************/
        Entity days = schema.addEntity("Day");
        days.addIdProperty();
        days.addStringProperty("name").notNull();

        /******************** Schedules ***********************/
        Entity schedules = schema.addEntity("Schedule");
        schedules.addIdProperty();
        schedules.addDateProperty("StartHour").notNull();
        schedules.addDateProperty("EndHour");
        Property idDaysProperty = schedules.addLongProperty("IdDay").getProperty();
        Property idClassProperty2 = schedules.addLongProperty("IdCourse").getProperty();

        schedules.addToOne(days, idDaysProperty);
        schedules.addToOne(courses, idClassProperty2);

        /******************** Relations 1:n ***********************/
        courses.addToMany(alarms, idClassProperty1);
        courses.addToMany(schedules, idClassProperty2);
        days.addToMany(schedules, idDaysProperty);
    }
}
