package com.example.demo.controller;

import com.example.demo.domain.Transaction;
import com.example.demo.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 거래 생성
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Transaction tx, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        tx.setSellerId(loginCompanyId);
        Transaction created = transactionService.create(tx, loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "거래가 성공적으로 생성되었습니다.",
                "data", created
        ));
    }

    /**
     * 거래 전체 조회 (판매자 기준)
     */
    @GetMapping
    public ResponseEntity<?> getMyTransactions(HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        List<Transaction> list = transactionService.getTransactionsBySellerId(loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "거래 목록 조회 성공",
                "count", list.size(),
                "data", list
        ));
    }

    /**
     * 거래 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(
                "message", "거래 상세 조회 성공",
                "data", transactionService.getTransactionById(id)
        ));
    }

    /**
     * 판매자 확인
     */
    @PatchMapping("/{id}/seller-confirm")
    public ResponseEntity<?> sellerConfirm(@PathVariable String id, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        transactionService.confirm(id, "seller", loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "판매자 확인 완료",
                "transactionId", id
        ));
    }

    /**
     * 구매자 확인
     */
    @PatchMapping("/{id}/buyer-confirm")
    public ResponseEntity<?> buyerConfirm(@PathVariable String id, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        transactionService.confirm(id, "buyer", loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "구매자 확인 완료",
                "transactionId", id
        ));
    }

    /**
     * 거래 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        transactionService.deleteTransaction(id, loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "거래 삭제 완료",
                "transactionId", id
        ));
    }

    /**
     * 거래 유효성 확인 (리뷰 작성 등)
     */
    @GetMapping("/{id}/validate")
    public ResponseEntity<?> validateTransaction(@PathVariable String id, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        transactionService.validateConfirmedTransaction(id, loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "유효한 완료 거래입니다. 리뷰 작성 가능",
                "transactionId", id
        ));
    }
}
