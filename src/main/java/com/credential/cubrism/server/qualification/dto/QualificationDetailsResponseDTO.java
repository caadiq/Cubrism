package com.credential.cubrism.server.qualification.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class QualificationDetailsResponseDTO {
    private String name;
    private String code;
    private List<Schedule> schedule;
    private Fee fee;
    private String tendency;
    private List<Standard> standard;
    private List<Question> question;
    private String acquisition;

    @Getter
    public static class Schedule {
        private String category;
        private String writtenApp;
        private String writtenExam;
        private String writtenExamResult;
        private String practicalApp;
        private String practicalExam;
        private String practicalExamResult;
    }

    @Getter
    public static class Fee {
        private int writtenFee;
        private int practicalFee;
    }

    @Getter
    public static class Standard {
        private String filePath;
        private String fileName;
    }

    @Getter
    public static class Question {
        private String filePath;
        private String fileName;
    }
}