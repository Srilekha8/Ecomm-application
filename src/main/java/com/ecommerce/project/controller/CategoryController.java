package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryRequestDTO;
import com.ecommerce.project.payload.CategoryResponseDTO;
import com.ecommerce.project.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private CategoryServiceImpl catservice;

    public CategoryController(CategoryServiceImpl catservice) {
        this.catservice = catservice;
    }

    @GetMapping("/echo")
    //(@RequestParam(name ="message", defaultValue ="hello", required = false
    public ResponseEntity<String> getEchoMessage(@RequestParam(name ="message") String message) {
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponseDTO> getAllCategories(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNum,
                                                                @RequestParam(name= "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        CategoryResponseDTO totalCategories = catservice.getAllCategories(pageNum,pageSize, sortBy, sortOrder);
        return  new ResponseEntity<>(totalCategories, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryRequestDTO> createNewCategory(@Valid @RequestBody CategoryRequestDTO category){
        CategoryRequestDTO status = catservice.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(status);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryRequestDTO> deleteCat(@PathVariable Long id){
        CategoryRequestDTO status = catservice.deleteCategory(id);
            //can be written in various forms
            //return new ResponseEntity<>(catservice.deleteCategory(id), HttpStatus.OK);
            //return ResponseEntity.ok(catservice.deleteCategory(id));
            return new ResponseEntity<>(status, HttpStatus.OK);

    }

    @PutMapping("/admin/categories/{catId}")
    public ResponseEntity<CategoryRequestDTO> updateCategory(@RequestBody CategoryRequestDTO category, @PathVariable Long catId){
            CategoryRequestDTO updatedCat = catservice.updateCategory(category, catId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCat);

    }
}
