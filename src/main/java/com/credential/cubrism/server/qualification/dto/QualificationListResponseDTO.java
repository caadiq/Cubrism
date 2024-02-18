package com.credential.cubrism.server.qualification.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class QualificationListResponseDTO {
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
        private List<QualificationListDTO> item;
    }
}