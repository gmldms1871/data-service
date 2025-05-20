package com.example.demo.controller;

import com.example.demo.domain.Products;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.ProductsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    // 전체 상품 조회
    @GetMapping
    public ResponseEntity<ResponseDto<?>> getAllProducts(HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(401)
                    .body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        List<Products> products = productsService.getAllProducts();
        return ResponseEntity.ok(new ResponseDto<>(true, "전체 상품 조회 성공", products));
    }

    // 단일 상품 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDto<Products>> getProductById(@PathVariable String productId, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(401)
                    .body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        return productsService.getProductById(productId)
                .map(product -> ResponseEntity.ok(new ResponseDto<>(true, "상품 조회 성공", product)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ResponseDto<>(false, "상품을 찾을 수 없습니다", null)));
    }

    // 상품 등록
    @PostMapping
    public ResponseEntity<ResponseDto<?>> createProduct(@RequestBody Products product, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(401)
                    .body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        Products created = productsService.createProduct(product, loginCompanyId);
        return ResponseEntity.ok(new ResponseDto<>(true, "상품 등록 성공", created));
    }

    // 상품 수정
    @PatchMapping("/{productId}")
    public ResponseEntity<ResponseDto<?>> updateProduct(@PathVariable String productId,
                                                        @RequestBody Products product,
                                                        HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(401)
                    .body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        try {
            Products updated = productsService.updateProduct(productId, product);
            return ResponseEntity.ok(new ResponseDto<>(true, "상품 수정 성공", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDto<>(false, e.getMessage(), null));
        }
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseDto<?>> deleteProduct(@PathVariable String productId, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) {
            return ResponseEntity.status(401)
                    .body(new ResponseDto<>(false, "로그인이 필요합니다", null));
        }

        try {
            productsService.deleteProduct(productId);
            return ResponseEntity.ok(new ResponseDto<>(true, "상품 삭제 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(new ResponseDto<>(false, "상품 삭제 실패: " + e.getMessage(), null));
        }
    }
}
