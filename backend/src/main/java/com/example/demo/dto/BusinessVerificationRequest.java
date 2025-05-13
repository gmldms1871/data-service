package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationRequest {
    private List<BusinessInfo> businesses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInfo {
        private String b_no; // 사업자번호
        private String start_dt; // 개업일자(YYYYMMDD)
        private String p_nm; // 대표자명
        private String p_nm2; // 대표자명2 (공동대표인 경우)
        private String b_nm; // 상호
        private String corp_no; // 법인번호
        private String b_sector; // 주업태명
        private String b_type; // 주종목명
    }
}