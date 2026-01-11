package ma.mondial2030.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant un ticket
 */
public class Ticket {
    public enum Status {
        VALID, USED, CANCELLED, EXPIRED
    }

    private int id;
    private String ticketCode;
    private User user;
    private MatchEvent matchEvent;
    private String seatNumber;
    private String qrCodeData;
    private LocalDateTime purchaseDate;
    private Status status;

    public Ticket() {}

    public Ticket(String ticketCode, User user, MatchEvent matchEvent, String seatNumber) {
        this.ticketCode = ticketCode;
        this.user = user;
        this.matchEvent = matchEvent;
        this.seatNumber = seatNumber;
        this.status = Status.VALID;
        this.purchaseDate = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MatchEvent getMatchEvent() {
        return matchEvent;
    }

    public void setMatchEvent(MatchEvent matchEvent) {
        this.matchEvent = matchEvent;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isValid() {
        return status == Status.VALID;
    }

    @Override
    public String toString() {
        return ticketCode + " - " + (matchEvent != null ? matchEvent.getMatchDisplay() : "N/A");
    }
}
