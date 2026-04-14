package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="role_id")
    private Integer roleId;

    @Enumerated(EnumType.STRING)//cuz enum by default stored as integers in db, to chnage that to string
    @Column(name ="role_name", length=50)
    private AppRole roleName;


    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
