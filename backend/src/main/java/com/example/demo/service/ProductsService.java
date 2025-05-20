package com.example.demo.service;

import com.example.demo.domain.Products;
import com.example.demo.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    public Optional<Products> getProductById(String productId) {
        return productsRepository.findById(productId);
    }

    public Products createProduct(Products product, String companyId) {
        product.setCompanyId(companyId); // 꼭 필요
        return productsRepository.save(product);
    }

    public Products updateProduct(String productId, Products updatedProduct) {
        Products existing = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setMainImage(updatedProduct.getMainImage());
        existing.setDescriptionImage(updatedProduct.getDescriptionImage());
        existing.setExtensionList(updatedProduct.getExtensionList());
        existing.setAttachmentId(updatedProduct.getAttachmentId());
        existing.setCategoryId(updatedProduct.getCategoryId());

        return productsRepository.save(existing);
    }

    public void deleteProduct(String productId) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
        productsRepository.delete(product);
    }
}
