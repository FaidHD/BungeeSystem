package de.netdown.bungeesystem.utils;

public class Reason {

    private String reason;
    private int seconds;
    private int id;
    private int points;
    private ReasonType reasonType;
    private boolean canSupBan;

    public Reason(String reason, int seconds, int id, int points, ReasonType reasonType, boolean canSupBan) {
        this.reason = reason;
        this.seconds = seconds;
        this.id = id;
        this.points = points;
        this.reasonType = reasonType;
        this.canSupBan = canSupBan;
    }

    public int getHalfTime() {
        return seconds/2;
    }

    public String getReason() {
        return reason;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getId() {
        return id;
    }

    public boolean isCanSupBan() {
        return canSupBan;
    }

    public ReasonType getReasonType() {
        return reasonType;
    }

    public int getPoints() {
        return points;
    }

    public enum ReasonType {
        BAN("BAN"), MUTE("MUTE");

        private String type;

        ReasonType(String type) {
            this.type = type;
        }
    }

}
