package kz.sdu.kairatawer.ratemyhocam.models;

/**
 * Created by асус on 19.02.2018.
 */

public class Users {
    private int graduateYear, faculty;
    private boolean admin;

    public Users() {}

    public Users(int graduateYear, int faculty, boolean isAdmin) {
        this.graduateYear = graduateYear;
        this.faculty = faculty;
        this.admin = isAdmin;
    }

    public int getGraduateYear() {
        return graduateYear;
    }

    public void setGraduateYear(int graduateYear) {
        this.graduateYear = graduateYear;
    }

    public int getFaculty() {
        return faculty;
    }

    public void setFaculty(int faculty) {
        this.faculty = faculty;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
