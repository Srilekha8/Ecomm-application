package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartResponseDto;
import com.ecommerce.project.payload.ProductRequestDto;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.utility.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartResponseDto addItemToCart(Long productId, Integer quantity) {
        Cart cart = creatCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(
                cart.getCartId(),
                productId
        );

        if (cartItem != null) {
            throw new APIException("CartItem is already exist");
        }
        if(product.getQuantity() < quantity) {
            throw new APIException("The availbale quantity of "+product.getProductName() + " is only" +product.getQuantity());
        }

        if(product.getQuantity()==0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());
        cartItemRepository.save(newCartItem);

        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * quantity);
        cartRepository.save(cart);

        CartResponseDto cartResponseDto = modelMapper.map(cart, CartResponseDto.class);

        List<CartItem> cartItems = cart.getCartItems();
        System.out.println("Number of itmes in the cart:::"+cartItems.size());

        Stream<ProductRequestDto> productDtoStream = cartItems.stream().map(item-> {
            ProductRequestDto proReq = modelMapper.map(item.getProduct(), ProductRequestDto.class);
            proReq.setQuantity(quantity);
            return proReq;
        });
        cartResponseDto.setProductDto(productDtoStream.toList());
        return cartResponseDto;
    }


    private Cart creatCart() {
        Cart userCart = cartRepository.findCartByEmail((authUtil.loggedInEmail()));
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }

    @Override
    public List<CartResponseDto> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()) {
            throw new APIException("No carts found");
        }
        List<CartResponseDto> cartRespdtos = carts.stream().map(cart -> {
            CartResponseDto cartResponseDto = modelMapper.map(cart, CartResponseDto.class);
            List<ProductRequestDto> productDtos = cart.getCartItems().stream()
                    .map(ci ->
                        modelMapper.map(ci.getProduct(), ProductRequestDto.class)
                    ).toList();
            cartResponseDto.setProductDto(productDtos);
            return cartResponseDto;
        }).toList();
        return cartRespdtos;
    }

    @Override
    public CartResponseDto getUerCart() {
        String mail = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(mail);
        Long cartId = cart.getCartId();
        Cart usercart = cartRepository.findCartByEmailAndCartId(mail, cartId);
        if(usercart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartResponseDto cartResponseDto = modelMapper.map(usercart, CartResponseDto.class);
        usercart.getCartItems().forEach(ci -> ci.getProduct().setQuantity(ci.getQuantity()));
        List<ProductRequestDto> products = usercart.getCartItems().stream().map(
                ci-> modelMapper.map(ci.getProduct(), ProductRequestDto.class)
        ).toList();
        cartResponseDto.setProductDto(products);
         return cartResponseDto;
    }

    @Transactional
    @Override
    public CartResponseDto updateCartProductQuantity(Long productId, String op) {
        Integer quantity = op.equalsIgnoreCase("increase") ? 1 : -1;
        //check whether cart exist
        String mail = authUtil.loggedInEmail();
        Long cartId = cartRepository.findCartByEmail(mail).getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity()==0) {
            throw new APIException(product.getProductName() + " is not available");
        }
        if(product.getQuantity() < quantity) {
            throw new APIException("The availbale quantity of "+product.getProductName() + " is only" +product.getQuantity());
        }

        //Get product from cart to update the quantity of the product
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(
               cartId,
                productId
        );
        if(cartItem == null) {
            throw new APIException("Product"+ cartItem.getProduct().getProductName() + " is not available");
        }
        System.out.println("net quantity is : "+ cartItem.getQuantity()+quantity);
        if((cartItem.getQuantity()+quantity) == 0) {
            System.out.println("delete cart product when reduced quantity becomes zero");
            deleteProdFromCart(productId);

        }else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
            CartItem updatedCartItem = cartItemRepository.save(cartItem);
            if (updatedCartItem.getQuantity() == 0) {
                cartItemRepository.deleteById(updatedCartItem.getCartItemId());
            }
        }
        CartResponseDto cartResponseDto = modelMapper.map(cart, CartResponseDto.class);
        List<CartItem> cartItems = cart.getCartItems();
        System.out.println("Number of itmes in the cart:::"+cartItems.size());

        Stream<ProductRequestDto> productDtoStream = cartItems.stream().map(item-> {
            ProductRequestDto proReq = modelMapper.map(item.getProduct(), ProductRequestDto.class);
            proReq.setQuantity(item.getQuantity());
            return proReq;
        });
        cartResponseDto.setProductDto(productDtoStream.toList());
        return cartResponseDto;
    }

    @Transactional
    @Override
    public String deleteProdFromCart(Long productId) {
        String mail = authUtil.loggedInEmail();
        Long cartId = cartRepository.findCartByEmail(mail).getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(
                cartId,
                productId
        );
        if(cartItem == null) {
            throw new APIException("Product"+ cartItem.getProduct().getProductName() + " is not available");
        }
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cartId);
        return "Product "+ cartItem.getProduct().getProductName()+ " has been removed from Cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if(cartItem == null) {
            throw new APIException("Product"+ cartItem.getProduct().getProductName() + " is not available in the cart");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice +  (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.save(cartItem);
    }
}
