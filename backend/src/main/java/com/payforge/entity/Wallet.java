package com.payforge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Data
@Getter
@Setter
@Table(name="wallets")
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(nullable = false , precision = 19,scale = 2)
    private BigDecimal balance=BigDecimal.ZERO;
    @Column(nullable = false)
    private String currency="INR";
    @Column(nullable = false)
    private boolean active=true;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private User user;
}
