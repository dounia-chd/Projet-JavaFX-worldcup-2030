package ma.mondial2030.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modèle représentant un match/événement
 */
public class MatchEvent {
    public enum Status {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }

    private int id;
    private String matchName;
    private LocalDateTime matchDate;
    private String venue;
    private String teamA;
    private String teamB;
    private int totalCapacity;
    private int availableTickets;
    private BigDecimal ticketPrice;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MatchEvent() {}

    public MatchEvent(String matchName, LocalDateTime matchDate, String venue, 
                     String teamA, String teamB, int totalCapacity, BigDecimal ticketPrice) {
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.venue = venue;
        this.teamA = teamA;
        this.teamB = teamB;
        this.totalCapacity = totalCapacity;
        this.availableTickets = totalCapacity;
        this.ticketPrice = ticketPrice;
        this.status = Status.UPCOMING;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getMatchDisplay() {
        return teamA + " vs " + teamB;
    }

    @Override
    public String toString() {
        return matchName + " - " + getMatchDisplay();
    }
}
