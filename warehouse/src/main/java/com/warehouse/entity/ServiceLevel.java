package com.warehouse.entity;

import com.warehouse.entity.enums.Priority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_levels")
@Getter
@Setter
@NoArgsConstructor
public class ServiceLevel {
    @Id
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "sort_rank", nullable = false)
    private int rank;
}
