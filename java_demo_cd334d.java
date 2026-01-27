// Learning Objective:
// This tutorial will guide you through visualizing and exploring the Mandelbrot fractal
// using Java Swing for the graphical interface and basic complex number arithmetic.
// We'll focus on understanding how the iteration count of a complex function
// determines which points belong to the Mandelbrot set and how to represent this visually.

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage; // For creating and manipulating image data

public class MandelbrotExplorer extends JFrame {

    // --- Constants for the fractal and visualization ---
    private static final int WIDTH = 800;  // Width of our fractal image
    private static final int HEIGHT = 800; // Height of our fractal image
    private static final int MAX_ITERATIONS = 100; // Maximum iterations for each point calculation

    // --- Image to draw the fractal on ---
    private BufferedImage fractalImage;
    private JLabel imageLabel; // A Swing component to display the image

    public MandelbrotExplorer() {
        // --- JFrame setup ---
        super("Mandelbrot Fractal Explorer"); // Set the window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the window is closed
        setSize(WIDTH, HEIGHT); // Set the size of the window
        setLocationRelativeTo(null); // Center the window on the screen

        // --- Initialize the image ---
        fractalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // Create an RGB image
        imageLabel = new JLabel(new ImageIcon(fractalImage)); // Create a label to hold the image
        add(imageLabel); // Add the label to the frame

        // --- Calculate and draw the Mandelbrot set ---
        calculateMandelbrot();

        // --- Make the window visible ---
        setVisible(true);
    }

    // --- Core Mandelbrot calculation logic ---
    private void calculateMandelbrot() {
        // We iterate through each pixel of our image (WIDTH x HEIGHT)
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                // Map the pixel coordinates (x, y) to a complex number (c_real, c_imag)
                // The Mandelbrot set is defined on the complex plane.
                // We are mapping the image pixels to a specific region of this plane.
                // Here, we map from [0, WIDTH] to [-2.0, 1.0] for the real axis
                // and from [0, HEIGHT] to [-1.5, 1.5] for the imaginary axis.
                double c_real = (double) x / WIDTH * 3.0 - 2.0; // Map x to real part
                double c_imag = (double) y / HEIGHT * 3.0 - 1.5; // Map y to imaginary part

                // Initialize the complex number z for the iteration.
                // For the Mandelbrot set, z starts at 0 + 0i.
                double z_real = 0.0;
                double z_imag = 0.0;

                int iterations = 0; // Counter for how many times we iterate

                // The core Mandelbrot iteration: z = z^2 + c
                // We repeat this process up to MAX_ITERATIONS times.
                // A point belongs to the Mandelbrot set if |z| remains bounded (less than 2).
                // If |z| grows larger than 2, the point escapes to infinity.
                while (z_real * z_real + z_imag * z_imag < 4.0 && iterations < MAX_ITERATIONS) {
                    // Calculate z^2: (z_real + i*z_imag)^2 = (z_real^2 - z_imag^2) + i*(2*z_real*z_imag)
                    double z_real_temp = z_real * z_real - z_imag * z_imag; // Temporary storage for z_real^2 - z_imag^2
                    z_imag = 2.0 * z_real * z_imag; // Calculate the new imaginary part of z^2
                    z_real = z_real_temp; // Update z_real with the calculated real part of z^2

                    // Add c to z: z = z + c
                    z_real += c_real; // Add the real part of c
                    z_imag += c_imag; // Add the imaginary part of c

                    iterations++; // Increment the iteration counter
                }

                // --- Coloring based on iterations ---
                // If iterations == MAX_ITERATIONS, the point is likely in the Mandelbrot set.
                // Otherwise, the number of iterations tells us how "quickly" it escaped.
                if (iterations == MAX_ITERATIONS) {
                    // Point is in the Mandelbrot set, color it black
                    fractalImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    // Point escaped, color it based on how fast it escaped.
                    // This creates the interesting fractal patterns.
                    // A simple coloring scheme:
                    float hue = (float) iterations / MAX_ITERATIONS; // Normalize iterations to [0, 1]
                    Color color = Color.getHSBColor(hue, 1.0f, 1.0f); // Use HSB color model for vibrant colors
                    fractalImage.setRGB(x, y, color.getRGB());
                }
            }
        }
        // --- Update the display to show the calculated fractal ---
        imageLabel.repaint(); // Tell Swing to redraw the label with the new image
    }

    // --- Example Usage ---
    public static void main(String[] args) {
        // Swing applications should be run on the Event Dispatch Thread (EDT)
        // to ensure proper GUI updates and thread safety.
        SwingUtilities.invokeLater(() -> {
            new MandelbrotExplorer(); // Create and show our fractal explorer window
        });
    }
}