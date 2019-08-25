package rossH.CD19.Scanner;

public class Key<T, E> {
    private final T x;
    private final E y;

    public Pair(final T x, final E y) {
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

        final Key pair = (Key) o;

        if (x != pair.x) {
            return false;
        }
        if (y != pair.y) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

