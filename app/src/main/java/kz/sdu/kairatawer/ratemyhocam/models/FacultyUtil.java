package kz.sdu.kairatawer.ratemyhocam.models;

import java.util.HashMap;

/**
 * Created by асус on 19.02.2018.
 */

public class FacultyUtil {
    public HashMap<Integer,String> facultyList = new HashMap<>();

    public FacultyUtil() {
        facultyList.put(0,"Инженерия и естественные науки");
        facultyList.put(1,"Юрисприденция и социальные науки");
        facultyList.put(2,"Педагогика и гуманитарные науки");
        facultyList.put(3,"Бизнес-школа");
    }

    public String getFacultyName(int i) {
        return facultyList.get(i);
    }
}
