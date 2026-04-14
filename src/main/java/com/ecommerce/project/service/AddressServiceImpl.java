package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDto;
import com.ecommerce.project.repository.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private AddressRepository addressRepository;

    @Autowired
    ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public AddressDto createAddress(AddressDto addressDto, User user) {
        Address address = modelMapper.map(addressDto, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();
        List<AddressDto> addressDtoList = addressList.stream().map(
                address -> modelMapper.map(address, AddressDto.class)
        ).toList();
        return addressDtoList;
    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "AdressId",addressId));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAllUserAdd(User user) {
        List<Address> addressList = user.getAddresses();
        List<AddressDto> addressDtoList = addressList.stream().map(
                address -> modelMapper.map(address, AddressDto.class)
        ).toList();
        return addressDtoList;
    }

    @Override
    public AddressDto updateAddressById(Long addressId, AddressDto address) {
        Address add = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "AdressId",addressId));
        add.setCity(address.getCity());
        add.setCountry(address.getCountry());
        add.setState(address.getState());
        add.setStreet(address.getStreet());
        add.setPinCode(address.getPinCode());
        add.setBuildingName(address.getBuildingName());
        Address savedAddress = addressRepository.save(add);
        return modelMapper.map(savedAddress, AddressDto.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address add = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "AdressId",addressId));
        addressRepository.delete(add);
        return "Address is deleted successfully";
    }

}
