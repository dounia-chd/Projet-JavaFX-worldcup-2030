package ma.mondial2030.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle représentant une accréditation
 */
public class Accreditation {
    public enum Type {
        MEDIA, STAFF, VIP, SECURITY
    }

    public enum Status {
        ACTIVE, EXPIRED, REVOKED
    }

    private int id;
    private User user;
    private Type accreditationType;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String accessLevels;
    private Status status;
    private User issuedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Accreditation() {}

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

    public Type getAccreditationType() {
        return accreditationType;
    }

    public void setAccreditationType(Type accreditationType) {
        this.accreditationType = accreditationType;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getAccessLevels() {
        return accessLevels;
    }

    public void setAccessLevels(String accessLevels) {
        this.accessLevels = accessLevels;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(User issuedBy) {
        this.issuedBy = issuedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isValid() {
        LocalDate now = LocalDate.now();
        return status == Status.ACTIVE && 
               now.isAfter(validFrom.minusDays(1)) && 
               now.isBefore(validUntil.plusDays(1));
    }
}
