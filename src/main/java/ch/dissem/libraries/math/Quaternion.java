package ch.dissem.libraries.math;

import java.text.DecimalFormat;
import java.util.Locale;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Quaternion">Quaternions
 * as described by William Rowan Hamilton</a>.
 * <p/>
 * To save you some writing there are helper methods H to create Quaternion
 * representations of cartesian coordinates, scalars and of course full
 * Quaternions.
 * <p/>
 * Created by Christian Basler on 03.11.14.
 */
public class Quaternion {
    public static final double DELTA = 0.00000000000001;
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

    protected double normSquared() {
        return w * w + x * x + y * y + z * z;
    }

    public double norm() {
        return sqrt(normSquared());
    }

    public Quaternion normalize() {
        double n = norm();
        return new Quaternion(w / n, x / n, y / n, z / n);
    }

    public Quaternion reciprocal() {
        double n = normSquared();
        return new Quaternion(w / n, -x / n, -y / n, -z / n);
    }

    public Quaternion divide(Quaternion by) {
        return multiply(by.reciprocal());
    }

    public Quaternion rotate(double theta, double x, double y, double z) {
        double n = sqrt(x * x + y * y + z * z);
        double sinFactor = sin(theta / 2) / n;
        Quaternion q = new Quaternion(cos(theta / 2), x * sinFactor, y * sinFactor, z * sinFactor);
        return q.multiply(this).multiply(q.reciprocal());
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
        return String.format(Locale.US, "%.2f%+.2fi%+.2fj%+.2fk", w, x, y, z).replaceAll("\\+", " + ").replaceAll("\\-", " - ").trim();
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
