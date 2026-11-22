package com.warehouse.entity;

import com.warehouse.entity.enums.StocktakeStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stocktakes")
@Getter
@Setter
@NoArgsConstructor
public class Stocktake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stocktake_id")
    private Long id;

    @Column(name = "reference_no", nullable = false, unique = true)
    private String referenceNo;

    @ManyToMany
    @JoinTable(name = "stocktake_locations", joinColumns = @JoinColumn(name = "stocktake_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id"))
    private List<Location> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scope_batch_id")
    private Batch scopeBatch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StocktakeStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "counter_id", nullable = false)
    private User counter;

    @Column(name = "snapshot_at", nullable = false)
    private LocalDateTime snapshotAt;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
