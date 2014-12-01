import ch.dissem.libraries.math.Quaternion;
import org.junit.Test;

import static ch.dissem.libraries.math.Quaternion.*;
import static java.lang.Math.PI;
import static org.junit.Assert.assertEquals;

/**
 * Created by Christian Basler on 03.11.14.
 */
public class QuaternionTest {
    @Test
    public void ensureQuaternionIsPrintedCorrectly() {
        Quaternion q = new Quaternion(1, 1, 1, 1);
        assertEquals("1.00 + 1.00i + 1.00j + 1.00k", q.toString());
    }

    @Test
    public void testAddition() {
        double w1 = 100 * Math.random();
        double x1 = 100 * Math.random();
        double y1 = 100 * Math.random();
        double z1 = 100 * Math.random();
        double w2 = 100 * Math.random();
        double x2 = 100 * Math.random();
        double y2 = 100 * Math.random();
        double z2 = 100 * Math.random();

        Quaternion q1 = H(w1, x1, y1, z1);
        Quaternion q2 = H(w2, x2, y2, z2);

        Quaternion sum = q1.add(q2);

        assertEquals(q2.add(q1), sum);

        double delta = DELTA;
        assertEquals(w1 + w2, sum.w, delta);
        assertEquals(x1 + x2, sum.x, delta);
        assertEquals(y1 + y2, sum.y, delta);
        assertEquals(z1 + z2, sum.z, delta);

        assertEquals(q1, q1.add(ZERO));
    }

    @Test
    public void testSubtraction() {
        double w1 = 100 * Math.random();
        double x1 = 100 * Math.random();
        double y1 = 100 * Math.random();
        double z1 = 100 * Math.random();
        double w2 = 100 * Math.random();
        double x2 = 100 * Math.random();
        double y2 = 100 * Math.random();
        double z2 = 100 * Math.random();

        Quaternion q1 = H(w1, x1, y1, z1);
        Quaternion q2 = H(w2, x2, y2, z2);

        Quaternion difference = q1.subtract(q2);

        assertEquals(q2.subtract(q1), ZERO.subtract(difference));

        double delta = Quaternion.DELTA;
        assertEquals(w1 - w2, difference.w, delta);
        assertEquals(x1 - x2, difference.x, delta);
        assertEquals(y1 - y2, difference.y, delta);
        assertEquals(z1 - z2, difference.z, delta);

        assertEquals(q1, q1.subtract(ZERO));
    }

    @Test
    public void testMultiplication() {
        Quaternion q1 = H(1, 2, 3, 4);
        Quaternion q2 = H(15, -4, -26, 0);

        assertEquals(ZERO, q1.multiply(ZERO));
        assertEquals(H(-28, 4, 6, 8), q1.multiply(q1));
        assertEquals(H(101, 130, 3, 20), q1.multiply(q2));

        assertEquals(H(2, 4, 6, 8), q1.multiply(H(2)));
    }

    @Test
    public void ensureNumberTimesReciprocalEqualsOne() {
        Quaternion q = getRandom();
        assertEquals(H(1), q.multiply(q.reciprocal()));
    }

    @Test
    public void testDivision() {
        Quaternion q1 = H(1, 2, 3, 4);
        Quaternion q2 = H(15, -4, -26, 0);

        assertEquals(q1, q1.divide(H(1)));
        assertEquals(H(0.5, 1, 1.5, 2), q1.divide(H(2)));

        Quaternion q3 = H(0, 0, 0, 1);
        assertEquals(q1, q1.divide(q3).multiply(q3));

        assertEquals(q1, q1.multiply(q2).divide(q2));
    }

    @Test
    public void testNormalization() {
        assertEquals(H(1, 0, 0, 0), H(2, 0, 0, 0).normalize());
        assertEquals(H(0, 1, 0, 0), H(0, 3, 0, 0).normalize());
        assertEquals(H(0, 0, 1, 0), H(0, 0, 4, 0).normalize());
        assertEquals(H(0, 0, 0, 1), H(0, 0, 0, 5).normalize());

        assertEquals(1, H(1, 2, 3, 4).normalize().norm(), DELTA);
    }

    @Test
    public void testRotation() {
        assertEquals(H(0, 1, 0, 0), H(0, 0, 1, 0).rotate(-PI / 2, 0, 0, 1));
        assertEquals(H(0, PI, 1, 2), H(0, -1, PI, 2).rotate(PI / 2, 0, 0, -2));
        assertEquals(H(0, 3, 1, 2), H(0, 1, 2, 3).rotate(2 * PI / 3, 1, 1, 1));
    }

    private Quaternion getRandom() {
        return H(100 * Math.random(), 100 * Math.random(), 100 * Math.random(), 100 * Math.random());
    }
}
