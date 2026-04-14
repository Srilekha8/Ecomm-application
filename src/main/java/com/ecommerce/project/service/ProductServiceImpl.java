package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartResponseDto;
import com.ecommerce.project.payload.ProductRequestDto;
import com.ecommerce.project.payload.ProductResponseDto;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{
    private ProductRepository productRepo;
    private CategoryRepository categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartService cartService;

    @Value("${product.images.dir}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public ProductResponseDto getAllProducts(Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum,pageSize, sortByandOrder);
        Page<Product> products = productRepo.findAll(pageDetails);
        List<ProductRequestDto> productDto = products.stream().map(
                product -> modelMapper.map(product, ProductRequestDto.class)).toList();

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setProducts(productDto);
        productResponseDto.setPageNumber(products.getNumber());
        productResponseDto.setPageSize(products.getSize());
        productResponseDto.setTotalPages(products.getTotalPages());
        productResponseDto.setTotalElements(products.getTotalElements());

        return productResponseDto;
    }

    @Override
    public ProductResponseDto getProductsByKeyword(String keyword,Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum,pageSize, sortByandOrder);
        Page<Product> products = productRepo.findProductByProductNameContainingIgnoreCase(keyword, pageDetails);
        List<ProductRequestDto> productDto = products.stream().map(
                product -> modelMapper.map(product, ProductRequestDto.class)).toList();

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setProducts(productDto);
        productResponseDto.setPageNumber(products.getNumber());
        productResponseDto.setPageSize(products.getSize());
        productResponseDto.setTotalPages(products.getTotalPages());
        productResponseDto.setTotalElements(products.getTotalElements());
        return productResponseDto;
    }

    public ProductResponseDto getProductsByCategory(Long categoryId, Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {
        Category cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId",categoryId));

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNum,pageSize, sortByandOrder);
        Page<Product> catProducts = productRepo.findProductByCategory(cat, pageDetails);
        List<ProductRequestDto> productDto = catProducts.stream().map(
                product -> modelMapper.map(product, ProductRequestDto.class)).toList();
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setProducts(productDto);
        productResponseDto.setProducts(productDto);
        productResponseDto.setPageNumber(catProducts.getNumber());
        productResponseDto.setPageSize(catProducts.getSize());
        productResponseDto.setTotalPages(catProducts.getTotalPages());
        productResponseDto.setTotalElements(catProducts.getTotalElements());
        return productResponseDto;

    }

    @Override
    public ProductRequestDto addProduct(Long categoryId, ProductRequestDto product) {
        Category cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId",categoryId));

        List<Product> products = cat.getProductList();
        boolean isProductPresnt = false;
        for  (Product pro : products) {
            if(pro.getProductName().equals(product.getProductName())) {
                isProductPresnt=true;
                break;
            }
        }
        if(!isProductPresnt) {
            Product dtoToProduct = modelMapper.map(product, Product.class);
            dtoToProduct.setCategory(cat);
            dtoToProduct.setImage("image.png");
            double specialPrice = product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice());
            dtoToProduct.setSpecialPrice(specialPrice);
            Product savedProduct = productRepo.save(dtoToProduct);
            return modelMapper.map(savedProduct, ProductRequestDto.class);
        }else{
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductRequestDto updateProduct(Long productId, ProductRequestDto product) {
        Product dbProduct = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId",productId));

        dbProduct.setProductName(product.getProductName());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDiscount(product.getDiscount());
        double specialPrice = product.getPrice() - (product.getDiscount()*0.01 * product.getPrice());
        dbProduct.setSpecialPrice(specialPrice);
        dbProduct.setDescription(product.getDescription());
        dbProduct.setQuantity(product.getQuantity());
        Product updatedProduct = productRepo.save(dbProduct);

        //To propogate the product updates to the cart
        List<Cart> carts = cartRepo.findCartByProductId(productId);
        List<CartResponseDto> cartDtos = carts.stream().map(cart -> {
            CartResponseDto cartResponseDto = modelMapper.map(cart, CartResponseDto.class);
            List<ProductRequestDto> products = cart.getCartItems().stream().map(
                    ci -> modelMapper.map(ci.getProduct(), ProductRequestDto.class)
            ).toList();
            cartResponseDto.setProductDto(products);
            return cartResponseDto;
        }).toList();
        cartDtos.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(updatedProduct, ProductRequestDto.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product getProduct = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId",productId));
        List<Cart> carts = cartRepo.findCartByProductId(productId);
        carts.forEach(cart -> cartService.deleteProdFromCart(productId));
        productRepo.delete(getProduct);
        return "Product Deleted Successfully";
    }

    @Override
    public ProductRequestDto saveOrUpdateProductImage(MultipartFile productImage, Long productId) throws IOException {
        Product getProduct = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId",productId));
        //upload image to the image directory(server) and get the filename of the uploaded image
        String fileName = fileService.uploadImage(path,productImage);
        getProduct.setImage(fileName);
        productRepo.save(getProduct);
        return modelMapper.map(getProduct, ProductRequestDto.class);
    }

}
