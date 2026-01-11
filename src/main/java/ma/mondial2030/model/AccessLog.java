package ma.mondial2030.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant un log d'accès
 */
public class AccessLog {
    public enum AccessType {
        TICKET, ACCREDITATION, BIOMETRIC
    }

    public enum AccessResult {
        GRANTED, DENIED
    }

    private int id;
    private User user;
    private Ticket ticket;
    private GateDevice gateDevice;
    private AccessType accessType;
    private AccessResult accessResult;
    private String denialReason;
    private LocalDateTime accessTimestamp;
    private String ipAddress;

    public AccessLog() {}

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public GateDevice getGateDevice() {
        return gateDevice;
    }

    public void setGateDevice(GateDevice gateDevice) {
        this.gateDevice = gateDevice;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public AccessResult getAccessResult() {
        return accessResult;
    }

    public void setAccessResult(AccessResult accessResult) {
        this.accessResult = accessResult;
    }

    public String getDenialReason() {
        return denialReason;
    }

    public void setDenialReason(String denialReason) {
        this.denialReason = denialReason;
    }

    public LocalDateTime getAccessTimestamp() {
        return accessTimestamp;
    }

    public void setAccessTimestamp(LocalDateTime accessTimestamp) {
        this.accessTimestamp = accessTimestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
