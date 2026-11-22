package com.warehouse.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "storage_profiles")
@Getter
@Setter
@NoArgsConstructor
public class StorageProfile {
    @Id
    private String code;

    @Column(name = "temperature_min", precision = 6, scale = 2)
    private BigDecimal temperatureMin;
    @Column(name = "temperature_max", precision = 6, scale = 2)
    private BigDecimal temperatureMax;
    @Column(name = "humidity_min", precision = 5, scale = 2)
    private BigDecimal humidityMin;
    @Column(name = "humidity_max", precision = 5, scale = 2)
    private BigDecimal humidityMax;
    @Column(name = "avoid_light", nullable = false)
    private boolean avoidLight;
    @Column(name = "fire_protection", nullable = false)
    private boolean fireProtection;
    @Column(name = "incompatible_groups", nullable = false)
    private String incompatibleGroups;
}
