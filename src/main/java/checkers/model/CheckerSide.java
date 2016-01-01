package checkers.model;

public enum CheckerSide {
    BLACK,
    RED;

    public boolean equalsType(CheckerSide other) {
        return equals(other);
    }
}
