package com.credential.cubrism.server.qualification.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class QualificationListApiGetDTO {
    private Response response;

    @Getter
    public static class Response {
        private Body body;
    }

    @Getter
    public static class Body {
        private Items items;
    }

    @Getter
    public static class Items {
        private List<Item> item;
    }

    @Getter
    public static class Item {
        private String jmcd;
        private String jmfldnm;
        private String mdobligfldnm;
        private String obligfldnm;
        private String qualgbnm;
        private String seriesnm;
    }
}