package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessVerificationResponse {

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("request_cnt")
    private int requestCount;

    private List<BusinessResult> data;
}
