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

    public Optional<Products> getProductById(Long id) {
        return productsRepository.findById(id);
    }

    public Products createProduct(Products product) {
        return productsRepository.save(product);
    }

    public Products updateProduct(Long id, Products updatedProduct) {
        return productsRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setCategory(updatedProduct.getCategory());
            product.setCategoryId(updatedProduct.getCategoryId());
            product.setMainImage(updatedProduct.getMainImage());
            product.setDescriptionImage(updatedProduct.getDescriptionImage());
            product.setExtensionList(updatedProduct.getExtensionList());
            return productsRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public void deleteProduct(Long id) {
        productsRepository.deleteById(id);
    }
}
