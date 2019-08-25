package rossH.CD19.Scanner;

public class Key {
    public final char x;
    public final String y;

    public Key(final char x, String y ) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Key)) {
            return false;
        }

        final Key key = (Key) o;

        if (x != key.x) {
            return false;
        }
        if (y != key.y) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) x;
        result = 31 * result + y.hashCode();
        return result;
    }
}

