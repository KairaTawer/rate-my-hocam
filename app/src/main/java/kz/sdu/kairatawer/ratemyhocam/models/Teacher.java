package kz.sdu.kairatawer.ratemyhocam.models;

public class Teacher {

    private String id, name, position, image;
    private int facultyId, ratingCount;
    private float rating;

    public Teacher() {
    }

    public Teacher(String name, String position, String image, int facultyId, float rating, int ratingCount) {
        this.name = name;
        this.position = position;
        this.image = image;
        this.facultyId = facultyId;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
