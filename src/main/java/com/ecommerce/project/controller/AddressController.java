package com.ecommerce.project.controller;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDto;
import com.ecommerce.project.service.AddressServiceImpl;
import com.ecommerce.project.utility.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressServiceImpl addService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> creatAdd(@RequestBody AddressDto address){
        User user = authUtil.loggedInUser();
        AddressDto result = addService.createAddress(address, user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAllAddresses(){
        List<AddressDto> addresses = addService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long addressId){
        AddressDto add = addService.getAddressById(addressId);
        return new ResponseEntity<>(add, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDto>> getAllUserAddresses(){
        User user = authUtil.loggedInUser();
        List<AddressDto> addresses = addService.getAllUserAdd(user);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddressById(@PathVariable Long addressId, @RequestBody AddressDto address){
            AddressDto add = addService.updateAddressById(addressId, address);
        return new ResponseEntity<>(add, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String add = addService.deleteAddress(addressId);
        return new ResponseEntity<>(add, HttpStatus.OK);
    }

}
