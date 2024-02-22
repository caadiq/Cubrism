package com.credential.cubrism.server.qualification.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class KakaoBookResponseDTO {
    private List<Documents> documents;

    @Getter
    public static class Documents {
        private List<String> authors;
        private String datetime;
        private String publisher;
        private int price;
        private String thumbnail;
        private int sale_price;
        private String title;
        private String url;

        public Integer getSalePrice() {
            return sale_price;
        }
    }
}