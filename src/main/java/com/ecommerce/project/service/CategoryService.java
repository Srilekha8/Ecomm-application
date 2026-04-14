package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryRequestDTO;
import com.ecommerce.project.payload.CategoryResponseDTO;

public interface CategoryService {

    CategoryResponseDTO getAllCategories(Integer pageNum, Integer pageSize,String sortBy, String sortOrder);

    CategoryRequestDTO createCategory(CategoryRequestDTO category);

    CategoryRequestDTO deleteCategory(Long id);

    CategoryRequestDTO updateCategory(CategoryRequestDTO category, Long id);
}
