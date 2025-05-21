package com.example.demo.controller;

import com.example.demo.domain.Products;
import com.example.demo.service.ProductsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductsService productsService;

    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    // 전체 상품 목록 + companyId 필터링
    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts(@RequestParam(required = false) String companyId) {
        if (companyId != null) {
            return ResponseEntity.ok(productsService.getProductsByCompanyId(companyId));
        } else {
            return ResponseEntity.ok(productsService.getAllProducts());
        }
    }

    // 특정 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<Products> getProductById(@PathVariable String productId) {
        return productsService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 상품 등록 (세션 기반 companyId)
    @PostMapping
    public ResponseEntity<Products> createProduct(@RequestBody Products product, HttpSession session) {
        String loginCompanyId = (String) session.getAttribute("loginCompanyId");
        if (loginCompanyId == null) return ResponseEntity.status(401).build();
        product.setCompanyId(loginCompanyId);
        return ResponseEntity.ok(productsService.createProduct(product));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id,
                                           @RequestBody Products updatedProduct,
                                           HttpSession session) {
        String sessionCompanyId = (String) session.getAttribute("loginCompanyId");
        if (sessionCompanyId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            Products result = productsService.updateProduct(id, updatedProduct, sessionCompanyId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id, HttpSession session) {
        String sessionCompanyId = (String) session.getAttribute("loginCompanyId");
        if (sessionCompanyId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            productsService.deleteProduct(id, sessionCompanyId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
