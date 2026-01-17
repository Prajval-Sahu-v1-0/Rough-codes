// Learning Objective: This tutorial teaches how to generate and visualize
// intricate fractal patterns using recursion and basic Java graphics.
// We will focus on the concept of self-similarity inherent in fractals
// and how it can be implemented recursively.

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RecursiveFractalGenerator extends JPanel {

    // The maximum depth of recursion. Higher values create more detail
    // but take longer to compute. This is a key parameter for fractal complexity.
    private int maxDepth;

    // Constructor: Initializes the fractal generator with a specified recursion depth.
    public RecursiveFractalGenerator(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    // The core drawing method provided by JPanel. This is where our fractal
    // will be rendered. We override it to perform custom drawing.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call superclass method first.

        // Create a blank image to draw on. This can be more efficient for complex
        // fractal generation as we are not directly drawing pixels on the panel
        // repeatedly. Instead, we draw to an image and then draw that image.
        // The image size is determined by the panel's current dimensions.
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics imageGraphics = image.getGraphics();

        // Fill the background with black for contrast.
        imageGraphics.setColor(Color.BLACK);
        imageGraphics.fillRect(0, 0, getWidth(), getHeight());

        // Define the starting point and dimensions for our fractal.
        // For simplicity, we'll center the fractal and use a portion of the panel's
        // width to define its initial size.
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int initialSize = Math.min(getWidth(), getHeight()) / 3; // Start with a reasonable size.

        // Begin the recursive drawing process.
        // We start drawing from the center, with the initial size, and at depth 0.
        drawFractal(imageGraphics, centerX, centerY, initialSize, 0);

        // Draw the generated fractal image onto the panel.
        g.drawImage(image, 0, 0, this);
    }

    // Recursive function to draw the fractal.
    // Parameters:
    // g: The Graphics object to draw on.
    // x, y: The current center coordinates for drawing this part of the fractal.
    // size: The current size or extent of this fractal element.
    // depth: The current recursion depth.
    private void drawFractal(Graphics g, int x, int y, int size, int depth) {
        // Base case for recursion: If we have reached the maximum depth,
        // stop drawing this branch to prevent infinite recursion.
        if (depth >= maxDepth || size < 2) { // Also stop if size becomes too small
            return;
        }

        // Set a color for drawing. We can use a simple scheme or more complex ones.
        // Here, we'll make the color depend on the depth to add visual variety.
        // We use HSB color model for smoother transitions.
        float hue = (float) depth / maxDepth; // Hue varies from 0.0 to 1.0
        float saturation = 1.0f; // Full saturation
        float brightness = 1.0f; // Full brightness
        g.setColor(Color.getHSBColor(hue, saturation, brightness));

        // Draw a simple shape at the current location. For this example, we'll draw
        // a small square. This shape will be the "building block" of our fractal.
        // The square is centered around (x, y) and has dimensions size x size.
        int halfSize = size / 2;
        g.fillRect(x - halfSize, y - halfSize, size, size);

        // Recursive step: Call drawFractal multiple times with modified parameters
        // to create the self-similar pattern. The exact placement and scaling of
        // these recursive calls determine the type of fractal.

        // For this example, let's create a Sierpinski-like triangle pattern
        // by drawing three smaller versions of the fractal in a triangular arrangement.
        // The new size is half of the current size.

        // Top-left recursive call
        drawFractal(g, x - size / 2, y - size / 2, size / 2, depth + 1);
        // Top-right recursive call
        drawFractal(g, x + size / 2, y - size / 2, size / 2, depth + 1);
        // Bottom recursive call (centered below)
        drawFractal(g, x, y + size / 2, size / 2, depth + 1);

        // Important: If you wanted a different fractal, you would change the
        // number of recursive calls, their positions, and their scaling here.
        // For example, a Koch snowflake would involve rotating and scaling
        // segments. A Mandelbrot set involves complex number calculations.
    }

    // Example usage: Sets up a JFrame to display our fractal.
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure that GUI operations are
        // performed on the Event Dispatch Thread (EDT), which is the standard
        // and safest way to handle Swing applications.
        SwingUtilities.invokeLater(() -> {
            // Create a frame for our fractal.
            JFrame frame = new JFrame("Recursive Fractal Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
            frame.setSize(600, 600); // Set initial window size

            // Create an instance of our fractal generator.
            // The parameter '10' sets the maximum recursion depth.
            // Experiment with this value to see how it affects detail and performance.
            RecursiveFractalGenerator fractalPanel = new RecursiveFractalGenerator(10);

            // Add the fractal panel to the frame.
            frame.add(fractalPanel);

            // Make the frame visible.
            frame.setVisible(true);
        });
    }
}