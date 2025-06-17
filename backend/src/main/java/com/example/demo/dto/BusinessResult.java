package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResult {

    @JsonProperty("b_no")
    private String businessNumber;

    @JsonProperty("tax_type")
    private String taxType;
}
