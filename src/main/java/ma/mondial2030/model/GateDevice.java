package ma.mondial2030.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant un dispositif de porte/barrière
 */
public class GateDevice {
    public enum Type {
        ENTRANCE, EXIT, VIP
    }

    private int id;
    private String deviceName;
    private String deviceLocation;
    private Type deviceType;
    private boolean isActive;
    private LocalDateTime lastSync;
    private LocalDateTime createdAt;

    public GateDevice() {}

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(String deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    public Type getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Type deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastSync() {
        return lastSync;
    }

    public void setLastSync(LocalDateTime lastSync) {
        this.lastSync = lastSync;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return deviceName + " (" + deviceLocation + ")";
    }
}
