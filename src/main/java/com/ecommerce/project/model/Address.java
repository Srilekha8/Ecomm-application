package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min =  5, message = "street name must contains at-least 5 characters")
    private String street;

    @NotBlank
    @Size(min =  5, message = "City name must contains at-least 5 characters")
    private String city;

    @NotBlank
    @Size(min =  5, message = "State name must contains at-least 5 characters")
    private String state;

    @NotBlank
    @Size(min =  6, message = "Pincode name must contains at-least 6 characters")
    private String pinCode;

    @NotBlank
    @Size(min =  5, message = "Country name must contains at-least 5 characters")
    private String country;

    @NotBlank
    @Size(min =  5, message = "Building name must contains at-least 5 characters")
    private String buildingName;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "address", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Order> order;

    public Address(String street, String city, String state, String pinCode, String country, String buildingName) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.country = country;
        this.buildingName = buildingName;
    }
}
