package com.zjjxy.cache.controller;

import com.zjjxy.cache.service.ProductBloomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    private ProductBloomService productBloomService;

    @GetMapping("/{id}")
    public String getProductInfo(@PathVariable Long id) {
        return productBloomService.getProduct(id);
    }
}