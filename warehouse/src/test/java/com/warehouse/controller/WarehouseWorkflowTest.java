package com.warehouse.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/** HTTP integration tests. Run against a dedicated MySQL database with WAREHOUSE_MYSQL_TEST=true. */
@SpringBootTest(properties = {"warehouse.demo=true", "debug=false"})
@AutoConfigureMockMvc
@Import(WarehouseWorkflowTest.TestTime.class)
@EnabledIfEnvironmentVariable(named = "WAREHOUSE_MYSQL_TEST", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WarehouseWorkflowTest {
    private static final LocalDate DAY = LocalDate.of(2026, 11, 21);
    private static final LocalDateTime NOW = DAY.atTime(10, 0);
    private static final String IN = "/inbound-orders";
    private static final String OUT = "/outbound-orders";
    private static final String TRIPS = "/shipments";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired AdjustableClock clock;
    @Value("${WAREHOUSE_ADMIN_PASSWORD}") String password;
    String admin, manager, staff, customer, customer2, supplier, dispatcher;
    long supplierId, staffId;

    @BeforeAll
    void signIn() throws Exception {
        admin = login("admin", 200);
        manager = login("manager", 200);
        staff = login("staff", 200);
        customer = login("customer", 200);
        customer2 = login("customer2", 200);
        supplier = login("supplier", 200);
        dispatcher = login("dispatcher", 200);
        login("driver", 200);
        supplierId = find(get("/suppliers", manager), "code", "NCC-DEMO").path("id").asLong();
        JsonNode accounts = get("/users", admin);
        staffId = find(accounts, "username", "staff").path("id").asLong();
        assertThat(find(accounts, "username", "driver").path("role").asText()).isEqualTo("DRIVER");
    }

    @BeforeEach
    void resetTime() {
        clock.set(NOW);
    }

    @Test
    void accountsLockAndSupplierOwnership() throws Exception {
        var example = fixture(false);
        String username = example.sku() + "-staff";
        long account = user(username, "STAFF", null, null);
        String token = login(username, 200);
        write("/users", admin, Map.of("username", username.toLowerCase(), "fullName", username,
                "password", password, "role", "DRIVER"), 409);
        write("/users", manager, Map.of("username", username + "-other", "fullName", username,
                "password", password, "role", "STAFF"), 403);
        put("/users/" + account, admin, Map.of("fullName", username, "role", "STAFF", "active", false), 200);
        login(username, 401);
        call(HttpMethod.GET, "/inventory", token, null, 401);
        call(HttpMethod.GET, "/inventory", customer, null, 403);

        long otherSupplier = write("/suppliers", manager, Map.of("code", example.sku(), "name", "Partner", "active", true), 200)
                .path("id").asLong();
        String partner = example.sku() + "-partner";
        user(partner, "SUPPLIER", otherSupplier, null);
        String partnerToken = login(partner, 200);
        JsonNode notice = write(IN, supplier, Map.of("expectedAt", NOW.plusDays(1), "externalReference", example.sku(),
                "items", List.of(Map.of("productId", example.product(), "qtyOrdered", 12))), 200);
        long id = notice.path("id").asLong();
        assertThat(notice.path("status").asText()).isEqualTo("EXPECTED");
        assertThat(notice.path("items").get(0).has("unitCost")).isFalse();
        call(HttpMethod.GET, IN + "/" + id, partnerToken, null, 404);
        put(IN + "/" + id, partnerToken, Map.of("expectedAt", NOW.plusDays(1), "externalReference", "foreign",
                "items", List.of(Map.of("productId", example.product(), "qtyOrdered", 2))), 404);
        assertThat(get(IN + "/" + id, staff).path("items").get(0).path("qtyOrdered").asInt()).isEqualTo(12);
    }

    @Test
    void catalogValidationAndInactiveSupplierPreserveExistingDocument() throws Exception {
        Fixture f = fixture(true);
        String profilePath = "/products/storage-profiles/" + f.sku();
        put(profilePath, manager, Map.of("temperatureMin", 2, "temperatureMax", 8, "humidityMin", 40, "humidityMax", 70,
                "avoidLight", true, "fireProtection", true, "incompatibleGroups", List.of("X")), 200);
        put(profilePath, manager, Map.of("temperatureMin", 8, "temperatureMax", 2), 400);
        assertThat(find(get("/products/storage-profiles", manager), "code", f.sku()).path("temperatureMin").asInt()).isEqualTo(2);
        write("/products", manager, Map.of("skuCode", f.sku(), "name", "Duplicate", "category", "Food",
                "unit", "box", "active", true, "minStock", 0), 409);

        JsonNode source = write("/suppliers", manager, Map.of("code", f.sku(), "name", "Partner", "active", true), 200);
        long id = source.path("id").asLong();
        Map<String, Object> planned = Map.of("supplierId", id, "externalReference", f.sku(),
                "items", List.of(Map.of("productId", f.product(), "qtyOrdered", 5)));
        JsonNode doc = write(IN, staff, planned, 200);
        write(IN, manager, planned, 409);
        put("/suppliers/" + id, manager, Map.of("code", f.sku(), "name", "Partner", "active", false), 200);
        write(IN, staff, planned, 409);
        assertThat(get(IN + "/" + doc.path("id").asLong(), staff).path("supplierId").asLong()).isEqualTo(id);
    }

    @Test
    void approvalDoesNotPostAndReplaysDoNotCreateStock() throws Exception {
        Fixture f = fixture(true);
        receiveAndPost(f, f.a(), "A", 10, DAY.plusDays(5));
        JsonNode prepared = receive(f, List.of(new Receipt(f.a(), "A", 50, DAY.plusDays(5))));
        String path = IN + "/" + prepared.path("id").asLong();
        write(path + "/confirm", staff, null, 403);
        write(path + "/confirm", manager, null, 200);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(10);

        JsonNode line = prepared.path("items").get(0);
        write(path + "/post", staff, Map.of("items", List.of(placement(line, f.b()))), 409);
        Map<String, Object> actual = Map.of("items", List.of(placement(line, f.a())));
        write(path + "/post", staff, actual, 200);
        write(path + "/post", staff, actual, 200);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(60);
        JsonNode ledger = get("/movements?referenceNo=" + prepared.path("orderNo").asText(), manager);
        assertThat(ledger.size()).isEqualTo(1);
        assertThat(ledger.get(0).path("changeQty").asInt()).isEqualTo(50);
    }

    @Test
    void laterLockedLocationStopsEveryInboundLine() throws Exception {
        Fixture f = fixture(false);
        receiveAndPost(f, f.a(), "A", 6, null);
        receiveAndPost(f, f.b(), "B", 8, null);
        JsonNode prepared = receive(f, List.of(new Receipt(f.a(), "A", 5, null), new Receipt(f.b(), "B", 3, null)));
        String path = IN + "/" + prepared.path("id").asLong();
        write(path + "/confirm", manager, null, 200);
        put("/locations/" + f.b(), manager, locationData(f.sku(), "B", false, true, f.profile()), 200);
        write(path + "/post", staff, Map.of("items", List.of(placement(prepared.path("items").get(0), f.a()),
                placement(prepared.path("items").get(1), f.b()))), 409);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(6);
        assertThat(stock(f, f.b()).path("qtyOnHand").asInt()).isEqualTo(8);
        assertThat(get("/movements?referenceNo=" + prepared.path("orderNo").asText(), manager).size()).isZero();
        assertThat(get(path, staff).path("status").asText()).isEqualTo("CONFIRMED");
    }

    @Test
    void fefoAndFifoRespectDateBoundsAndNeverReserveOnPreview() throws Exception {
        Fixture f = fixture(true);
        receiveAndPost(f, f.a(), "A", 8, DAY);
        receiveAndPost(f, f.a(), "B", 10, DAY.plusDays(7));
        receiveAndPost(f, f.a(), "X", 100, DAY.minusDays(1));
        receiveAndPost(f, f.a(), "Y", 2, DAY.plusDays(8));
        receiveAndPost(f, f.b(), "Z", 10, DAY);
        put("/locations/" + f.b(), manager, locationData(f.sku(), "B", false, true, f.profile()), 200);
        JsonNode plan = get(OUT + "/" + newOrder(f, 15) + "/suggestions", staff).path("allocations");
        assertThat(names(plan, "batchNo")).containsExactly("A", "B");
        assertThat(plan.get(0).path("qtyRequested").asInt()).isEqualTo(8);
        assertThat(plan.get(1).path("qtyRequested").asInt()).isEqualTo(7);
        assertThat(get("/inventory/summary?sku=" + f.sku(), staff).path("qtyReserved").asInt()).isZero();
        JsonNode alerts = get("/batches/alerts", manager);
        assertThat(namesForSku(alerts.path("expired"), f.sku())).containsExactly("X");
        assertThat(namesForSku(alerts.path("expiring"), f.sku())).containsExactly("A", "Z", "B");
        call(HttpMethod.GET, "/batches/alerts", staff, null, 403);

        Fixture noExpiry = fixture(false);
        receiveAndPost(noExpiry, noExpiry.a(), "EARLY", 5, null);
        clock.set(NOW.plusMinutes(1));
        receiveAndPost(noExpiry, noExpiry.a(), "A-LATE", 5, null);
        assertThat(names(get(OUT + "/" + newOrder(noExpiry, 4) + "/suggestions", staff).path("allocations"), "batchNo"))
                .containsExactly("EARLY");
    }

    @Test
    void concurrentApprovalsCannotOverbookAndCancelReleasesOnlyItsReservation() throws Exception {
        Fixture f = fixture(false);
        receiveAndPost(f, f.a(), "A", 10, null);
        long a = newOrder(f, 8);
        long b = newOrder(f, 8);
        var first = CompletableFuture.supplyAsync(() -> approvalStatus(a));
        var second = CompletableFuture.supplyAsync(() -> approvalStatus(b));
        assertThat(List.of(first.join(), second.join())).containsExactlyInAnyOrder(200, 409);
        assertThat(stock(f, f.a()).path("qtyReserved").asInt()).isEqualTo(8);
        long winner = first.join() == 200 ? a : b;
        long loser = first.join() == 200 ? b : a;
        assertThat(get(OUT + "/" + loser, manager).path("status").asText()).isEqualTo("REQUESTED");
        write(OUT + "/" + winner + "/cancel-request", customer, Map.of("reason", "Address changed"), 200);
        assertThat(stock(f, f.a()).path("qtyReserved").asInt()).isEqualTo(8);
        write(OUT + "/" + winner + "/cancel", manager, Map.of("reason", "Approved cancellation"), 200);
        assertThat(stock(f, f.a()).path("qtyReserved").asInt()).isZero();
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(10);
    }

    @Test
    void pickPackHandoverAndReturnKeepStockConsistent() throws Exception {
        Fixture f = fixture(false);
        receiveAndPost(f, f.a(), "A", 8, null);
        receiveAndPost(f, f.b(), "B", 10, null);
        long orderId = newOrder(f, 15);
        String order = OUT + "/" + orderId;
        JsonNode approved = write(order + "/approve", manager, null, 200);
        JsonNode a = approved.path("allocations").get(0);
        JsonNode b = approved.path("allocations").get(1);
        write(order + "/pick", staff, Map.of("items", List.of(pick(a, 1, f.b()))), 400);
        assertThat(write(order + "/pick", staff, Map.of("items", List.of(pick(a, 5, f.a()))), 200)
                .path("status").asText()).isEqualTo("APPROVED");
        write(order + "/pack", staff, Map.of("packageCount", 2), 409);
        write(order + "/pick", staff, Map.of("items", List.of(pick(a, 3, f.a()), pick(b, 7, f.b()))), 200);
        write(order + "/pack", staff, Map.of("packageCount", 0), 400);
        write(order + "/pack", staff, Map.of("packageCount", 2), 200);
        String dispatchNo = write(order + "/issue-dispatch", staff, null, 200).path("dispatchNo").asText();
        assertThat(write(order + "/issue-dispatch", staff, null, 200).path("dispatchNo").asText()).isEqualTo(dispatchNo);

        long vehicleId = createVehicle(f.sku(), "VAN-CHUYEN", true);
        JsonNode shipment = openTrip(orderId);
        String trip = TRIPS + "/" + shipment.path("id").asLong();
        String assignedDriver = f.sku() + "-assigned";
        long driverId = user(assignedDriver, "DRIVER", null, "VAN-CHUYEN");
        String driver = login(assignedDriver, 200);
        write(trip + "/assign", dispatcher, assignment(driverId, vehicleId, NOW.minusHours(1), NOW.plusHours(1)), 200);
        write(trip + "/accept", driver, Map.of("packageCount", 2), 409);
        String otherDriver = f.sku() + "-driver";
        user(otherDriver, "DRIVER", null, "VAN-CHUYEN");
        String otherToken = login(otherDriver, 200);
        call(HttpMethod.GET, trip, otherToken, null, 404);
        write(trip + "/accept", otherToken, Map.of("packageCount", 2), 404);
        write(trip + "/reschedule", dispatcher, Map.of("kind", "NO_SHOW", "reason", "Late arrival",
                "startAt", NOW.plusHours(1), "endAt", NOW.plusHours(2)), 200);
        write(order + "/handover", staff, Map.of("packageCount", 1), 400);
        write(order + "/handover", staff, Map.of("packageCount", 2), 200);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(8);
        assertThat(stock(f, f.a()).path("qtyReserved").asInt()).isEqualTo(8);
        assertThat(write(trip + "/accept", driver, Map.of("packageCount", 2), 200)
                .path("receivedPackages").asInt()).isEqualTo(2);
        write(trip + "/accept", driver, Map.of("packageCount", 2), 200);
        assertThat(get("/movements?referenceNo=" + dispatchNo, manager).size()).isEqualTo(2);
        assertThat(stock(f, f.b()).path("qtyOnHand").asInt()).isEqualTo(3);
        assertThat(stock(f, f.b()).path("qtyReserved").asInt()).isZero();
        write(order + "/cancel", manager, Map.of("reason", "After handover"), 409);

        write(trip + "/delivery", driver, Map.of("delivered", false, "note", "Nobody available"), 200);
        write(trip + "/reschedule", dispatcher, Map.of("kind", "REDELIVERY", "reason", "Second attempt",
                "startAt", NOW.plusHours(4), "endAt", NOW.plusHours(5)), 200);
        JsonNode journey = get(order, customer);
        assertThat(journey.has("allocations")).isFalse();
        assertThat(names(journey.path("history"), "type")).containsSubsequence("NO_SHOW", "WAREHOUSE_CHECKED",
                "HANDED_OVER", "FAILED", "REDELIVERY");
        write(trip + "/delivery", driver, Map.of("delivered", true), 200);
        assertThat(get("/movements?referenceNo=" + dispatchNo, manager).size()).isEqualTo(2);

        long itemId = a.path("itemId").asLong();
        JsonNode ret = write(IN + "/returns", staff, Map.of("outboundOrderId", orderId, "items", List.of(
                Map.of("outboundItemId", itemId, "locationId", f.a(), "quantity", 4, "itemCondition", "GOOD"),
                Map.of("outboundItemId", itemId, "locationId", f.q(), "quantity", 2, "itemCondition", "DAMAGED"))), 200);
        write(IN + "/returns", staff, Map.of("outboundOrderId", orderId, "items", List.of(
                Map.of("outboundItemId", itemId, "locationId", f.a(), "quantity", 3, "itemCondition", "GOOD"))), 400);
        assertThat(stock(f, f.a())).isNull();
        confirmAndPost(ret);
        assertThat(stock(f, f.a()).path("qtyAvailable").asInt()).isEqualTo(4);
        assertThat(stock(f, f.q()).path("qtyOnHand").asInt()).isEqualTo(2);
        assertThat(stock(f, f.q()).path("qtyAvailable").asInt()).isZero();
    }

    @Test
    void assignmentRejectsOverlapAndOutsideCarrier() throws Exception {
        Fixture f = fixture(false);
        receiveAndPost(f, f.a(), "A", 12, null);
        long first = preparedOrder(f, 2);
        long second = preparedOrder(f, 2);
        long van = createVehicle(f.sku() + "-active", "VAN-CHUYEN", true);
        long disabled = createVehicle(f.sku() + "-disabled", "VAN-CHUYEN", false);
        assertThat(names(get(TRIPS + "/vehicles?activeOnly=true", dispatcher), "code")).doesNotContain(f.sku() + "-DISABLED");
        String firstTrip = TRIPS + "/" + openTrip(first).path("id").asLong();
        String secondTrip = TRIPS + "/" + openTrip(second).path("id").asLong();
        LocalDateTime starts = NOW.plusDays(5);
        long driverId = user(f.sku() + "-assigned", "DRIVER", null, "VAN-CHUYEN");
        JsonNode accepted = write(firstTrip + "/assign", dispatcher, assignment(driverId, van, starts, starts.plusHours(1)), 200);
        assertThat(accepted.path("vehicleId").asLong()).isEqualTo(van);
        long other = user(f.sku() + "-driver", "DRIVER", null, "VAN-CHUYEN");
        write(secondTrip + "/assign", dispatcher, assignment(other, disabled, starts, starts.plusHours(1)), 409);
        assertThat(write(secondTrip + "/assign", dispatcher, assignment(other, van, starts.plusMinutes(30), starts.plusHours(2)), 409)
                .path("error").path("message").asText()).contains("trùng lịch");
        long secondVan = createVehicle(f.sku() + "-alt", "VAN-CHUYEN", true);
        write(secondTrip + "/assign", dispatcher, assignment(driverId, secondVan, starts.plusMinutes(30), starts.plusHours(2)), 409);
        long foreignDriver = user(f.sku() + "-foreign", "DRIVER", null, "FOREIGN-CARRIER");
        write(secondTrip + "/assign", dispatcher, assignment(foreignDriver, secondVan, starts, starts.plusHours(1)), 409);
        assertThat(get(secondTrip, manager).path("status").asText()).isEqualTo("PENDING_ASSIGNMENT");
    }

    @Test
    void stocktakeAndQuarantineUseLedgerAndDetectStaleSnapshot() throws Exception {
        Fixture f = fixture(false);
        receiveAndPost(f, f.a(), "A", 20, null);
        receiveAndPost(f, f.b(), "B", 6, null);
        JsonNode count = write("/stocktakes", manager, Map.of("locationIds", List.of(f.a(), f.b()), "counterId", staffId), 200);
        String path = "/stocktakes/" + count.path("id").asLong();
        JsonNode a = count.path("items").get(0);
        JsonNode b = count.path("items").get(1);
        write(path + "/count", staff, Map.of("items", List.of(Map.of("itemId", a.path("id").asLong(), "countedQty", 18))), 400);
        write(path + "/count", staff, Map.of("items", List.of(Map.of("itemId", a.path("id").asLong(), "countedQty", 18),
                Map.of("itemId", b.path("id").asLong(), "countedQty", 7)), "reason", "Cycle count"), 200);
        write(path + "/approve", manager, null, 200);
        write(path + "/approve", manager, null, 200);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(18);
        assertThat(stock(f, f.b()).path("qtyOnHand").asInt()).isEqualTo(7);
        JsonNode adjustments = get("/movements?referenceNo=" + count.path("referenceNo").asText(), manager);
        assertThat(adjustments.size()).isEqualTo(2);

        long batch = stock(f, f.a()).path("batchId").asLong();
        JsonNode stale = write("/stocktakes", manager, Map.of("locationId", f.a(), "batchId", batch), 200);
        String stalePath = "/stocktakes/" + stale.path("id").asLong();
        write(stalePath + "/count", manager, Map.of("countedQty", 0), 200);
        transfer(batch, f.a(), f.b(), 1);
        transfer(batch, f.b(), f.a(), 1);
        write(stalePath + "/approve", manager, null, 409);
        write(stalePath + "/recount", manager, Map.of("reason", "Ledger moved"), 200);
        assertThat(write(stalePath + "/refresh", manager, null, 200).path("status").asText()).isEqualTo("DRAFT");

        JsonNode damage = write("/stocktakes/quarantine", staff, Map.of("batchId", batch, "fromLocationId", f.a(),
                "quantity", 2, "reason", "Damaged package"), 200);
        String quarantine = "/stocktakes/quarantine/" + damage.path("id").asLong();
        write(quarantine + "/approve", manager, Map.of("toLocationId", f.q()), 200);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(18);
        write(quarantine + "/post", staff, null, 200);
        write(quarantine + "/post", staff, null, 200);
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(16);
        assertThat(stock(f, f.q()).path("qtyAvailable").asInt()).isZero();
        assertThat(get("/inventory/summary?sku=" + f.sku(), staff).path("qtyOnHand").asInt()).isEqualTo(25);
        assertThat(get("/movements?referenceNo=" + damage.path("referenceNo").asText(), manager).size()).isEqualTo(1);
    }

    @Test
    void reportsIncludeWholeBusinessDayAndNetSameWarehouseTransferToZero() throws Exception {
        Fixture f = fixture(false);
        clock.set(DAY.minusDays(1).atTime(23, 59, 59));
        receiveAndPost(f, f.a(), "A", 20, null);
        clock.set(DAY.atStartOfDay());
        receiveAndPost(f, f.a(), "A", 2, null);
        long batch = stock(f, f.a()).path("batchId").asLong();
        transfer(batch, f.a(), f.b(), 4);
        clock.set(DAY.atTime(23, 59, 59));
        var snapshot = write("/stocktakes", manager, Map.of("locationId", f.a(), "batchId", batch), 200);
        String path = "/stocktakes/" + snapshot.path("id").asLong();
        write(path + "/count", manager, Map.of("countedQty", 15, "reason", "Cycle count"), 200);
        write(path + "/approve", manager, null, 200);
        clock.set(DAY.plusDays(1).atStartOfDay());
        receiveAndPost(f, f.a(), "A", 5, null);
        String query = "/reports/stock-summary?from=" + DAY + "&to=" + DAY + "&warehouseName=" + f.sku();
        JsonNode report = get(query, manager).get(0);
        assertThat(report.path("openingQty").asInt()).isEqualTo(20);
        assertThat(report.path("inboundQty").asInt()).isEqualTo(2);
        assertThat(report.path("netAdjustment").asInt()).isEqualTo(-3);
        assertThat(report.path("closingQty").asInt()).isEqualTo(19);
        call(HttpMethod.GET, query, staff, null, 403);
        call(HttpMethod.GET, "/batches/" + batch + "/trace", staff, null, 403);
    }

    @Test
    void orderHistoryAndSupportAreScopedToCustomer() throws Exception {
        Fixture f = fixture(false);
        long id = newOrder(f, 3);
        String order = OUT + "/" + id;
        call(HttpMethod.GET, order, customer2, null, 404);
        JsonNode available = get(OUT + "?from=" + DAY + "&to=" + DAY + "&status=REQUESTED", customer);
        assertThat(names(available, "id")).contains(Long.toString(id));
        assertThat(names(get(OUT + "?from=" + DAY + "&to=" + DAY + "&status=REQUESTED", customer2), "id"))
                .doesNotContain(Long.toString(id));

        write("/support", customer, Map.of("outboundOrderId", id, "type", "MISSING_ITEMS", "content", "  "), 400);
        var ticket = write("/support", customer, Map.of("outboundOrderId", id, "type", "MISSING_ITEMS",
                "content", "Expected two packages"), 200);
        String path = "/support/" + ticket.path("id").asLong();
        call(HttpMethod.GET, path, customer2, null, 404);
        write(path + "/review", manager, Map.of("status", "IN_PROGRESS"), 200);
        write(path + "/review", manager, Map.of("status", "RESOLVED", "response", " "), 400);
        write(path + "/review", manager, Map.of("status", "RESOLVED", "response", "Checked the delivery notes"), 200);
        JsonNode history = get(path, customer);
        assertThat(history.path("response").asText()).isEqualTo("Checked the delivery notes");
        assertThat(names(history.path("history"), "status")).containsExactly("NEW", "IN_PROGRESS", "RESOLVED");
    }


    @Test
    void receivingValidatesPhysicalCountsExpiryAndLotBeforeConfirmation() throws Exception {
        Fixture f = fixture(true);
        JsonNode doc = write(IN, staff, Map.of("supplierId", supplierId, "externalReference", f.sku(),
                "items", List.of(Map.of("productId", f.product(), "qtyOrdered", 50, "unitCost", 12000))), 200);
        String path = IN + "/" + doc.path("id").asLong();
        Map<String, Object> counts = new java.util.HashMap<>(Map.of("inboundItemId", doc.path("items").get(0).path("id").asLong(),
                "qtyReceived", 48, "qtyAccepted", 49, "locationId", f.a(), "itemCondition", "DAMAGED"));
        write(path + "/receive", staff, Map.of("items", List.of(counts)), 400);
        counts.put("qtyAccepted", 45);
        write(path + "/receive", staff, Map.of("items", List.of(counts)), 400);
        counts.put("expiryDate", DAY);
        counts.put("mfgDate", DAY.plusDays(1));
        write(path + "/receive", staff, Map.of("items", List.of(counts)), 400);
        counts.put("mfgDate", DAY.minusDays(8));
        var line = write(path + "/receive", staff, Map.of("items", List.of(counts)), 200).path("items").get(0);
        assertThat(line.path("qtyRejected").asInt()).isEqualTo(3);
        assertThat(line.path("qtyMissing").asInt()).isEqualTo(2);
        assertThat(line.path("qtyAccepted").asInt()).isEqualTo(45);
        write(path + "/confirm", manager, null, 400);
        assertThat(get(path, staff).path("status").asText()).isEqualTo("PENDING_CONFIRMATION");
        assertThat(stock(f, f.a())).isNull();
    }

    @Test
    void pendingQueueOrdersDeadlineThenRankThenCode() throws Exception {
        Fixture f = fixture(false);
        put("/products/priorities", manager, Map.of("ranks", Map.of("URGENT", 1, "EXPRESS", 2, "NORMAL", 3)), 200);
        JsonNode normal = orderAt(f, "NORMAL", NOW.plusDays(1), 200);
        JsonNode urgentA = orderAt(f, "URGENT", NOW.plusDays(1), 200);
        JsonNode urgentB = orderAt(f, "URGENT", NOW.plusDays(1), 200);
        JsonNode early = orderAt(f, "NORMAL", NOW.plusHours(1), 200);
        JsonNode noDeadline = orderAt(f, "URGENT", null, 200);
        orderAt(f, "URGENT", NOW.minusMinutes(1), 400);
        var ids = List.of(normal, urgentA, urgentB, early, noDeadline).stream().map(row -> row.path("orderNo").asText()).toList();
        var actual = names(get(OUT + "?status=REQUESTED", staff), "orderNo").stream().filter(ids::contains).toList();
        var ties = List.of(urgentA.path("orderNo").asText(), urgentB.path("orderNo").asText()).stream().sorted().toList();
        assertThat(actual).containsExactly(early.path("orderNo").asText(), ties.get(0), ties.get(1),
                normal.path("orderNo").asText(), noDeadline.path("orderNo").asText());
    }

    @Test
    void pickedCancellationAndQuarantineCannotConsumeReservedStock() throws Exception {
        Fixture f = fixture(false);
        receiveAndPost(f, f.a(), "A", 10, null);
        long orderId = preparedOrder(f, 8);
        long batch = stock(f, f.a()).path("batchId").asLong();
        var count = write("/stocktakes", manager, Map.of("locationId", f.a(), "batchId", batch), 200);
        String countPath = "/stocktakes/" + count.path("id").asLong();
        write(countPath + "/count", manager, Map.of("countedQty", 7), 200);
        write(countPath + "/approve", manager, null, 409);
        var request = write("/stocktakes/quarantine", staff, Map.of("batchId", batch, "fromLocationId", f.a(),
                "quantity", 3, "reason", "Three damaged"), 200);
        write("/stocktakes/quarantine/" + request.path("id").asLong() + "/approve", manager,
                Map.of("toLocationId", f.q()), 409);

        String path = OUT + "/" + orderId + "/cancel";
        assertThat(write(path, manager, Map.of("reason", "Cancelled"), 200).path("status").asText())
                .isEqualTo("CANCEL_PENDING_RETURN");
        assertThat(stock(f, f.a()).path("qtyReserved").asInt()).isEqualTo(8);
        write(path, manager, Map.of("reason", "Returned to floor", "returnedToStorage", true), 200);
        assertThat(stock(f, f.a()).path("qtyReserved").asInt()).isZero();
        assertThat(stock(f, f.a()).path("qtyOnHand").asInt()).isEqualTo(10);
        assertThat(stock(f, f.q())).isNull();
    }


    @Test
    void placementChecksOtherBinsInZoneAndOrdersKeepTheirStorageRequirement() throws Exception {
        Fixture incoming = fixture(false);
        Fixture incompatible = fixture(false);
        String profile = incoming.sku();
        put("/products/storage-profiles/" + profile, manager,
                Map.of("temperatureMin", 2, "temperatureMax", 8, "avoidLight", true, "incompatibleGroups", List.of("X")), 200);
        put("/products/" + incoming.product(), manager, productData(incoming.sku(), profile, null), 200);
        put("/products/" + incompatible.product(), manager, productData(incompatible.sku(), profile, "X"), 200);

        long b = storageLocation(incoming.sku(), "B", "B-01", profile, false);
        storageLocation(incoming.sku(), "B", "B-02", profile, true);
        storageLocation(incoming.sku(), "C", "C-01", profile, false);
        long neighbor = storageLocation(incoming.sku(), "C", "C-02", profile, false);
        receiveAndPost(incompatible, neighbor, "X", 4, null);
        var suggestions = get("/locations/suggestions?productId=" + incoming.product(), staff);
        assertThat(names(suggestions, "id")).containsExactly(Long.toString(b));

        receiveAndPost(incoming, b, "MATCH", 5, null);
        long order = newOrder(incoming, 2);
        put("/products/" + incoming.product(), manager, productData(incoming.sku(), incoming.profile(), null), 200);
        receiveAndPost(incoming, incoming.a(), "A-DIFFERENT-PROFILE", 5, null);
        JsonNode orderDetail = get(OUT + "/" + order, customer);
        assertThat(orderDetail.path("items").get(0).path("storageCondition").asText()).isEqualTo(profile);
        assertThat(names(get(OUT + "/" + order + "/suggestions", staff).path("allocations"), "batchNo")).containsExactly("MATCH");
    }

    private long storageLocation(String warehouse, String zone, String code, String profile, boolean locked) throws Exception {
        var body = new java.util.HashMap<>(locationData(warehouse, code, false, locked, profile));
        body.put("zoneName", zone);
        return write("/locations", manager, body, 200).path("id").asLong();
    }

    private static Map<String, Object> productData(String sku, String profile, String group) {
        var product = new java.util.HashMap<String, Object>(Map.of("skuCode", sku, "name", sku, "category", "Samples",
                "unit", "box", "minStock", 0, "active", true, "storageCondition", profile));
        if (group != null) product.put("isolationGroup", group);
        return product;
    }

    private JsonNode orderAt(Fixture f, String priority, LocalDateTime deadline, int status) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>(Map.of("shippingAddress", "Delivery point",
                "priority", priority, "items", List.of(Map.of("productId", f.product(), "qtyRequested", 1))));
        if (deadline != null) body.put("deliveryDeadline", deadline);
        return write(OUT, customer, body, status);
    }

    private record Fixture(String sku, String profile, long product, long a, long b, long q) {}
    private record Receipt(long location, String batch, int quantity, LocalDate expiry) {}

    private Fixture fixture(boolean dated) throws Exception {
        String sku = "IT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(java.util.Locale.ROOT);
        String profile = dated ? "MAT" : "THUONG";
        long product = write("/products", manager, Map.of("skuCode", sku, "name", sku, "category", "Samples",
                "unit", "box", "hasExpiry", dated, "expiryAlertDays", 7, "minStock", 0, "storageCondition", profile,
                "active", true), 200).path("id").asLong();
        long a = write("/locations", manager, locationData(sku, "A", false, false, profile), 200).path("id").asLong();
        long b = write("/locations", manager, locationData(sku, "B", false, false, profile), 200).path("id").asLong();
        long q = write("/locations", manager, locationData(sku, "Q", true, false, profile), 200).path("id").asLong();
        return new Fixture(sku, profile, product, a, b, q);
    }

    private static Map<String, Object> locationData(String warehouse, String code, boolean quarantine, boolean locked, String profile) {
        return Map.of("warehouseName", warehouse, "zoneName", "Zone", "rackCode", "01", "code", code,
                "storageCondition", profile, "active", true, "locked", locked, "quarantine", quarantine);
    }

    private String login(String username, int expected) throws Exception {
        return write("/auth/login", null, Map.of("username", username, "password", password), expected).path("accessToken").asText();
    }

    private long user(String username, String role, Long supplier, String carrier) throws Exception {
        var body = new java.util.HashMap<String, Object>(Map.of("username", username, "fullName", username,
                "password", password, "role", role));
        if (supplier != null) body.put("supplierId", supplier);
        if (carrier != null) body.put("carrierName", carrier);
        return write("/users", admin, body, 200).path("id").asLong();
    }

    private JsonNode receive(Fixture f, List<Receipt> receipts) throws Exception {
        var planned = receipts.stream().map(line -> Map.of("productId", f.product(), "qtyOrdered", line.quantity(), "unitCost", 1000)).toList();
        JsonNode draft = write(IN, staff, Map.of("supplierId", supplierId, "externalReference", UUID.randomUUID().toString(),
                "items", planned), 200);
        List<Map<String, Object>> received = new ArrayList<>();
        for (int i = 0; i < receipts.size(); i++) {
            Receipt line = receipts.get(i);
            Map<String, Object> body = new java.util.HashMap<>(Map.of("inboundItemId", draft.path("items").get(i).path("id").asLong(),
                    "locationId", line.location(), "qtyReceived", line.quantity(), "qtyAccepted", line.quantity(),
                    "batchNo", line.batch(), "itemCondition", "GOOD"));
            if (line.expiry() != null) {
                body.put("mfgDate", line.expiry().minusDays(14));
                body.put("expiryDate", line.expiry());
            }
            received.add(body);
        }
        return write(IN + "/" + draft.path("id").asLong() + "/receive", staff, Map.of("items", received), 200);
    }

    private void receiveAndPost(Fixture f, long location, String lot, int quantity, LocalDate expiry) throws Exception {
        confirmAndPost(receive(f, List.of(new Receipt(location, lot, quantity, expiry))));
    }

    private void confirmAndPost(JsonNode prepared) throws Exception {
        String path = IN + "/" + prepared.path("id").asLong();
        write(path + "/confirm", manager, null, 200);
        var placements = StreamSupport.stream(prepared.path("items").spliterator(), false)
                .map(line -> placement(line, line.path("locationId").asLong())).toList();
        write(path + "/post", staff, Map.of("items", placements), 200);
    }

    private static Map<String, Object> placement(JsonNode line, long location) {
        return Map.of("inboundItemId", line.path("id").asLong(), "batchNo", line.path("batchNo").asText(), "locationId", location);
    }

    private JsonNode stock(Fixture f, long location) throws Exception {
        return get("/inventory?sku=" + f.sku() + "&locationId=" + location, staff).get(0);
    }

    private long newOrder(Fixture f, int quantity) throws Exception {
        return write(OUT, customer, Map.of("shippingAddress", "Delivery point", "receiverName", "Receiver",
                "receiverPhone", "0900000003", "priority", "NORMAL",
                "items", List.of(Map.of("productId", f.product(), "qtyRequested", quantity))), 200).path("id").asLong();
    }

    private int approvalStatus(long id) {
        try {
            return requestResult(HttpMethod.POST, OUT + "/" + id + "/approve", manager, null).getResponse().getStatus();
        } catch (Exception error) {
            throw new IllegalStateException(error);
        }
    }

    private static Map<String, Object> pick(JsonNode line, int quantity, long location) {
        return Map.of("outboundItemId", line.path("itemId").asLong(), "inventoryId", line.path("inventoryId").asLong(),
                "batchId", line.path("batchId").asLong(), "locationId", location, "qtyPicked", quantity);
    }

    private long preparedOrder(Fixture f, int quantity) throws Exception {
        long id = newOrder(f, quantity);
        String path = OUT + "/" + id;
        JsonNode line = write(path + "/approve", manager, null, 200).path("allocations").get(0);
        write(path + "/pick", staff, Map.of("items", List.of(pick(line, quantity, f.a()))), 200);
        write(path + "/pack", staff, Map.of("packageCount", 1), 200);
        write(path + "/issue-dispatch", staff, null, 200);
        return id;
    }

    private long createVehicle(String code, String carrier, boolean active) throws Exception {
        return write(TRIPS + "/vehicles", dispatcher, Map.of("carrierName", carrier, "code", code,
                "plateNumber", code, "active", active), 200).path("id").asLong();
    }

    private JsonNode openTrip(long id) throws Exception {
        return write(TRIPS, staff, Map.of("outboundOrderId", id, "carrierName", "VAN-CHUYEN",
                "pickupLocation", "Warehouse gate"), 200);
    }

    private static Map<String, Object> assignment(long driverId, long vehicle, LocalDateTime start, LocalDateTime end) {
        return Map.of("driverId", driverId, "vehicleId", vehicle, "startAt", start, "endAt", end);
    }

    private void transfer(long batch, long from, long to, int qty) throws Exception {
        write("/movements/transfer", staff, Map.of("batchId", batch, "fromLocationId", from, "toLocationId", to, "quantity", qty), 200);
    }

    private JsonNode get(String path, String token) throws Exception {
        return call(HttpMethod.GET, path, token, null, 200);
    }

    private JsonNode write(String path, String token, Object payload, int status) throws Exception {
        return call(HttpMethod.POST, path, token, payload, status);
    }

    private JsonNode put(String path, String token, Object payload, int status) throws Exception {
        return call(HttpMethod.PUT, path, token, payload, status);
    }

    private JsonNode call(HttpMethod method, String path, String token, Object payload, int expected) throws Exception {
        var response = requestResult(method, path, token, payload).getResponse();
        JsonNode tree = json.readTree(response.getContentAsByteArray());
        assertThat(response.getStatus()).as("%s %s (%s)", method, path, tree == null ? "" : tree.path("error")).isEqualTo(expected);
        return tree;
    }

    private MvcResult requestResult(HttpMethod method, String path, String token, Object payload) throws Exception {
        var request = request(method, "/api/v1" + path).accept(MediaType.APPLICATION_JSON);
        if (token != null) request.header("Authorization", "Bearer " + token);
        if (payload != null) request.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(payload));
        return mvc.perform(request).andReturn();
    }

    private static JsonNode find(JsonNode array, String field, String key) {
        return StreamSupport.stream(array.spliterator(), false).filter(row -> key.equals(row.path(field).asText()))
                .findFirst().orElseThrow();
    }

    private static List<String> names(JsonNode arr, String key) {
        return StreamSupport.stream(arr.spliterator(), false).map(row -> row.path(key).asText()).toList();
    }

    private static List<String> namesForSku(JsonNode arr, String sku) {
        return StreamSupport.stream(arr.spliterator(), false).filter(row -> sku.equals(row.path("skuCode").asText()))
                .map(row -> row.path("batchNo").asText()).toList();
    }

    @TestConfiguration
    static class TestTime {
        @Bean
        @Primary
        AdjustableClock adjustableClock() {
            return new AdjustableClock();
        }
    }

    static class AdjustableClock extends Clock {
        private final ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        private volatile Instant instant = NOW.atZone(zone).toInstant();

        void set(LocalDateTime local) {
            instant = local.atZone(zone).toInstant();
        }

        @Override public ZoneId getZone() { return zone; }
        @Override public Clock withZone(ZoneId other) { return Clock.fixed(instant, other); }
        @Override public Instant instant() { return instant; }
    }
}
