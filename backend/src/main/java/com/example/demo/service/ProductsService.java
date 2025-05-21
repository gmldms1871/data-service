package com.example.demo.service;

import com.example.demo.domain.Products;
import com.example.demo.repository.ProductsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    // 전체 조회
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    // 특정 상품 ID로 조회
    public Optional<Products> getProductById(String id) {
        return productsRepository.findById(id);
    }

    // 특정 회사의 상품 조회
    public List<Products> getProductsByCompanyId(String companyId) {
        return productsRepository.findAllByCompanyId(companyId);
    }

    @Transactional
    public Products createProduct(Products product) {
        return productsRepository.save(product);
    }

    @Transactional
    public Products updateProduct(String id, Products newProduct, String sessionCompanyId) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (!product.getCompanyId().equals(sessionCompanyId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        product.setName(newProduct.getName());
        product.setDescription(newProduct.getDescription());
        product.setCategoryId(newProduct.getCategoryId());
        product.setMainImage(newProduct.getMainImage());
        product.setDescriptionImage(newProduct.getDescriptionImage());
        product.setExtensionList(newProduct.getExtensionList());
        product.setAttachmentId(newProduct.getAttachmentId());

        return productsRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String id, String sessionCompanyId) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (!product.getCompanyId().equals(sessionCompanyId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        productsRepository.delete(product);
    }
}
