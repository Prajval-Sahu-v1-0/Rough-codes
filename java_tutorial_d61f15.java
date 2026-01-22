// Learning Objective: This tutorial will teach you how to visualize and interact with the Mandelbrot fractal
// using Java's Swing framework for graphical display. We'll focus on rendering the fractal and
// understanding the core logic behind its generation.

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MandelbrotRenderer extends JPanel {

    // The width and height of the image we'll draw the fractal on.
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    // These represent the boundaries of the complex plane we are rendering.
    // Initially, we'll focus on the standard view of the Mandelbrot set.
    private double xMin = -2.0;
    private double xMax = 1.0;
    private double yMin = -1.5;
    private double yMax = 1.5;

    // This is the maximum number of iterations to perform for each point.
    // A higher number means more detail but slower rendering.
    private static final int MAX_ITERATIONS = 1000;

    // A BufferedImage will hold the pixel data of our fractal.
    private BufferedImage fractalImage;

    // Constructor: Sets up the panel and creates the image.
    public MandelbrotRenderer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Set the preferred size for the panel.
        createFractalImage(); // Call the method to generate the fractal image.
    }

    // This method is responsible for generating the pixel data for the Mandelbrot fractal.
    private void createFractalImage() {
        fractalImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // Create an empty image.

        // Iterate through each pixel on our image.
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                // Map the pixel coordinates (x, y) to complex plane coordinates (cx, cy).
                // This is a crucial step in translating our 2D image to the complex plane.
                double cx = xMin + (double) x / WIDTH * (xMax - xMin);
                double cy = yMin + (double) y / HEIGHT * (yMax - yMin);

                // Calculate the number of iterations for this complex number.
                int iterations = calculateMandelbrotIterations(cx, cy);

                // Determine the color based on the number of iterations.
                // Points inside the Mandelbrot set (iterations == MAX_ITERATIONS) will be black.
                // Points outside will be colored based on how quickly they escaped.
                Color color = getColor(iterations);
                fractalImage.setRGB(x, y, color.getRGB()); // Set the pixel color in the image.
            }
        }
    }

    // The core Mandelbrot calculation logic.
    // For a given complex number c (represented by cx, cy), we repeatedly apply
    // the function z = z^2 + c, starting with z = 0.
    // If the magnitude of z stays bounded (less than 2) after MAX_ITERATIONS,
    // the point c is considered to be in the Mandelbrot set.
    private int calculateMandelbrotIterations(double cx, double cy) {
        double zx = 0; // Real part of z
        double zy = 0; // Imaginary part of z
        int iterations = 0;

        // Loop until the magnitude of z exceeds 2 or we reach MAX_ITERATIONS.
        while (zx * zx + zy * zy < 4 && iterations < MAX_ITERATIONS) {
            double tempZx = zx * zx - zy * zy + cx; // Calculate the new real part of z.
            zy = 2 * zx * zy + cy;               // Calculate the new imaginary part of z.
            zx = tempZx;                         // Update zx.
            iterations++;                         // Increment the iteration count.
        }
        return iterations; // Return the number of iterations it took to escape or reach the limit.
    }

    // A simple coloring scheme. You can experiment with more complex ones!
    private Color getColor(int iterations) {
        if (iterations == MAX_ITERATIONS) {
            return Color.BLACK; // Points in the set are black.
        }
        // Simple gradient: more iterations = brighter colors.
        // This creates a smooth transition for points outside the set.
        float hue = (float) iterations / MAX_ITERATIONS;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    // This method is called by Swing to draw the component.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to ensure proper painting.
        if (fractalImage != null) {
            // Draw the pre-rendered fractal image onto the panel.
            g.drawImage(fractalImage, 0, 0, this);
        }
    }

    // Example Usage: How to create and display the Mandelbrot fractal.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mandelbrot Fractal"); // Create a window (JFrame).
            MandelbrotRenderer renderer = new MandelbrotRenderer(); // Create our fractal renderer component.
            frame.add(renderer); // Add the renderer to the frame.
            frame.pack(); // Size the frame to fit its contents.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set what happens when the window is closed.
            frame.setLocationRelativeTo(null); // Center the window on the screen.
            frame.setVisible(true); // Make the window visible.
        });
    }
}