package com.credential.cubrism.server.qualification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class QualificationDetailsDto {
    private String code;
    private String name;
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
        private Integer writtenFee;
        private Integer practicalFee;
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
        private String title;
        private String authors;
        private String publisher;
        private LocalDate date;
        private Integer price;
        private Integer sale_price;
        private String thumbnail;
        private String url;
    }
}