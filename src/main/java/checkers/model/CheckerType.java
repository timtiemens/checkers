package checkers.model;

public enum CheckerType
{
    REGULAR,
    KING;

    public boolean equalsType(CheckerType other) {
        return equals(other);
    }
}