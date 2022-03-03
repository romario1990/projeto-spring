package com.web.rest.api.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pguser")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @Column(nullable = false, length = 100)
    public String username;
    @Column(nullable = false, length = 255)
    public String password;
    @Column(name = "is_enabled", nullable = false)
    public boolean isEnabled;
    @Column(name = "register_date",nullable = false)
    public Date registerDate;
    @Column(nullable = false, length = 255)
    public String name;
    @Column(nullable = false, length = 100)
    public String surname;
    @Column(nullable = false, length = 255)
    public String email;
    @Column(nullable = false, length = 15)
    public String phone;

}
