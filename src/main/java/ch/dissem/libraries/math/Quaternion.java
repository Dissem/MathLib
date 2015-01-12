package ch.dissem.libraries.math;

import java.util.Locale;

import static ch.dissem.libraries.math.Vector.V;
import static java.lang.Math.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Quaternion">Quaternions
 * as described by William Rowan Hamilton</a>.
 * <p>
 * To save you some writing there are helper methods H to create Quaternion
 * representations of cartesian coordinates, rotations, scalars and of course
 * full Quaternions.
 * <p>
 * Created by Christian Basler on 03.11.2014.
 */
public class Quaternion {
    public static final double DELTA = 0.00000000000001;
    public final double w, x, y, z;

    public static final Quaternion ZERO = new Quaternion(0, 0, 0, 0);
    public static final Quaternion IDENTITY = new Quaternion(1, 0, 0, 0);

    /**
     * The constructor is private, please use the helper methods {@link #H}
     * to create new Quaternions.
     */
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

    /**
     * Returns the squared norm, i.e. w^2 + x^2 + y^2 + z^2.
     */
    protected double normSquared() {
        return w * w + x * x + y * y + z * z;
    }

    /**
     * Returns the norm. Note that in some sources, the square of this value is called the norm.
     */
    public double norm() {
        return sqrt(normSquared());
    }

    /**
     * Returns q / q.norm()
     */
    public Quaternion normalize() {
        double n = norm();
        return new Quaternion(w / n, x / n, y / n, z / n);
    }

    /**
     * Returns q^-1
     */
    public Quaternion reciprocal() {
        double n = normSquared();
        return new Quaternion(w / n, -x / n, -y / n, -z / n);
    }

    public Quaternion divide(Quaternion by) {
        return multiply(by.reciprocal());
    }

    /**
     * This method assumes that this object represents a vector, and rotation is normalized.
     * It applies the rotation and returns the result.
     */
    public Quaternion rotate(Quaternion rotation) {
        return rotation.multiply(this).multiply(rotation.conjugate());
    }

    /**
     * This method assumes that this object represents a vector, and rotation is normalized.
     * It applies the rotation backwards and returns the result.
     */
    public Quaternion rotateBack(Quaternion rotation) {
        return rotation.conjugate().multiply(this).multiply(rotation);
    }

    /**
     * Convenience method to rotate around axis (x, y, z) with angle theta.
     */
    public Quaternion rotate(double theta, double x, double y, double z) {
        double n = sqrt(x * x + y * y + z * z);
        double sinFactor = sin(theta / 2) / n;
        Quaternion q = new Quaternion(cos(theta / 2), x * sinFactor, y * sinFactor, z * sinFactor);
        return rotate(q);
    }

    /**
     * Returns e^q
     */
    public Quaternion exp() {
        Quaternion im = getIm();
        double imNorm = im.norm();
        return H(Math.exp(w)).multiply(H(cos(imNorm)).add(im.normalize().multiply(H(sin(imNorm)))));
    }

    /**
     * Returns ln q
     */
    public Quaternion ln() {
        double norm = norm();
        return H(Math.log(norm)).add(getIm().normalize().multiply(H(acos(w / norm))));
    }

    /**
     * Returns the dot product, normally used with vectors.
     */
    public Quaternion dot(Quaternion other) {
        return new Quaternion(w * other.w + x * other.x + y * other.y + z * other.z,
                0, 0, 0);
    }

    /**
     * Returns the cross product, normally used with vectors.
     */
    public Quaternion cross(Quaternion other) {
        return new Quaternion(0,
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    /**
     * Returns the real part of this quaternion.
     */
    public Quaternion getRe() {
        return new Quaternion(w, 0, 0, 0);
    }

    /**
     * Returns the imaginary or vector part of this quaternion.
     */
    public Quaternion getIm() {
        return new Quaternion(0, x, y, z);
    }

    /**
     * Returns the vector represented by this quaternion.
     * A possible real part is just ignored.
     */
    public Vector getVector() {
        return new Vector(x, y, z);
    }

    /**
     * Assumes this quaternion represents a rotation and returns its angle.
     */
    public double getRotationAngle() {
        return 2 * acos(w / norm());
    }

    /**
     * Assumes this quaternion represents a rotation and returns its axis.
     */
    public Quaternion getRotationAxis() {
        return getIm().normalize();
    }

    /**
     * Assumes this quaternion represents a rotation and returns a rotation
     * around the same axis but with a scaled angle. (a' = a * scale)
     */
    public Quaternion getScaledRotation(double scale) {
        double n = sqrt(x * x + y * y + z * z);
        if (n == 0) {
            // Let's just assume it is a rotation, then it must be the Identity:
            return IDENTITY;
        }

        double halfAngle = acos(w / norm()) * scale;
        double cosFactor = cos(halfAngle);
        double sinFactor = sin(halfAngle) / n;
        return new Quaternion(cosFactor, sinFactor * x, sinFactor * y, sinFactor * z);
    }

    /**
     * Returns an array [roll, pitch, yaw] i.e. the rotation about x, y and z axis.
     * Assumes this quaternion represents a rotation.
     */
    public double[] getEulerAngles() {
        double[] euler = new double[3];
        euler[0] = atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y));
        euler[1] = asin(2 * (w * y - z * x));
        euler[2] = atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));
        return euler;
    }

    /**
     * Returns true if the length difference between this and the other
     * quaternion is smaller than {@link #DELTA}. This is used due to the
     * fact that you can't rely on doubles that should be equal to actually
     * be equal.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Quaternion && equals((Quaternion) obj, DELTA);
    }

    /**
     * It's a bad idea to put real numbers into hashed sets or maps, but to
     * prevent any catastrophic failure due to the weird {@link #equals(Object)}
     * implementation, we'll just have to accept some hash collisions.
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /**
     * Returns true if the length difference between this and the other
     * quaternion is smaller than delta.
     */
    public boolean equals(Quaternion other, double delta) {
        return this.subtract(other).normSquared() < delta;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.2f%+.2fi%+.2fj%+.2fk", w, x, y, z).replaceAll("\\+", " + ").replaceAll("\\-", " - ").trim();
    }

    /**
     * Returns the rotation Quaternion that translates from q1 to q2.
     */
    public static Quaternion getRotation(Quaternion q1, Quaternion q2) {
        double angle = acos(q1.dot(q2).w / (q1.norm() * q2.norm()));
        if (angle == 0) {
            return IDENTITY;
        }
        Quaternion axis = q1.cross(q2).normalize();
        return H(angle, axis);
    }

    public static Quaternion H(double h, double hi, double hj, double hk) {
        return new Quaternion(h, hi, hj, hk);
    }

    /**
     * Convenience method for vectors that lets you omit the real part.
     */
    public static Quaternion H(double x, double y, double z) {
        return new Quaternion(0, x, y, z);
    }

    /**
     * Convenience method for real numbers.
     */
    public static Quaternion H(double scale) {
        return new Quaternion(scale, 0, 0, 0);
    }

    /**
     * Creates a rotation around the axis with the given angle.
     */
    public static Quaternion H(double angle, Vector axis) {
        axis = axis.normalize();
        double sinHalfAngle = sin(angle / 2);
        double cosHalfAngle = cos(angle / 2);
        return new Quaternion(cosHalfAngle, sinHalfAngle * axis.x, sinHalfAngle * axis.y, sinHalfAngle * axis.z);
    }

    /**
     * Assumes the axis quaternion is normal and pure imaginary.
     */
    public static Quaternion H(double angle, Quaternion axis) {
        double sinHalfAngle = sin(angle / 2);
        double cosHalfAngle = cos(angle / 2);
        return new Quaternion(cosHalfAngle, sinHalfAngle * axis.x, sinHalfAngle * axis.y, sinHalfAngle * axis.z);
    }

    /**
     * Convenience method to create a quaternion from a vector.
     */
    public static Quaternion H(Vector vector) {
        return new Quaternion(0, vector.x, vector.y, vector.z);
    }

    /**
     * Convenience method to create a quaternion from an array of doubles. It must be of either length 3 or 4.
     * Arrays of length 3 are assumed to be vector representations.
     */
    public static Quaternion H(double[] array) {
        if (array.length == 3) {
            return new Quaternion(0, array[0], array[1], array[2]);
        } else if (array.length == 4) {
            return new Quaternion(array[0], array[1], array[2], array[3]);
        } else {
            throw new IllegalArgumentException("Array with either 3 or 4 elements expected");
        }
    }

    /**
     * Convenience method to create a quaternion from an array of doubles. It must be of either length 3 or 4.
     * Arrays of length 3 are assumed to be vector representations.
     */
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
