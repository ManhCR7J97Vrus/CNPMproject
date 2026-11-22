package com.warehouse.entity;

import com.warehouse.entity.enums.SupportStatus;
import com.warehouse.entity.enums.SupportType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "support_tickets")
@Getter
@Setter
@NoArgsConstructor
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(name = "reference_no", nullable = false, unique = true)
    private String referenceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "outbound_order_id", nullable = false)
    private OutboundOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportStatus status;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(length = 2000)
    private String response;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
