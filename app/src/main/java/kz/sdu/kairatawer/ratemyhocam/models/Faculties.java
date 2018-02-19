package kz.sdu.kairatawer.ratemyhocam.models;

import java.util.HashMap;

/**
 * Created by асус on 19.02.2018.
 */

public class Faculties {
    public HashMap<Integer,String> facultyList;

    public Faculties() {
        facultyList.put(0,"Engineering and Natural Sciences");
        facultyList.put(1,"Law and Social Science");
        facultyList.put(2,"Education and Humanity");
        facultyList.put(3,"Business School");
    }

    public String getFacultyName(int i) {
        return facultyList.get(i);
    }
}
