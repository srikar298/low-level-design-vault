package com.lld.parking;

import java.time.Instant;
import java.util.UUID;

public class ParkingTicket {
    private final String ticketId;
    private final String spotId;
    private final String licensePlate;
    private final Instant entryTime;
    private Instant exitTime;
    private double fee;

    public ParkingTicket(String spotId, String licensePlate) {
        this.ticketId = UUID.randomUUID().toString();
        this.spotId = spotId;
        this.licensePlate = licensePlate;
        this.entryTime = Instant.now();
    }

    public String getTicketId() { return ticketId; }
    public String getSpotId() { return spotId; }
    public String getLicensePlate() { return licensePlate; }
    public Instant getEntryTime() { return entryTime; }
    public Instant getExitTime() { return exitTime; }
    public void setExitTime(Instant exitTime) { this.exitTime = exitTime; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
}
