package com.example.demo.service;

import com.example.demo.domain.Products;
import com.example.demo.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    public Optional<Products> getProductById(String id) {
        return productsRepository.findById(id);
    }


    public Products createProduct(Products product) {
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }

        // 필수 필드 유효성 검사
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        if (product.getCompanyId() == null || product.getCompanyId().isEmpty()) {
            throw new IllegalArgumentException("회사 ID는 필수입니다.");
        }
        // 필요 시 추가 필수 필드 체크

        return productsRepository.save(product);
    }

    public Products updateProduct(String id, Products updatedProduct) {
        return productsRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setCategoryId(updatedProduct.getCategoryId());
            existingProduct.setMainImage(updatedProduct.getMainImage());
            existingProduct.setDescriptionImage(updatedProduct.getDescriptionImage());
            existingProduct.setExtensionList(updatedProduct.getExtensionList());
            existingProduct.setAttachmentId(updatedProduct.getAttachmentId()); // 새 컬럼도 추가
            return productsRepository.save(existingProduct);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public void deleteProduct(String id) {
        productsRepository.deleteById(id);
    }
}
