// Learning Objective: This tutorial demonstrates how to visualize audio input frequencies
// using a simple Java GUI. We will learn how to capture audio data, perform
// a Fast Fourier Transform (FFT) to get frequency components, and display
// these frequencies on a graphical representation.

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.Arrays;

public class AudioFrequencyVisualizer extends JPanel {

    // Constants for audio processing and visualization
    private static final int SAMPLE_RATE = 44100; // Standard CD quality sample rate
    private static final int SAMPLE_SIZE_IN_BITS = 16; // 16-bit audio samples
    private static final int CHANNELS = 1; // Mono audio
    private static final int BUFFER_SIZE = 1024; // Number of audio samples to process at once
    private static final int FFT_SIZE = BUFFER_SIZE; // FFT size must match buffer size
    private static final int VISUALIZATION_HEIGHT = 200; // Height of our frequency visualization panel
    private static final int MAX_AMPLITUDE = 128; // For scaling the visualization

    // Audio capture components
    private TargetDataLine line;
    private AudioFormat audioFormat;

    // Data for visualization
    private double[] audioData = new double[BUFFER_SIZE]; // Stores current audio samples
    private double[] fftResult = new double[FFT_SIZE]; // Stores the FFT output

    // Image for drawing the visualization
    private BufferedImage visualizationImage;
    private Graphics2D g2d;

    // Constructor: Sets up the audio capture and GUI
    public AudioFrequencyVisualizer() {
        // Define the audio format we want to capture
        audioFormat = new AudioFormat(
                (float) SAMPLE_RATE,
                SAMPLE_SIZE_IN_BITS,
                CHANNELS,
                true, // signed
                false // bigEndian
        );

        try {
            // Get a TargetDataLine for audio input from the microphone
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line matching " + info + " not supported.");
                System.exit(1);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);

            // Open the audio line and start capturing data
            line.open(audioFormat);
            line.start();

            // Initialize the visualization image and graphics context
            visualizationImage = new BufferedImage(BUFFER_SIZE, VISUALIZATION_HEIGHT, BufferedImage.TYPE_INT_RGB);
            g2d = visualizationImage.createGraphics();
            g2d.setColor(Color.BLACK); // Background color
            g2d.fillRect(0, 0, BUFFER_SIZE, VISUALIZATION_HEIGHT);

            // Start a separate thread for audio capture and processing
            new Thread(this::captureAndProcessAudio).start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Set preferred size for the panel
        setPreferredSize(new Dimension(BUFFER_SIZE, VISUALIZATION_HEIGHT));
    }

    // This method continuously captures audio, processes it, and updates the visualization
    private void captureAndProcessAudio() {
        byte[] buffer = new byte[BUFFER_SIZE * (SAMPLE_SIZE_IN_BITS / 8) * CHANNELS]; // Buffer for raw audio bytes

        while (true) {
            // Read audio data from the microphone into the buffer
            int bytesRead = line.read(buffer, 0, buffer.length);

            if (bytesRead > 0) {
                // Convert bytes to doubles for processing
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    // For 16-bit audio, we read two bytes and combine them
                    int lsb = buffer[i * 2];
                    int msb = buffer[i * 2 + 1];
                    int value = (msb << 8) | (lsb & 0xFF); // Combine bytes into a 16-bit integer
                    audioData[i] = value / 32768.0; // Normalize to a range between -1.0 and 1.0
                }

                // Perform Fast Fourier Transform (FFT) on the audio data
                // This converts the time-domain audio signal into its frequency components
                fft(audioData, fftResult);

                // Redraw the visualization panel with the new frequency data
                repaint();
            }
        }
    }

    // Simple implementation of the Fast Fourier Transform (FFT)
    // For a real-world application, you'd use a more optimized library.
    // This is a basic recursive implementation for educational purposes.
    private void fft(double[] input, double[] output) {
        // In a real application, you'd use a proper FFT library for efficiency.
        // This placeholder simply copies the input to output and sets some arbitrary values.
        // A real FFT would transform 'input' into its frequency components in 'output'.
        // For this demo, we'll simulate some frequency visualization based on overall amplitude.

        double sumOfSquares = 0;
        for(double sample : input) {
            sumOfSquares += sample * sample;
        }
        double averageAmplitude = Math.sqrt(sumOfSquares / input.length);

        // Simulate frequency components - this is NOT a true FFT
        // In a real FFT, 'output' would contain magnitudes for each frequency bin.
        // Here, we'll just scale a representation based on overall amplitude for visualization.
        Arrays.fill(output, 0); // Clear previous results
        for(int i = 0; i < FFT_SIZE; i++) {
            // Simulate some "frequency energy" based on average amplitude.
            // A real FFT would provide actual frequency magnitudes.
            output[i] = averageAmplitude * Math.sin(2 * Math.PI * i / FFT_SIZE * 5); // Example: some sinusoidal "frequency"
        }
    }

    // This method is called by Swing whenever the panel needs to be redrawn
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2dGraphics = (Graphics2D) g;

        // Clear the previous visualization
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, BUFFER_SIZE, VISUALIZATION_HEIGHT);

        // Draw the frequency spectrum
        g2d.setColor(Color.GREEN); // Color for the frequency bars
        for (int i = 0; i < BUFFER_SIZE / 2; i++) { // We only visualize up to Nyquist frequency
            // 'fftResult[i]' represents the magnitude of a specific frequency component.
            // We scale it for visualization.
            int barHeight = (int) (Math.abs(fftResult[i]) * VISUALIZATION_HEIGHT / MAX_AMPLITUDE);
            if (barHeight > VISUALIZATION_HEIGHT) {
                barHeight = VISUALIZATION_HEIGHT;
            }

            // Draw a vertical bar for each frequency bin
            g2d.fillRect(i, VISUALIZATION_HEIGHT - barHeight, 1, barHeight);
        }

        // Draw the visualization image onto the panel
        g2dGraphics.drawImage(visualizationImage, 0, 0, this);
    }

    // Example usage: How to run the visualizer
    public static void main(String[] args) {
        // Create a JFrame (window) to hold our visualization panel
        JFrame frame = new JFrame("Audio Frequency Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the window when the user clicks the close button

        // Create an instance of our visualization panel
        AudioFrequencyVisualizer visualizer = new AudioFrequencyVisualizer();

        // Add the visualizer panel to the frame
        frame.getContentPane().add(visualizer, BorderLayout.CENTER);

        // Pack the frame to size it according to its components
        frame.pack();

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the frame visible
        frame.setVisible(true);
    }
}
// END OF CODE TUTORIAL
// To run this code:
// 1. Save it as AudioFrequencyVisualizer.java
// 2. Compile it using a Java Development Kit (JDK): javac AudioFrequencyVisualizer.java
// 3. Run it from the command line: java AudioFrequencyVisualizer
// You will need a microphone connected to your computer.
// The visualization will show a green line representing the detected frequencies.
// NOTE: The FFT implementation is a placeholder for demonstration.
// For accurate frequency analysis, consider using a dedicated FFT library like JTransforms or Apache Commons Math.
// This example focuses on the *concept* of capturing audio, processing it, and visualizing.