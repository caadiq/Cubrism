package com.credential.cubrism.server.qualification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class QualificationDTO {
    private String code;
    private String name;
    private String middleFieldName;
    private String majorFieldName;
    private String qualName;
    private String seriesName;

    @JsonProperty("jmcd")
    public String getCode() {
        return code;
    }

    @JsonProperty("jmfldnm")
    public String getName() {
        return name;
    }

    @JsonProperty("mdobligfldnm")
    public String getMiddleFieldName() {
        return middleFieldName;
    }

    @JsonProperty("obligfldnm")
    public String getMajorFieldName() {
        return majorFieldName;
    }

    @JsonProperty("qualgbnm")
    public String getQualName() {
        return qualName;
    }

    @JsonProperty("seriesnm")
    public String getSeriesName() {
        return seriesName;
    }
}
