package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationListApiGetDTO;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualificationListService {
    @Value("${open.api.key:}")
    private String apiKey;

    private final QualificationListRepository qualificationListRepository;
    private final WebClient webClient;

    public void getQualificationList() {
        String url = "http://openapi.q-net.or.kr/api/service/rest/InquiryListNationalQualifcationSVC/getList?_type=json&serviceKey=" + apiKey;

        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(QualificationListApiGetDTO.class)
                .subscribe(this::saveQualificationList);
    }

    private void saveQualificationList(QualificationListApiGetDTO dto) {
        List<QualificationList> qualificationLists = dto.getResponse().getBody().getItems().getItem().stream()
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
}
