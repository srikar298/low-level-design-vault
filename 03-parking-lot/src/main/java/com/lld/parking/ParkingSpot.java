package com.lld.parking;

public class ParkingSpot {
    private final String spotId;
    private final SpotType spotType;
    private boolean occupied;
    private Vehicle currentVehicle;

    public ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.occupied = false;
    }

    public synchronized boolean assignVehicle(Vehicle vehicle) {
        if (occupied) {
            return false;
        }
        this.currentVehicle = vehicle;
        this.occupied = true;
        return true;
    }

    public synchronized void release() {
        this.currentVehicle = null;
        this.occupied = false;
    }

    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
    public synchronized boolean isOccupied() { return occupied; }
    public synchronized Vehicle getCurrentVehicle() { return currentVehicle; }
}
