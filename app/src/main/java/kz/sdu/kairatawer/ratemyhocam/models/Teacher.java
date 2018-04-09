package kz.sdu.kairatawer.ratemyhocam.models;

public class Teacher {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id, name, position;
    private int facultyId;

    private int ratingCount;
    private float rating;

    public Teacher() {}

    public Teacher(String name, String position, int facultyId, float rating, int ratingCount) {
        this.name = name;
        this.position = position;
        this.facultyId = facultyId;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }
}
