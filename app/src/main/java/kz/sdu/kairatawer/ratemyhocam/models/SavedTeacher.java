package kz.sdu.kairatawer.ratemyhocam.models;

public class SavedTeacher {
    String userId, teacherId;

    public SavedTeacher() {}

    public SavedTeacher(String userId, String teacherId) {
        this.userId = userId;
        this.teacherId = teacherId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
