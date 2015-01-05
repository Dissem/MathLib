package ch.dissem.libraries.math;

import java.util.Locale;

import static java.lang.Math.sqrt;

/**
 * Created by chris on 08.12.14.
 */
public class Vector {
    public static final Vector UNIT_X = new Vector(1, 0, 0);
    public static final Vector UNIT_Y = new Vector(0, 1, 0);
    public static final Vector UNIT_Z = new Vector(0, 0, 1);

    public final double x, y, z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected double normSquared() {
        return x * x + y * y + z * z;
    }

    public double norm() {
        return sqrt(normSquared());
    }

    public Vector normalize() {
        double n = norm();
        return new Vector(x / n, y / n, z / n);
    }

    public double dot(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector cross(Vector other) {
        return new Vector(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public static Vector normed(Quaternion q) {
        double n = sqrt(q.x * q.x + q.y * q.y + q.z * q.z);
        return new Vector(q.x / n, q.y / n, q.z / n);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.2f/%.2f/%.2f)", x, y, z);
    }

    public static Vector V(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public static Vector V(Quaternion q) {
        return new Vector(q.x, q.y, q.z);
    }
}
