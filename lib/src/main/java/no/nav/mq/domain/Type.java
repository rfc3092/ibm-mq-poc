package no.nav.mq.domain;

public enum Type {

    POSITIVE("positive"),
    NEGATIVE("negative");

    private final String text;

    Type(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Type fromString(String s) {
        for (Type type : Type.values()) {
            if (type.toString().equals(s)) {
                return type;
            }
        }
        return null;
    }

}
