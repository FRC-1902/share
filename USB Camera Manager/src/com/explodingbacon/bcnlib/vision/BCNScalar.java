package com.explodingbacon.bcnlib.vision;

import org.opencv.core.Scalar;

public class BCNScalar {

    private int v1, v2, v3;

    protected BCNScalar(int v1, int v2, int v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    /**
     * Converts this to a Scalar. Mainly for internal use.
     *
     * @return A Scalar version of this.
     */
    public Scalar toScalar() {
        return new Scalar(v1, v2, v3);
    }

}
