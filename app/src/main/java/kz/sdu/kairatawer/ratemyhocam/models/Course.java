package kz.sdu.kairatawer.ratemyhocam.models;

/**
 * Created by асус on 19.02.2018.
 */

public class Course {
    private String id, code, name;

    public Course() {
    }

    public Course(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
