package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles", uniqueConstraints = @UniqueConstraint(columnNames = {"carrier_name", "code"}))
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long id;

    @Column(nullable = false)
    private String code;
    @Column(name = "carrier_name", nullable = false)
    private String carrierName;
    @Column(name = "plate_number", nullable = false)
    private String plateNumber;
    @Column(name = "is_active", nullable = false)
    private boolean active;
}
