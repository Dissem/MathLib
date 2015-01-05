package ch.dissem.libraries.math;

import java.util.Locale;

import static ch.dissem.libraries.math.Vector.V;
import static java.lang.Math.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Quaternion">Quaternions
 * as described by William Rowan Hamilton</a>.
 * <p/>
 * To save you some writing there are helper methods H to create Quaternion
 * representations of cartesian coordinates, rotations, scalars and of course
 * full Quaternions.
 * <p/>
 * Created by Christian Basler on 03.11.14.
 */
public class Quaternion {
    public static final double DELTA = 0.00000000000001;
    public final double w, x, y, z;

    public static final Quaternion ZERO = new Quaternion(0, 0, 0, 0);

    protected Quaternion(double w, double x, double y, double z) {
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

    public Quaternion rotate(Quaternion rotation) {
        return rotation.multiply(this).multiply(rotation.conjugate());
    }

    public Quaternion rotate(double theta, double x, double y, double z) {
        double n = sqrt(x * x + y * y + z * z);
        double sinFactor = sin(theta / 2) / n;
        Quaternion q = new Quaternion(cos(theta / 2), x * sinFactor, y * sinFactor, z * sinFactor);
        return rotate(q);
    }

    public Quaternion exp() {
        Quaternion im = getIm();
        double imNorm = im.norm();
        return H(Math.exp(w)).multiply(H(cos(imNorm)).add(im.normalize().multiply(H(sin(imNorm)))));
    }

    public Quaternion ln() {
        double norm = norm();
        return H(Math.log(norm)).add(getIm().normalize().multiply(H(acos(w / norm))));
    }

    public Quaternion dot(Quaternion other) {
        return new Quaternion(w * other.w + x * other.x + y * other.y + z * other.z,
                0, 0, 0);
    }

    public Quaternion cross(Quaternion other) {
        return new Quaternion(0,
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public Quaternion getRe() {
        return new Quaternion(w, 0, 0, 0);
    }

    public Quaternion getIm() {
        return new Quaternion(0, x, y, z);
    }

    public Vector getVector() {
        return new Vector(x, y, z);
    }

    public double getPhi() {
        return acos(w / norm());
    }

    public Quaternion getEpsilon() {
        Quaternion im = getIm();
        return im.divide(H(im.norm()));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Quaternion && equals((Quaternion) obj, DELTA);
    }

    public boolean equals(Quaternion other, double delta) {
        return this.subtract(other).normSquared() < delta;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.2f%+.2fi%+.2fj%+.2fk", w, x, y, z).replaceAll("\\+", " + ").replaceAll("\\-", " - ").trim();
    }

    public static Quaternion getRotation(Quaternion q1, Quaternion q2) {
        double angle = acos(q1.dot(q2).w / (q1.norm() * q2.norm()));
        if (angle == 0) {
            return H(1); // Identity
        }
        Quaternion axis = q1.cross(q2).normalize();
        return H(angle, axis);
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

    public static Quaternion H(double angle, Vector axis) {
        axis = axis.normalize();
        double sinHalfAngle = sin(angle / 2);
        double cosHalfAngle = cos(angle / 2);
        return new Quaternion(cosHalfAngle, sinHalfAngle * axis.x, sinHalfAngle * axis.y, sinHalfAngle * axis.z);
    }

    /**
     * Assumes the axis quaternion is normal and pure imaginary.
     *
     * @param angle
     * @param axis
     * @return
     */
    public static Quaternion H(double angle, Quaternion axis) {
        double sinHalfAngle = sin(angle / 2);
        double cosHalfAngle = cos(angle / 2);
        return new Quaternion(cosHalfAngle, sinHalfAngle * axis.x, sinHalfAngle * axis.y, sinHalfAngle * axis.z);
    }

    public static Quaternion H(Vector vector) {
        return new Quaternion(0, vector.x, vector.y, vector.z);
    }

    public static Quaternion H(double[] array) {
        if (array.length == 3) {
            return new Quaternion(0, array[0], array[1], array[2]);
        } else if (array.length == 4) {
            return new Quaternion(array[0], array[1], array[2], array[3]);
        } else {
            throw new IllegalArgumentException("Array with either 3 or 4 elements expected");
        }
    }

    public static Quaternion H(float[] array) {
        if (array.length == 3) {
            return new Quaternion(0, array[0], array[1], array[2]);
        } else if (array.length == 4) {
            return new Quaternion(array[0], array[1], array[2], array[3]);
        } else {
            throw new IllegalArgumentException("Array with either 3 or 4 elements expected");
        }
    }
}
