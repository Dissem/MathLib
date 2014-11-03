package ch.dissem.libraries.math;

import java.text.DecimalFormat;

/**
 * Created by chris on 03.11.14.
 */
public class Quaternion {
    private final static DecimalFormat FORMAT = new DecimalFormat("#,##0.00");

    public static final double DELTA = 0.0000000001;
    public final double w, x, y, z;

    public static final Quaternion ZERO = new Quaternion(0, 0, 0, 0);

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion add(Quaternion other) {
        return new Quaternion(w + other.w, x + other.x, y + other.y, z + other.z);
    }

    public Quaternion subtract(Quaternion other) {
        return new Quaternion(w - other.w, x - other.x, y - other.y, z - other.z);
    }

    public Quaternion multiply(Quaternion by) {
        Quaternion a = this;
        Quaternion b = by;
        double w1 = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;
        double x1 = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y;
        double y1 = a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x;
        double z1 = a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w;
        return new Quaternion(w1, x1, y1, z1);
    }

    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    protected double normalSquare() {
        return w * w + x * x + y * y + z * z;
    }

    public double normal() {
        return Math.sqrt(normalSquare());
    }

    public Quaternion reciprocal() {
        double n = normalSquare();
        return new Quaternion(w / n, -x / n, -y / n, -z / n);
    }

    public Quaternion divide(Quaternion by) {
        return multiply(by.reciprocal());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Quaternion && equals((Quaternion) obj, DELTA);
    }

    public boolean equals(Quaternion other, double delta) {
        Quaternion d = this.subtract(other);
        return Math.abs(d.w) < delta && Math.abs(d.x) < delta && Math.abs(d.y) < delta && Math.abs(d.z) < delta;
    }

    @Override
    public String toString() {
        return String.format("%.2f%+.2fi%+.2fj%+.2fk", w, x, y, z).replaceAll("\\+", " + ").replaceAll("\\-", " - ").trim();
    }

    public static Quaternion H(double h, double hi, double hj, double hk) {
        return new Quaternion(h, hi, hj, hk);
    }

    public static Quaternion H(double x, double y, double z) {
        return new Quaternion(0, x, y, z);
    }

    public static Quaternion H(double scale) {
        return new Quaternion(scale, 0, 0, 0);
    }
}
