// Learning Objective:
// This tutorial will teach the concept of recursion by
// creating a visual Sierpinski Triangle fractal pattern using Java Swing.
// We will break down the recursive process and demonstrate how
// a function can call itself to generate complex patterns from simple rules.

import javax.swing.*;
import java.awt.*;

// The main class to set up the Swing window and display the fractal.
public class SierpinskiTriangle extends JPanel {

    // The depth of recursion controls the complexity of the fractal.
    // Higher values create more intricate patterns but take longer to render.
    private int recursionDepth;

    // Constructor: Initializes the JPanel and sets the initial recursion depth.
    public SierpinskiTriangle(int depth) {
        this.recursionDepth = depth;
        // Set a preferred size for the panel so it's visible.
        setPreferredSize(new Dimension(800, 700));
    }

    // The paintComponent method is where all custom drawing happens in Swing.
    // It's automatically called by the Swing framework when the panel needs to be redrawn.
    @Override
    protected void paintComponent(Graphics g) {
        // Always call the superclass's paintComponent first. This ensures
        // that the panel is cleared and other Swing components are drawn correctly.
        super.paintComponent(g);

        // Start the recursive drawing process.
        // We pass the graphics context (g), and the coordinates for the initial triangle.
        // The initial triangle is defined by three points:
        // Point 1: Top center of the panel.
        // Point 2: Bottom left of the panel.
        // Point 3: Bottom right of the panel.
        drawSierpinski(g,
                       getWidth() / 2, 20, // Top point X, Y
                       20, getHeight() - 40, // Bottom-left point X, Y
                       getWidth() - 20, getHeight() - 40, // Bottom-right point X, Y
                       recursionDepth);
    }

    // This is the core recursive function.
    // It draws a Sierpinski Triangle of a given depth.
    // Parameters:
    // g: The Graphics object for drawing.
    // x1, y1: Coordinates of the first vertex of the current triangle.
    // x2, y2: Coordinates of the second vertex of the current triangle.
    // x3, y3: Coordinates of the third vertex of the current triangle.
    // depth: The remaining recursion depth.
    private void drawSierpinski(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3, int depth) {
        // BASE CASE: If the depth reaches 0, we stop recursing.
        // This is crucial to prevent infinite recursion.
        if (depth <= 0) {
            // Draw a filled triangle. This is the smallest unit of our fractal.
            // We use a simple polygon to represent the triangle.
            int[] xPoints = {x1, x2, x3};
            int[] yPoints = {y1, y2, y3};
            g.fillPolygon(xPoints, yPoints, 3);
            return; // Exit the function, stopping this branch of recursion.
        }

        // RECURSIVE STEP: If depth is greater than 0, we divide the current
        // triangle into three smaller triangles and recursively call
        // drawSierpinski on each of them.

        // Calculate the midpoints of each side of the current triangle.
        // These midpoints will form the vertices of the three smaller triangles.

        // Midpoint between (x1, y1) and (x2, y2)
        int mid1x = (x1 + x2) / 2;
        int mid1y = (y1 + y2) / 2;

        // Midpoint between (x2, y2) and (x3, y3)
        int mid2x = (x2 + x3) / 2;
        int mid2y = (y2 + y3) / 2;

        // Midpoint between (x3, y3) and (x1, y1)
        int mid3x = (x3 + x1) / 2;
        int mid3y = (y3 + y1) / 2;

        // Recursively draw the three smaller Sierpinski triangles.
        // Each recursive call reduces the depth by 1.

        // Draw the top triangle (using the top vertex and two midpoints).
        drawSierpinski(g, x1, y1, mid1x, mid1y, mid3x, mid3y, depth - 1);

        // Draw the left triangle (using the bottom-left vertex and two midpoints).
        drawSierpinski(g, x2, y2, mid1x, mid1y, mid2x, mid2y, depth - 1);

        // Draw the right triangle (using the bottom-right vertex and two midpoints).
        drawSierpinski(g, x3, y3, mid2x, mid2y, mid3x, mid3y, depth - 1);
    }

    // Example Usage:
    public static void main(String[] args) {
        // Create a JFrame (window) for our application.
        JFrame frame = new JFrame("Sierpinski Triangle Fractal");

        // Set the default close operation for the window.
        // When the user closes the window, the application should exit.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ask the user for the desired recursion depth using a dialog box.
        // This allows for interactive exploration of fractal complexity.
        String inputDepth = JOptionPane.showInputDialog(frame, "Enter recursion depth (e.g., 5):", "Sierpinski Settings", JOptionPane.QUESTION_MESSAGE);

        int depth = 5; // Default depth if input is invalid or canceled.
        try {
            depth = Integer.parseInt(inputDepth);
            // Ensure a reasonable depth to avoid excessive computation or rendering time.
            if (depth < 0 || depth > 10) {
                depth = 5; // Reset to default if out of a practical range.
                JOptionPane.showMessageDialog(frame, "Invalid depth. Using default depth of 5.", "Sierpinski Settings", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            // If the user enters non-numeric text or cancels, use the default.
            JOptionPane.showMessageDialog(frame, "Invalid input. Using default depth of 5.", "Sierpinski Settings", JOptionPane.WARNING_MESSAGE);
        }

        // Create an instance of our SierpinskiTriangle panel.
        SierpinskiTriangle sierpinskiPanel = new SierpinskiTriangle(depth);

        // Add the fractal panel to the frame's content pane.
        frame.getContentPane().add(sierpinskiPanel);

        // Pack the frame. This sizes the frame to fit its components.
        frame.pack();

        // Center the frame on the screen.
        frame.setLocationRelativeTo(null);

        // Make the frame visible.
        frame.setVisible(true);

        // Set the background color to something that makes the triangles visible.
        // Black triangles on a white background are classic.
        sierpinskiPanel.setBackground(Color.WHITE);
        sierpinskiPanel.setForeground(Color.BLACK); // This sets the default color for drawing.
    }
}