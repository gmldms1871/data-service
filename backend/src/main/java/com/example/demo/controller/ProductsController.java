package com.example.demo.controller;

import com.example.demo.domain.Products;
import com.example.demo.service.ProductsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductsController {

    private final ProductsService productsService;

    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    // 1) 전체 상품 목록 조회
    @GetMapping("/readMany")
    public ResponseEntity<List<Products>> getAllProducts() {
        return ResponseEntity.ok(productsService.getAllProducts());
    }

    // 2) 특정 상품 상세 조회
    @GetMapping("/readOne/{productId}")
    public ResponseEntity<Products> getProductById(@PathVariable String productId) {
        return productsService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3) 특정 회사의 상품 목록 조회
    @GetMapping("/readMany/{companyId}")
    public ResponseEntity<List<Products>> getProductsByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(productsService.getProductsByCompanyId(companyId));
    }

    // 4) 상품 등록 (세션 기반)
    @PostMapping("/create")
    public ResponseEntity<Products> createProduct(@RequestBody Products product, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) return ResponseEntity.status(401).build();

        product.setCompanyId(loginCompanyId);
        return ResponseEntity.ok(productsService.createProduct(product));
    }

    // 5) 상품 수정
    @PatchMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId,
                                           @RequestBody Products updatedProduct,
                                           HttpSession session) {
        String sessionCompanyId = (String) session.getAttribute("loginCompanyId");
        if (sessionCompanyId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            Products result = productsService.updateProduct(productId, updatedProduct, sessionCompanyId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // 6) 상품 삭제
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId, HttpSession session) {
        String sessionCompanyId = (String) session.getAttribute("loginCompanyId");
        if (sessionCompanyId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        try {
            productsService.deleteProduct(productId, sessionCompanyId);
            return ResponseEntity.ok(Map.of("success", true, "message", "상품이 삭제되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
