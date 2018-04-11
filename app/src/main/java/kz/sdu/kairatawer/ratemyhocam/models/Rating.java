package kz.sdu.kairatawer.ratemyhocam.models;

public class Rating {

    private String id, userId, courseCode, comment, teacherId;
    private int rating, difficulty, attendance, takeAgain;
    private boolean isAccepted;

    public Rating(String id, String userId, String courseCode, String comment, String teacherId, int rating, int difficulty, int attendance, int takeAgain, boolean isAccepted) {
        this.id = id;
        this.userId = userId;
        this.courseCode = courseCode;
        this.comment = comment;
        this.teacherId = teacherId;
        this.rating = rating;
        this.difficulty = difficulty;
        this.attendance = attendance;
        this.takeAgain = takeAgain;
        this.isAccepted = isAccepted;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public int getTakeAgain() {
        return takeAgain;
    }

    public void setTakeAgain(int takeAgain) {
        this.takeAgain = takeAgain;
    }
}
