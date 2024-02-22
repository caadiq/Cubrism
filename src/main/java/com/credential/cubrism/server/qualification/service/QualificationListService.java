package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationListApiDTO;
import com.credential.cubrism.server.qualification.dto.QualificationListResponseDTO;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

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
        List<QualificationList> qualificationLists = response.getResponse().getBody().getItems().getItem().stream()
                .filter(item -> "국가기술자격".equals(item.getQualgbnm()))
                .map(item -> {
                    QualificationList qualificationList = new QualificationList();
                    qualificationList.setCode(item.getJmcd());
                    qualificationList.setName(item.getJmfldnm());
                    qualificationList.setMiddleFieldName(item.getMdobligfldnm());
                    qualificationList.setMajorFieldName(item.getObligfldnm());
                    qualificationList.setQualName(item.getQualgbnm());
                    qualificationList.setSeriesName(item.getSeriesnm());
                    return qualificationList;
                })
                .collect(Collectors.toList());
        qualificationListRepository.saveAll(qualificationLists);
    }

    public List<QualificationListApiDTO> returnQualificationList() {
        return qualificationListRepository.findAll().stream()
                .map(qualificationList -> new QualificationListApiDTO(
                        qualificationList.getCode(),
                        qualificationList.getName(),
                        qualificationList.getMiddleFieldName(),
                        qualificationList.getMajorFieldName()
                ))
                .collect(Collectors.toList());
    }
}
