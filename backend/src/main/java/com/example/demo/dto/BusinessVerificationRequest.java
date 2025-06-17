package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationRequest {

    @JsonProperty("b_no")
    private String b_no;
}
