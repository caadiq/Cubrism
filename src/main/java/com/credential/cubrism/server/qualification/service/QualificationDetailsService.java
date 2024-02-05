package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationDetailsDTO;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualificationDetailsService {
    private final QualificationListRepository qualificationListRepository;
    private final WebClient webClient;

    @Autowired
    public QualificationDetailsService(QualificationListRepository qualificationListRepository, WebClient webClient) {
        this.qualificationListRepository = qualificationListRepository;
        this.webClient = webClient;
    }

    public void saveQualificationDetailsData() {
        String url = "http://localhost:8000/qualification";

        List<QualificationList> qualificationList = qualificationListRepository.findAll();

        List<QualificationDetailsDTO> qualificationDetailsDTO = qualificationList.stream()
                .map(qualification -> new QualificationDetailsDTO(qualification.getCode(), qualification.getName()))
                .collect(Collectors.toList());

        webClient.post()
                .uri(url)
                .bodyValue(qualificationDetailsDTO)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> System.out.println("GET : " + response));
    }
}
