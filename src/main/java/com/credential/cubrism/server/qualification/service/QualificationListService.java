package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationDTO;
import com.credential.cubrism.server.qualification.dto.QualificationListResponseDTO;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public void saveQualificationListData() {
        String url = "http://openapi.q-net.or.kr/api/service/rest/InquiryListNationalQualifcationSVC/getList?_type=json&serviceKey=" + apiKey;

        Mono<QualificationListResponseDTO> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(QualificationListResponseDTO.class);
        
        responseMono.subscribe(response -> {
            for (QualificationDTO qualificationDTO : response.getResponse().getBody().getItems().getItem()) {
                if (qualificationDTO.getQualName().equals("국가기술자격")) {
                    QualificationList qualificationList = new QualificationList();
                    qualificationList.setCode(qualificationDTO.getCode());
                    qualificationList.setName(qualificationDTO.getName());
                    qualificationList.setMiddleFieldName(qualificationDTO.getMiddleFieldName());
                    qualificationList.setMajorFieldName(qualificationDTO.getMajorFieldName());
                    qualificationList.setQualName(qualificationDTO.getQualName());
                    qualificationList.setSeriesName(qualificationDTO.getSeriesName());
                    qualificationListRepository.save(qualificationList);
                }
            }
        });
    }
}
