package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface TransactionMapper {
    Integer getSellerConfirmed(@Param("transactionId") String transactionId);  // 판매자 거래 확인
    Integer getBuyerConfirmed(@Param("transactionId") String transactionId);   // 구매자 거래 확인
    String getCompanyIdFromBuyerInquiry(@Param("transactionId") String transactionId);
}
