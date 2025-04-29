package com.example.demo.controller;

import com.example.demo.domain.Transaction;
import com.example.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 거래 생성 (판매자만 가능, buyer_id에 대한 문의 존재해야 함)
     */
    @PostMapping
    public ResponseEntity<Transaction> create(
            @RequestBody Transaction transaction,
            @RequestParam String requestingSellerEmail
    ) {
        return ResponseEntity.ok(transactionService.create(transaction, requestingSellerEmail));
    }

    /**
     * 거래 완료 버튼 클릭 (buyer/seller)
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(
            @PathVariable Long id,
            @RequestParam String role
    ) {
        transactionService.confirm(id, role);
        return ResponseEntity.ok().build();
    }

    /**
     * 내가 거래한 내역 조회 (구매자 or 판매자)
     */
//    @GetMapping
//    public ResponseEntity<List<Transaction>> getMyTransactions(
//            @RequestParam Long userId,
//            @RequestParam String userEmail
//    ) {
//        return ResponseEntity.ok(transactionService.getMyTransactions(userId, userEmail));
//    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactionsByEmail(@RequestParam String userEmail) {
        return ResponseEntity.ok(transactionService.getTransactionsBySellerEmail(userEmail));
    }

    // TransactionController.java

    @PatchMapping("/{id}/validate")
    public ResponseEntity<Void> validateTransaction(@PathVariable Long id) {
        transactionService.validateConfirmedTransaction(id);
        return ResponseEntity.ok().build();
    }

}
