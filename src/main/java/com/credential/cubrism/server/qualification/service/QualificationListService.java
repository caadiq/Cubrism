package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationListDTO;
import com.credential.cubrism.server.qualification.dto.QualificationListResponseDTO;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class QualificationListService {
    @Value("${open.api.key:}")
    private String apiKey;

    private final QualificationListRepository qualificationListRepository;
    private final WebClient webClient;

    @Autowired
    public QualificationListService(QualificationListRepository qualificationListRepository, WebClient webClient) {
        this.qualificationListRepository = qualificationListRepository;
        this.webClient = webClient;
    }

    public void getQualificationList() {
        String url = "http://openapi.q-net.or.kr/api/service/rest/InquiryListNationalQualifcationSVC/getList?_type=json&serviceKey=" + apiKey;

        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(QualificationListResponseDTO.class)
                .subscribe(this::saveQualificationList);
    }

    private void saveQualificationList(QualificationListResponseDTO response) {
        for (QualificationListDTO qualificationListDTO : response.getResponse().getBody().getItems().getItem()) {
            if (qualificationListDTO.getQualName().equals("국가기술자격")) {
                QualificationList qualificationList = new QualificationList();
                qualificationList.setCode(qualificationListDTO.getCode());
                qualificationList.setName(qualificationListDTO.getName());
                qualificationList.setMiddleFieldName(qualificationListDTO.getMiddleFieldName());
                qualificationList.setMajorFieldName(qualificationListDTO.getMajorFieldName());
                qualificationList.setQualName(qualificationListDTO.getQualName());
                qualificationList.setSeriesName(qualificationListDTO.getSeriesName());
                qualificationListRepository.save(qualificationList);
            }
        }
    }
}
