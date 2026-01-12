// Learning Objective: This tutorial will teach you how to visualize
// fractal patterns in Java using recursion and basic graphics.
// We will focus on the concept of self-similarity, which is
// fundamental to fractals, and demonstrate it with a simple
// recursive drawing algorithm.

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

// The main class for our fractal drawing application.
public class FractalVisualizer extends JPanel {

    // The primary drawing method called by Swing when the panel needs to be painted.
    @Override
    protected void paintComponent(Graphics g) {
        // Always call the superclass method to ensure proper painting of the panel itself.
        super.paintComponent(g);

        // Cast the Graphics object to Graphics2D for more advanced drawing capabilities.
        Graphics2D g2d = (Graphics2D) g;

        // Set the rendering hints for smoother lines and better anti-aliasing.
        // This makes the fractal look nicer.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the color of the lines we will draw. Black is a good starting point.
        g2d.setColor(Color.BLACK);

        // Define the starting point and size of our fractal.
        // We'll draw it centered in the panel.
        int startX = getWidth() / 2;
        int startY = getHeight() - 50; // Start from near the bottom
        int initialLength = 200;       // The length of the first line segment

        // Initiate the recursive drawing of the fractal.
        // We'll use a simple "Sierpinski Triangle" like structure for demonstration.
        // The parameters are:
        // g2d: The Graphics2D object to draw on.
        // x1, y1: The starting coordinates of the current line segment.
        // x2, y2: The ending coordinates of the current line segment.
        // depth: The current recursion depth, which controls how many times
        //        the pattern is repeated. Higher depth means more detail.
        drawFractal(g2d, startX, startY, startX + initialLength, startY, 7); // Start with a horizontal line
    }

    // The recursive method that draws the fractal pattern.
    // This is where the magic of fractals happens!
    private void drawFractal(Graphics2D g2d, int x1, int y1, int x2, int y2, int depth) {
        // Base case: If the recursion depth reaches 0, we stop drawing.
        // This prevents infinite recursion and ensures the fractal has a limit.
        if (depth <= 0) {
            // Draw the final line segment at this level.
            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
            return; // Stop this branch of recursion
        }

        // Calculate the coordinates of the two new points that will form the "branches"
        // of our fractal pattern. We are essentially dividing the current line segment
        // into three equal parts and creating a new point in the middle.
        // This is specific to creating a triangle-like fractal.

        // Point 1: Divides the line segment into 2/3 from the start.
        // (x1 + (x2 - x1) * 2/3, y1 + (y2 - y1) * 2/3)
        double x_p1 = x1 + (x2 - x1) * (2.0 / 3.0);
        double y_p1 = y1 + (y2 - y1) * (2.0 / 3.0);

        // Point 2: Divides the line segment into 1/3 from the start.
        // (x1 + (x2 - x1) * 1/3, y1 + (y2 - y1) * 1/3)
        double x_p2 = x1 + (x2 - x1) * (1.0 / 3.0);
        double y_p2 = y1 + (y2 - y1) * (1.0 / 3.0);

        // For a triangular fractal, we need a third point to form the triangle.
        // This point is offset from the middle of the original line segment.
        // For this specific fractal (similar to Sierpinski triangle),
        // it's derived from the middle of the segment (x1+x2)/2 and (y1+y2)/2
        // and then offset upwards.
        // The offset's magnitude is proportional to the line length and a constant factor.
        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double heightOffset = lineLength * Math.sqrt(3.0) / 6.0; // sqrt(3)/6 for equilateral triangle

        // Calculate the midpoint of the original line segment.
        double midX = (x1 + x2) / 2.0;
        double midY = (y1 + y2) / 2.0;

        // Determine the direction to offset the third point.
        // This uses the vector perpendicular to the line segment (x1,y1) to (x2,y2).
        // The vector from (x1,y1) to (x2,y2) is (dx, dy) = (x2-x1, y2-y1).
        // A perpendicular vector is (-dy, dx). We normalize this and scale by heightOffset.
        double dx = x2 - x1;
        double dy = y2 - y1;
        double perpX = -dy;
        double perpY = dx;

        // Normalize the perpendicular vector.
        double perpLength = Math.sqrt(perpX * perpX + perpY * perpY);
        double unitPerpX = perpX / perpLength;
        double unitPerpY = perpY / perpLength;

        // The third point of the triangle.
        // We need to decide which side to offset. For this example, we offset upwards.
        // We use the original line's orientation to determine the "up" direction.
        // If dy is negative (line goes down), we might want to offset up relative to screen.
        // A simpler approach for this specific fractal is to offset based on the y-axis.
        // For a basic demonstration, we'll always offset "up" relative to the segment's orientation.
        // A fixed offset direction might be easier to grasp initially.
        // Let's offset based on the screen's y-axis direction, which is downwards.
        // If the line goes from left to right (dx > 0), the perpendicular is (-dy, dx).
        // If we want to go "up", we need to adjust based on dx.
        // For a robust solution, one might consider the angle of the line.
        // For this simple example, let's try a fixed offset that generally points upwards.
        // The vector (x2-x1, y2-y1) has direction. Its perpendicular is (-(y2-y1), x2-x1).
        // If we want the triangle peak to be "above" the line, and the line is horizontal,
        // we offset upwards.
        // The current implementation of offset can be tricky. Let's simplify it.
        // For this specific fractal (Koch curve like structure within triangles),
        // we construct three new line segments.

        // The three recursive calls will draw the smaller versions of the fractal.
        // This is the core of the recursive fractal generation.

        // Call 1: Draws the left third of the segment to the point derived from dividing.
        // This point forms the base of the left small fractal.
        drawFractal(g2d, x1, y1, (int) x_p2, (int) y_p2, depth - 1);

        // Call 2: Draws the right third of the segment to the point derived from dividing.
        // This point forms the base of the right small fractal.
        drawFractal(g2d, (int) x_p1, (int) y_p1, x2, y2, depth - 1);

        // Call 3: Draws the "middle" part, forming the peak of the triangle.
        // This part connects the two dividing points and goes up to the calculated peak.
        // The peak point (x_peak, y_peak) is calculated using the midpoint and height offset.
        // For a simple Sierpinski-like structure, we connect the two intermediate points.
        // This specific recursive structure creates a "snowflake" like pattern if applied in 3D.
        // For 2D, it's more like a Sierpinski carpet or triangle variation.

        // Let's refine the third call for a more triangle-like structure.
        // The third line segment goes from point p2 to point p1 and then to the peak.
        // A simpler fractal (like a Koch curve) has 4 recursive calls.
        // For a Sierpinski triangle variant:
        // We divide each side into 3. The middle third is replaced by two sides of an equilateral triangle.

        // Let's re-think the recursion for a clearer Sierpinski-like fractal.
        // Each line segment is replaced by 4 smaller segments.

        // To simplify, let's stick to a fractal that uses the line segment itself
        // as the basis for branching.
        // The following recursive calls create a more standard branching fractal.
        // This creates a pattern with lines branching off.

        // Branch 1: Start from (x1, y1) to the first calculated point.
        drawFractal(g2d, x1, y1, (int) x_p2, (int) y_p2, depth - 1);

        // Branch 2: Start from the first calculated point to the second calculated point.
        // This is the middle segment.
        drawFractal(g2d, (int) x_p2, (int) y_p2, (int) x_p1, (int) y_p1, depth - 1);

        // Branch 3: Start from the second calculated point to the end (x2, y2).
        drawFractal(g2d, (int) x_p1, (int) y_p1, x2, y2, depth - 1);

        // For a more complex fractal, you'd add more recursive calls at different angles.
        // This implementation creates a simplified "line fractal" where each line is
        // replaced by three shorter lines, creating a jagged or branching effect.
        // If you want a Sierpinski triangle, the logic involves drawing three
        // recursive calls for each of the three sides of the larger triangle.

        // For this tutorial, we'll stick with this three-part division.
        // This demonstrates the core recursive idea well.

        // Let's add a slight angle to the middle segment for a more pronounced fractal shape.
        // This makes it look more like a tree or a branching pattern.
        // We can rotate the middle segment slightly.
        // For simplicity, we will not add rotation here to keep it beginner-friendly.
        // The current code creates a pattern where each line segment is replaced by three
        // segments of 1/3rd length, creating a dense, jagged line.

        // The base case `depth <= 0` draws the final, smallest line segment.
        // The recursive calls break down the larger segments into smaller ones,
        // infinitely (theoretically) repeating the pattern.
    }

    // This method creates a JFrame (a window) and adds our FractalVisualizer panel to it.
    public static void createAndShowGUI() {
        // Create the frame.
        JFrame frame = new JFrame("Fractal Visualizer");
        // Set the default close operation: when the window is closed, exit the application.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an instance of our fractal panel.
        FractalVisualizer fractalPanel = new FractalVisualizer();
        // Set the preferred size of the panel. This influences how large the window will be.
        fractalPanel.setPreferredSize(new Dimension(800, 600));

        // Add the fractal panel to the frame's content pane.
        frame.getContentPane().add(fractalPanel);

        // Pack the frame. This sizes the frame so that all its components
        // are at their preferred sizes.
        frame.pack();
        // Make the frame visible.
        frame.setVisible(true);
    }

    // The main method, the entry point of our Java application.
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        // This is the standard way to create GUIs in Swing to ensure thread safety.
        SwingUtilities.invokeLater(FractalVisualizer::createAndShowGUI);
    }
}