package com.explodingbacon.bcnlib.vision;

public class HSV extends BCNScalar {

    private int h, s, v;

    /**
     * Creates an HSV value set.
     *
     * @param h The Hue value.
     * @param s The Saturation value.
     * @param v The Value value.
     */
    public HSV(int h, int s, int v) {
        super(h, s, v);
        this.h = h;
        this.s = s;
        this.v = v;
    }

    public int getHue() {
        return h;
    }

    public int getSaturation() {
        return s;
    }

    public int getValue() {
        return v;
    }
}
