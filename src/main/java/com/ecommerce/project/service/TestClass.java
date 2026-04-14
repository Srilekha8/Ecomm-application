package com.ecommerce.project.service;

import java.util.UUID;

public class TestClass {
    public static void main(String[] args) {
        String originalFileName = "toy.jpg";
        String uuid = UUID.randomUUID().toString();
        System.out.println(originalFileName.lastIndexOf('.'));
        String newFileName = uuid.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        System.out.println(newFileName);
    }
}
