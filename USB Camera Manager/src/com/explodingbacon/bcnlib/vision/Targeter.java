package com.explodingbacon.bcnlib.vision;

public interface Targeter {

    /**
     * Returns a filtered version of an Image.
     *
     * @param i The Image.
     * @param args Additional arguments this function may require.
     * @return A filtered version of the Image.
     */
    Image filter(Image i, Object... args);

    /**
     * Checks if a Contour is valid and can be considered as a target.
     *
     * @param c The Contour to be checked.
     * @param args Additional arguments this function may require.
     * @return If the Contour is valid.
     */
    boolean isValid(Contour c, Object... args);

    /**
     * Finds the target in an Image and returns it as a Contour.
     *
     * @param i The Image.
     * @param args Additional arguments this function may require.
     * @return The target.
     */
    Contour findTarget(Image i, Object... args);

    /**
     * Adds visual indicators to an Image.
     *
     * @param i The Image.
     * @param args Additional arguments this function may require.
     */
    void addIndicators(Image i, Object... args);

}
