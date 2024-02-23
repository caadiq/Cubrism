package com.credential.cubrism.server.qualification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class QualificationDetailsApiDTO {
    private String code;
    private List<Schedule> schedule;
    private Fee fee;
    private String tendency;
    private List<Standard> standard;
    private List<Question> question;
    private String acquisition;
    private List<Books> books;

    @Getter
    @AllArgsConstructor
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
    @AllArgsConstructor
    public static class Fee {
        private int writtenFee;
        private int practicalFee;
    }

    @Getter
    @AllArgsConstructor
    public static class Standard {
        private String filePath;
        private String fileName;
    }

    @Getter
    @AllArgsConstructor
    public static class Question {
        private String filePath;
        private String fileName;
    }

    @Getter
    @AllArgsConstructor
    public static class Books {
        private String authors;
        private LocalDate date;
        private int price;
        private String publisher;
        private int sale_price;
        private String thumbnail;
        private String title;
        private String url;
    }
}
