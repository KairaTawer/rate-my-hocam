package kz.sdu.kairatawer.ratemyhocam.models;

import java.util.ArrayList;

public class TagUtil {
    ArrayList<String> tagList = new ArrayList<>();

    public TagUtil(ArrayList<String> tagList) {
        this.tagList = tagList;
        this.tagList.add("Справедливые оценки");
        this.tagList.add("Изи ретейк");
        this.tagList.add("Аттенданс важен");
        this.tagList.add("Много читать");
        this.tagList.add("Групповой проект");
        this.tagList.add("Интересные лекции");
        this.tagList.add("Вдохновляет");
        this.tagList.add("Куча домашки");
        this.tagList.add("Веселый учитель");
        this.tagList.add("Много куизов");
        this.tagList.add("Сложные экзамены");
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }
}
