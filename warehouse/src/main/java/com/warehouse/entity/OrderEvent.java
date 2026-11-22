package com.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbound_events")
@Getter
@Setter
@NoArgsConstructor
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outbound_order_id", nullable = false)
    private OutboundOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "event_type", nullable = false)
    private String type;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "note")
    private String note;
    @Column(name = "previous_appointment")
    private LocalDateTime previousAppointment;
    @Column(name = "appointment")
    private LocalDateTime appointment;
}
