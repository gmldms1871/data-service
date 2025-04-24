package com.example.demo.controller;

import com.example.demo.domain.Products;
import com.example.demo.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @GetMapping
    public List<Products> getAllProducts() {
        return productsService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Products> getProductById(@PathVariable Long productId) {
        return productsService.getProductById(productId)
                .map(ResponseEntity::ok)  // 존재하면 200 OK 반환
                .orElseGet(() -> ResponseEntity.notFound().build());  // 없으면 404 Not Found 반환
    }

    @PostMapping
    public Products createProduct(@RequestBody Products product) {
        return productsService.createProduct(product);
    }

    @PatchMapping("/{productId}")
    public Products updateProduct(@PathVariable Long productId, @RequestBody Products product) {
        return productsService.updateProduct(productId, product);
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productsService.deleteProduct(productId);
    }
}
