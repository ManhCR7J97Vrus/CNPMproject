package com.warehouse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Serializes warehouse writes across app instances until the transaction commits. */
@Component
@RequiredArgsConstructor
class StockWriteLock {
    private final JdbcTemplate jdbc;

    @Transactional(propagation = Propagation.MANDATORY)
    public void acquire() {
        jdbc.queryForObject("SELECT lock_id FROM stock_write_lock WHERE lock_id = 1 FOR UPDATE", Integer.class);
    }
}
