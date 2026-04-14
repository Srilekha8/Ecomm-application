package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryRequestDTO;
import com.ecommerce.project.payload.CategoryResponseDTO;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
    private CategoryRepository catrepo;

    @Autowired
    private ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository catrepo) {
        this.catrepo = catrepo;
    }


    @Override
    public CategoryResponseDTO getAllCategories(Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum,pageSize, sortByandOrder);
        Page<Category> categoryPageDetails = catrepo.findAll(pageDetails);

        List<Category> categories = categoryPageDetails.getContent();
        if(ObjectUtils.isEmpty(categories)){
            throw new APIException("Your categories list is empty");
        }
        List<CategoryRequestDTO> catlist = categories.stream()
                .map(category -> modelMapper.map(category, CategoryRequestDTO.class))
                .toList();
        CategoryResponseDTO catRespDto = new CategoryResponseDTO();
        catRespDto.setCategories(catlist);
        catRespDto.setPageNumber(categoryPageDetails.getNumber());
        catRespDto.setPageSize(categoryPageDetails.getSize());
        catRespDto.setTotalPages(categoryPageDetails.getTotalPages());
        catRespDto.setTotalElements(categoryPageDetails.getTotalElements());
        catRespDto.setLastPage(categoryPageDetails.isLast());
        return catRespDto;
    }

    @Override
    public CategoryRequestDTO createCategory(CategoryRequestDTO category) {
        Category savedCaegory = catrepo.findByCategoryName(category.getCategoryName());
        if(savedCaegory != null){
            throw  new APIException("Category with name "+ category.getCategoryName()+"already exists!");
        }
        Category savedcat = modelMapper.map(category,Category.class);
        return modelMapper.map(catrepo.save(savedcat), CategoryRequestDTO.class);

    }

    @Override
    public CategoryRequestDTO deleteCategory(Long id) {
        Category cat1 = catrepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryID",id));
        System.out.println("cat found in Repo: " + cat1);
        catrepo.delete(cat1);
        return modelMapper.map(cat1, CategoryRequestDTO.class);
    }

    @Override
    public CategoryRequestDTO updateCategory(CategoryRequestDTO category, Long id) {
        Category savedCaegory = catrepo.findByCategoryName(category.getCategoryName());
        if(savedCaegory != null){
            throw  new APIException("Category with name "+ category.getCategoryName()+"already exists!");
        }
        Category cat = catrepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryID", id));
        cat.setCategoryName(category.getCategoryName());
        return modelMapper.map(catrepo.save(cat),CategoryRequestDTO.class);

    }
}
