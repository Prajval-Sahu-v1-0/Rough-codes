// Learning Objective: This tutorial will teach you how to generate intricate fractal art patterns
// using recursion and basic Java graphics. We will focus on the concept of
// "self-similarity" which is the core of fractal generation.

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FractalArtGenerator extends JPanel {

    // The main method to set up the JFrame and start the fractal drawing.
    public static void main(String[] args) {
        JFrame frame = new JFrame("Fractal Art"); // Create a window for our drawing.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application closes when the window is closed.
        frame.setSize(800, 600); // Set the initial size of the window.
        frame.add(new FractalArtGenerator()); // Add our custom panel to the frame.
        frame.setVisible(true); // Make the window visible.
    }

    // The paintComponent method is where all custom painting is done.
    // It's called automatically by Swing when the panel needs to be redrawn.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass's paintComponent to ensure proper rendering.
        Graphics2D g2d = (Graphics2D) g; // Cast to Graphics2D for more advanced drawing capabilities.

        // Set the background color of the drawing area.
        g2d.setColor(Color.BLACK); // We'll draw on a black background.
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Fill the entire panel with black.

        // Define the starting point and initial size for our fractal.
        int startX = getWidth() / 2; // Start in the horizontal center of the panel.
        int startY = getHeight() - 50; // Start near the bottom of the panel.
        int initialLength = 200; // The initial length of the "trunk" of our fractal.
        int initialDepth = 8; // The maximum depth of recursion (how many levels of branches).

        // Set the color for our fractal.
        g2d.setColor(Color.GREEN); // Let's make our fractal green, like a tree.

        // Start the recursive drawing process.
        // We pass the graphics object, starting coordinates, initial length, and depth.
        drawFractalBranch(g2d, startX, startY, initialLength, initialDepth);
    }

    // This is the recursive function that draws a single branch and its sub-branches.
    // g2d: The Graphics2D object to draw on.
    // x1, y1: The starting coordinates of the current branch.
    // length: The length of the current branch.
    // depth: The current recursion depth.
    private void drawFractalBranch(Graphics2D g2d, int x1, int y1, int length, int depth) {
        // Base case: If the depth is 0, we stop recursing and don't draw anything further.
        // This prevents infinite recursion.
        if (depth == 0) {
            return;
        }

        // Calculate the endpoint of the current branch.
        // We're drawing a line upwards, so y decreases.
        int x2 = x1;
        int y2 = y1 - length;

        // Draw the current branch.
        g2d.drawLine(x1, y1, x2, y2);

        // Now, for the recursive step: draw sub-branches.
        // We'll create two sub-branches, angled slightly from the current branch.

        // Calculate the angle for the left sub-branch.
        // We'll angle it about 30 degrees to the left.
        // Math.toRadians converts degrees to radians for trigonometric functions.
        double angleLeft = Math.toRadians(30); // 30 degrees left
        // Calculate the new length for the sub-branches (they are shorter).
        int subBranchLength = (int) (length * 0.7); // Make sub-branches 70% of the current length.

        // Calculate the endpoint of the left sub-branch using trigonometry.
        // x2 = x1 + r * cos(theta)
        // y2 = y1 + r * sin(theta)
        // Since we're drawing upwards, the angle relative to the horizontal is 90 degrees.
        // For a branch going up, the angle from the vertical (our current direction) is 'angleLeft'.
        // So, the absolute angle from the horizontal is 90 - angleLeft.
        int subBranchX1Left = x1 + (int) (subBranchLength * Math.cos(Math.toRadians(90) - angleLeft));
        int subBranchY1Left = y1 - (int) (subBranchLength * Math.sin(Math.toRadians(90) - angleLeft));

        // Recursively call drawFractalBranch for the left sub-branch.
        // We decrease the depth by 1 for the next level of recursion.
        drawFractalBranch(g2d, subBranchX1Left, subBranchY1Left, subBranchLength, depth - 1);

        // Calculate the angle for the right sub-branch.
        double angleRight = Math.toRadians(30); // 30 degrees right

        // Calculate the endpoint of the right sub-branch.
        // The absolute angle from the horizontal is 90 + angleRight.
        int subBranchX1Right = x1 + (int) (subBranchLength * Math.cos(Math.toRadians(90) + angleRight));
        int subBranchY1Right = y1 - (int) (subBranchLength * Math.sin(Math.toRadians(90) + angleRight));

        // Recursively call drawFractalBranch for the right sub-branch.
        drawFractalBranch(g2d, subBranchX1Right, subBranchY1Right, subBranchLength, depth - 1);
    }
}