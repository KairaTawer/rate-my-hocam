package kz.sdu.kairatawer.ratemyhocam.models;

import java.util.ArrayList;

public class TagUtil {
    ArrayList<String> tagList = new ArrayList<>();

    public TagUtil(ArrayList<String> tagList) {
        this.tagList = tagList;
        this.tagList.add("");
        this.tagList.add("");
        this.tagList.add("");
        this.tagList.add("");
        this.tagList.add("");
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }
}
