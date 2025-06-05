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
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 거래 생성
     */
    @PostMapping ("/create")
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
    @GetMapping("/readMany")
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
    @GetMapping("/readOne/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(
                "message", "거래 상세 조회 성공",
                "data", transactionService.getTransactionById(id)
        ));
    }

    /**
     * 통합 로그인된 아이디로 거래완료버튼
     */
    @PatchMapping("/update/confirm/{id}")
    public ResponseEntity<?> confirmByLoginUser(@PathVariable String id, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "로그인 정보가 없습니다."
            ));
        }

        transactionService.confirmByLoginUser(id, loginCompanyId);

        return ResponseEntity.ok(Map.of(
                "message", "확인 완료",
                "transactionId", id
        ));
    }


    /**
     * 거래 삭제 (소프트 삭제)
     */
    @DeleteMapping("/delete/{id}")
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
    @GetMapping("/readOne/validate/{id}")
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
