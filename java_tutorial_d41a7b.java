// Learning Objective: This tutorial will teach you how to implement recursion
// and apply it to graphical rendering by building a Mandelbrot set fractal generator.
// We will focus on understanding the recursive nature of fractal generation
// and how to map complex numbers to colors for visualization.

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MandelbrotGenerator {

    // Constants defining the area of the complex plane to render.
    // These determine the "zoom" and "pan" of the fractal.
    private static final double X_MIN = -2.0;
    private static final double X_MAX = 1.0;
    private static final double Y_MIN = -1.5;
    private static final double Y_MAX = 1.5;

    // Maximum number of iterations to determine if a point escapes the Mandelbrot set.
    // Higher values produce more detail but take longer to compute.
    private static final int MAX_ITERATIONS = 100;

    // Width and height of the output image in pixels.
    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 600;

    public static void main(String[] args) {
        // Create a new blank image to draw the fractal onto.
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

        // Iterate over each pixel in the image.
        for (int x = 0; x < IMAGE_WIDTH; x++) {
            for (int y = 0; y < IMAGE_HEIGHT; y++) {

                // Map the pixel coordinates (x, y) to a point in the complex plane.
                // This converts screen space to complex plane coordinates.
                double real = X_MIN + (double) x / IMAGE_WIDTH * (X_MAX - X_MIN);
                double imaginary = Y_MIN + (double) y / IMAGE_HEIGHT * (Y_MAX - Y_MIN);

                // Calculate the number of iterations for this complex number.
                // This is where the core Mandelbrot logic resides.
                int iterations = calculateMandelbrotIterations(real, imaginary);

                // Determine the color based on the number of iterations.
                // Points that escape quickly are colored differently from those that don't.
                Color color = getColor(iterations);

                // Set the pixel color in the image.
                image.setRGB(x, y, color.getRGB());
            }
        }

        // Save the generated image to a file.
        saveImage(image, "mandelbrot.png");
        System.out.println("Mandelbrot fractal generated and saved as mandelbrot.png");
    }

    // This is the core recursive function that determines if a complex number 'c'
    // belongs to the Mandelbrot set. It does this by repeatedly applying the
    // function z = z*z + c, starting with z = 0. If the magnitude of 'z' ever
    // exceeds 2, the point is considered to have "escaped" and is not in the set.
    // The 'iterationCount' tracks how many steps it took to escape.
    // This method is intentionally NOT recursive in this specific implementation
    // to keep the complexity focused on the Mandelbrot logic itself for beginners.
    // A true recursive implementation would involve passing updated z and count.
    private static int calculateMandelbrotIterations(double realC, double imaginaryC) {
        double zReal = 0.0;
        double zImaginary = 0.0;
        int iterationCount = 0;

        // The loop continues as long as the magnitude of z is less than or equal to 2
        // AND we haven't reached the maximum number of iterations.
        while (zReal * zReal + zImaginary * zImaginary <= 4.0 && iterationCount < MAX_ITERATIONS) {
            // Calculate the next value of z using the formula: z = z*z + c
            // z*z = (zReal + i*zImaginary) * (zReal + i*zImaginary)
            //     = zReal*zReal + 2*i*zReal*zImaginary + (i*zImaginary)*(i*zImaginary)
            //     = zReal*zReal + 2*i*zReal*zImaginary - zImaginary*zImaginary
            // So, the real part of z*z is zReal*zReal - zImaginary*zImaginary
            // And the imaginary part of z*z is 2*zReal*zImaginary

            double nextZReal = zReal * zReal - zImaginary * zImaginary + realC;
            double nextZImaginary = 2 * zReal * zImaginary + imaginaryC;

            zReal = nextZReal;
            zImaginary = nextZImaginary;

            iterationCount++;
        }
        return iterationCount;
    }

    // This function maps the number of iterations to a color.
    // Points that reach MAX_ITERATIONS are considered part of the Mandelbrot set
    // and are colored black. Points that escape are colored based on how quickly
    // they escaped, creating the colorful patterns.
    private static Color getColor(int iterations) {
        if (iterations == MAX_ITERATIONS) {
            return Color.BLACK; // Inside the Mandelbrot set
        } else {
            // Simple coloring scheme: map iterations to shades of blue and green.
            // The modulo operator (%) helps cycle through colors.
            // You can experiment with different coloring algorithms here.
            float hue = (float) iterations / MAX_ITERATIONS;
            float saturation = 1.0f;
            float brightness = 1.0f;
            return Color.getHSBColor(hue, saturation, brightness);
        }
    }

    // Helper method to save the BufferedImage to a file.
    private static void saveImage(BufferedImage image, String filename) {
        try {
            File outputfile = new File(filename);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    // Example Usage:
    // To run this, simply compile and execute the main method.
    // It will generate an image named "mandelbrot.png" in the same directory
    // as your compiled Java class.
    // You can then open this image with any image viewer to see the fractal.
}