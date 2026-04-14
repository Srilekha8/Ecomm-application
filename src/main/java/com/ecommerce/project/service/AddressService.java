package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDto;

import java.util.List;

public interface AddressService {

    AddressDto createAddress(AddressDto address,  User user);

    List<AddressDto> getAllAddresses();

    AddressDto getAddressById(Long addressId);

    List<AddressDto> getAllUserAdd(User user);

    AddressDto updateAddressById(Long addressId, AddressDto address);

    String deleteAddress(Long addressId);
}
