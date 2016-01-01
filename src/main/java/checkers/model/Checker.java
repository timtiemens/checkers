package checkers.model;

//@MapKeyCapable -- i.e. is capable of being used as a key in a Map
public class Checker {
    // quick implementation of "id"
    private static int globalCheckerId = 1;


    private final int checkerId = globalCheckerId++;
    private CheckerType checkerType;
    private CheckerSide checkerSide;




    public Checker(CheckerType checkerType, CheckerSide checkerSide) {
        super();
        this.checkerType = checkerType;
        this.checkerSide = checkerSide;
    }

    public CheckerSide getSide() {
        return checkerSide;
    }

    public boolean isKing() {
        return CheckerType.KING.equalsType(checkerType);
    }

    public boolean isSide(CheckerSide querySide) {
        return checkerSide.equalsType(querySide);
    }



    @Override
    public String toString() {
        return "Checker [checkerId=" + checkerId + ", checkerType="
                + checkerType + ", checkerSide=" + checkerSide + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + checkerId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Checker other = (Checker) obj;
        if (checkerId != other.checkerId)
            return false;
        return true;
    }

    public boolean equalsType(Checker checker) {
        return equals(checker);
    }

    /**
     * @param s "b", "B", "r", "R" or "-"
     * @return newly-created Checker or null if s is "-"
     * @throws RuntimeException if input is invalid
     */
    public static Checker createFromSingleString(String s) {
        final Checker ret;
        final CheckerSide side;
        final CheckerType type;

        if (s.equals("b")) {
            side = CheckerSide.BLACK;
            type = CheckerType.REGULAR;
        } else if (s.equals("r")) {
            side = CheckerSide.RED;
            type = CheckerType.REGULAR;
        } else if (s.equals("B")) {
            side = CheckerSide.BLACK;
            type = CheckerType.KING;
        } else if (s.equals("R")) {
            side = CheckerSide.RED;
            type = CheckerType.KING;
        } else if (s.equals("-")) {
            side = null;
            type = null;
        } else {
            throw new RuntimeException("Cannot convert '" + s + "' to a piece");
        }

        if ((side != null) && (type != null)) {
            ret = new Checker(type, side);
        } else {
            ret = null;
        }

        return ret;
    }


}
