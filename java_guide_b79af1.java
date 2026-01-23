// Learning Objective: Build a basic peer-to-peer (P2P) messenger application
// using Java sockets and multi-threading to enable direct communication between users.
// We will learn how to:
// 1. Establish a network connection between two peers using Sockets.
// 2. Send and receive text messages over this connection.
// 3. Use separate threads to handle concurrent input/output operations (sending and receiving)
//    without blocking each other.
// 4. Implement basic server (listener) and client (connector) roles for P2P setup.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class P2PMessenger {

    private static final int DEFAULT_PORT = 12345; // Default port for listening or connecting
    private static Socket peerSocket;             // The central socket for communication with the other peer
    private static PrintWriter out;               // Used to send text data to the peer
    private static BufferedReader in;             // Used to receive text data from the peer

    public static void main(String[] args) {
        // Scanner to read messages typed by the user in the console.
        Scanner consoleInputScanner = new Scanner(System.in);

        // --- Step 1: Determine this peer's role (Listener or Connector) ---
        // In a P2P system, one peer must initiate a connection (client role),
        // and the other must wait for it (server role). This example uses command-line
        // arguments to switch roles.

        if (args.length == 0) {
            // If no arguments are provided, this peer will act as the LISTENER (server role).
            // It waits for another peer to connect to it.
            System.out.println("Starting P2P Messenger as LISTENER (Server Role)...");
            System.out.println("Listening on port " + DEFAULT_PORT);
            try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
                // serverSocket.accept() blocks execution until an incoming connection is made.
                // When a connection arrives, it returns a new Socket object to handle
                // communication with that specific client.
                peerSocket = serverSocket.accept();
                System.out.println("Connection established with peer: " + peerSocket.getInetAddress());
                setupCommunicationStreams(); // Initialize streams for the connected socket
            } catch (IOException e) {
                System.err.println("Listener (Server) error: " + e.getMessage());
                return; // Exit if the server setup or connection fails
            }
        } else if (args.length == 2) {
            // If two arguments are provided (IP and Port), this peer acts as the CONNECTOR (client role).
            // It tries to establish a connection to a listening peer.
            String targetIp = args[0];
            int targetPort = Integer.parseInt(args[1]); // Convert port string to an integer
            System.out.println("Starting P2P Messenger as CONNECTOR (Client Role)...");
            System.out.println("Attempting to connect to " + targetIp + ":" + targetPort);
            try {
                // Create a Socket and attempt to connect to the specified IP and port.
                // This call blocks until the connection is established or fails.
                peerSocket = new Socket(targetIp, targetPort);
                System.out.println("Successfully connected to peer: " + peerSocket.getInetAddress());
                setupCommunicationStreams(); // Initialize streams for the connected socket
            } catch (IOException e) {
                System.err.println("Connector (Client) error: " + e.getMessage());
                return; // Exit if the client connection fails
            }
        } else {
            // Handle incorrect command-line usage.
            System.out.println("Usage: java P2PMessenger                 (to start as listener on default port)");
            System.out.println("       java P2PMessenger <targetIp> <targetPort> (to connect to another peer)");
            consoleInputScanner.close(); // Close scanner before exiting
            return;
        }

        // --- Step 2: Start a separate thread for receiving messages ---
        // Network I/O can block. If we used the main thread for both sending and receiving,
        // one operation (e.g., waiting for an incoming message) would prevent the other
        // (e.g., typing and sending a message) from happening.
        // A dedicated 'receiver' thread continuously listens for incoming messages,
        // allowing the main thread to handle user input and sending messages concurrently.
        Thread receiverThread = new Thread(() -> { // Using a lambda expression for a concise Runnable
            try {
                String receivedMessage;
                // Loop indefinitely, reading lines from the peer's input stream.
                // readLine() blocks until a line is received or the stream is closed.
                while ((receivedMessage = in.readLine()) != null) {
                    System.out.println("Peer: " + receivedMessage); // Display the message from the other peer
                }
            } catch (IOException e) {
                // An IOException here often means the other peer disconnected, or a network issue occurred.
                System.out.println("Peer disconnected or error during receive: " + e.getMessage());
            } finally {
                // Always attempt to close resources if the receiver thread terminates.
                closeSocketResources();
            }
        });
        receiverThread.start(); // Launch the message receiving thread

        // --- Step 3: Main thread handles sending messages (user input) ---
        System.out.println("You are connected. Type messages and press Enter to send. Type 'exit' to quit.");
        String messageToSend;
        // Continue sending messages as long as the socket is open and valid.
        while (peerSocket != null && !peerSocket.isClosed()) {
            messageToSend = consoleInputScanner.nextLine(); // Read message from user input
            if ("exit".equalsIgnoreCase(messageToSend)) {
                break; // User wants to quit the application
            }
            out.println(messageToSend); // Send the message to the connected peer
        }

        // --- Step 4: Cleanup resources before exiting ---
        System.out.println("Shutting down messenger.");
        closeSocketResources(); // Close all stream and socket resources safely
        consoleInputScanner.close(); // Close the console input scanner
    }

    // Helper method to set up input and output streams for the connected peerSocket.
    private static void setupCommunicationStreams() throws IOException {
        // PrintWriter sends text. 'true' argument enables auto-flushing, meaning
        // messages are sent immediately after println() is called, rather than buffering.
        out = new PrintWriter(peerSocket.getOutputStream(), true);
        // BufferedReader allows reading text line by line, which is suitable for chat messages.
        in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
    }

    // Helper method to ensure all network resources (streams and socket) are closed safely.
    private static void closeSocketResources() {
        try {
            if (out != null) out.close(); // Close the output stream
            if (in != null) in.close();   // Close the input stream
            if (peerSocket != null && !peerSocket.isClosed()) {
                peerSocket.close();       // Close the main communication socket
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}

/*
--- Example Usage ---

To run this application, you will need two separate terminal windows (or command prompts).

1.  Compile the Java code:
    javac P2PMessenger.java

2.  In Terminal Window 1 (This will be Peer A - the listener/server role):
    java P2PMessenger
    You should see:
    Starting P2P Messenger as LISTENER (Server Role)...
    Listening on port 12345
    (It will pause here, waiting for Peer B to connect)

3.  In Terminal Window 2 (This will be Peer B - the connector/client role):
    java P2PMessenger 127.0.0.1 12345
    (127.0.0.1 is the 'localhost' IP address, meaning it connects to itself on the same machine.
    If you were connecting to another computer, you'd use its actual IP address).
    You should see:
    Starting P2PMessenger as CONNECTOR (Client Role)...
    Attempting to connect to 127.0.0.1:12345
    Successfully connected to peer: /127.0.0.1

4.  Once Peer B connects, both terminals should update:
    Terminal 1 (Peer A):
    Connection established with peer: /127.0.0.1
    You are connected. Type messages and press Enter to send. Type 'exit' to quit.

    Terminal 2 (Peer B):
    You are connected. Type messages and press Enter to send. Type 'exit' to quit.

5.  Now, you can type messages in either terminal and press Enter. The message will appear
    in the other terminal, prefixed with "Peer: ".
    Example:
    In Terminal 1, type: Hello Peer B!
    In Terminal 2, you'll see: Peer: Hello Peer B!

    In Terminal 2, type: Hi Peer A, how are you?
    In Terminal 1, you'll see: Peer: Hi Peer A, how are you?

6.  To quit the messenger, type 'exit' and press Enter in either terminal. Both peers will shut down gracefully.
*/