// Learning Objective: This tutorial demonstrates how to visualize the Mandelbrot set
// using Java's Swing framework and basic complex number arithmetic. We will focus on
// understanding the core iterative process that defines the Mandelbrot set.

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// The main class that sets up the window and draws the Mandelbrot set.
public class MandelbrotVisualizer extends JFrame {

    // Constants defining the size of our drawing area.
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    // The maximum number of iterations to determine if a point is in the set.
    private static final int MAX_ITERATIONS = 1000;

    // A BufferedImage to hold our pixel data. This is more efficient than drawing directly.
    private BufferedImage mandelbrotImage;

    // Constructor for our visualizer.
    public MandelbrotVisualizer() {
        // Set up the window title.
        setTitle("Mandelbrot Set Visualizer");
        // Set the default close operation to exit the application.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the initial size of the window.
        setSize(WIDTH, HEIGHT);
        // Center the window on the screen.
        setLocationRelativeTo(null);

        // Create a new BufferedImage. This is where we'll "paint" the Mandelbrot set.
        // TYPE_INT_RGB means each pixel will be represented by an integer representing its color.
        mandelbrotImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        // Call the method to calculate and draw the Mandelbrot set.
        drawMandelbrot();

        // Create a JLabel to display our image. JLabels can display icons, and a BufferedImage can be turned into an icon.
        JLabel label = new JLabel(new ImageIcon(mandelbrotImage));
        // Add the label (containing our image) to the frame's content pane.
        add(label);

        // Make the window visible.
        setVisible(true);
    }

    // This method calculates the Mandelbrot set and stores it in mandelbrotImage.
    private void drawMandelbrot() {
        // These define the boundaries of the complex plane we are interested in.
        // The Mandelbrot set is typically viewed in the region from -2.0 to 1.0 on the real axis,
        // and -1.5 to 1.5 on the imaginary axis.
        double realMin = -2.0;
        double realMax = 1.0;
        double imagMin = -1.5;
        double imagMax = 1.5;

        // Loop through each pixel in our image.
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                // Map the pixel coordinates (x, y) to coordinates in the complex plane.
                // 'x' maps to the real part, 'y' maps to the imaginary part.
                // We need to scale the pixel coordinates to fit within our chosen complex plane boundaries.
                double real = realMin + (x * (realMax - realMin) / WIDTH);
                double imag = imagMin + (y * (imagMax - imagMin) / HEIGHT);

                // Each point (real, imag) in the complex plane corresponds to a complex number 'c'.
                // The Mandelbrot set is defined by the behavior of the iteration: z = z^2 + c
                // starting with z = 0.
                // If the magnitude of 'z' stays bounded (doesn't go to infinity), the point 'c'
                // is considered to be in the Mandelbrot set.

                // Initialize 'z' to 0 (0 + 0i).
                double zReal = 0.0;
                double zImag = 0.0;

                // Keep track of the number of iterations.
                int iterations = 0;

                // The core iteration loop for checking if 'c' is in the Mandelbrot set.
                // We continue as long as the magnitude squared of 'z' is less than 4 (a common threshold for divergence)
                // and we haven't reached our maximum iteration limit.
                // Checking magnitude squared (|z|^2 = zReal^2 + zImag^2) is computationally cheaper than calculating the square root.
                while (zReal * zReal + zImag * zImag < 4.0 && iterations < MAX_ITERATIONS) {
                    // Calculate the next value of 'z' using the formula: z_new = z_old^2 + c
                    // z_old^2 = (zReal + zImag*i)^2 = zReal^2 + 2*zReal*zImag*i + (zImag*i)^2
                    // z_old^2 = zReal^2 + 2*zReal*zImag*i - zImag^2
                    // So, the real part of z_old^2 is zReal^2 - zImag^2
                    // And the imaginary part of z_old^2 is 2*zReal*zImag

                    double tempReal = zReal * zReal - zImag * zImag + real; // New zReal = (zReal^2 - zImag^2) + cReal
                    double tempImag = 2 * zReal * zImag + imag;         // New zImag = (2 * zReal * zImag) + cImag

                    // Update zReal and zImag with the new values.
                    zReal = tempReal;
                    zImag = tempImag;

                    // Increment the iteration count.
                    iterations++;
                }

                // Determine the color of the pixel based on how quickly it diverged.
                // If iterations == MAX_ITERATIONS, the point is considered to be in the Mandelbrot set.
                // We color it black for points within the set.
                if (iterations == MAX_ITERATIONS) {
                    // Black color for points inside the Mandelbrot set.
                    mandelbrotImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    // For points outside the set, we color them based on the number of iterations.
                    // This creates the colorful patterns. We use a simple color mapping here.
                    // The modulo operator (%) helps create repeating color patterns.
                    float hue = (float) iterations / MAX_ITERATIONS;
                    float saturation = 1.0f;
                    float brightness = 1.0f;
                    Color color = Color.getHSBColor(hue, saturation, brightness);
                    mandelbrotImage.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    // The entry point of our application.
    public static void main(String[] args) {
        // Swing applications should be created and updated on the Event Dispatch Thread (EDT)
        // to ensure thread safety and proper GUI updates.
        SwingUtilities.invokeLater(MandelbrotVisualizer::new);
    }
}