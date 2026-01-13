// Learning Objective: This tutorial demonstrates how to build a basic Java chatbot
// that learns user preferences and adapts its responses. We will focus on
// using a Map to store and retrieve learned preferences. This is a fundamental
// concept for building more sophisticated chatbots.

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LearningChatbot {

    // A Map to store the user's preferences.
    // The key will be a "topic" or "keyword" the user mentions,
    // and the value will be the chatbot's preferred response for that topic.
    // We use HashMap for efficient key-value storage and retrieval.
    private Map<String, String> learnedPreferences = new HashMap<>();

    // The Scanner object to read user input from the console.
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Create an instance of our LearningChatbot.
        LearningChatbot chatbot = new LearningChatbot();
        // Start the chatbot's interaction loop.
        chatbot.start();
    }

    // This method initiates the chatbot's main interaction loop.
    public void start() {
        System.out.println("Hello! I'm a learning chatbot. Tell me something, or ask me a question.");
        System.out.println("Type 'bye' to exit.");

        // This loop continues until the user explicitly types 'bye'.
        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim().toLowerCase(); // Read and clean user input

            // Check if the user wants to exit.
            if (userInput.equals("bye")) {
                System.out.println("Chatbot: Goodbye! It was nice talking to you.");
                break; // Exit the loop
            }

            // Get a response based on learned preferences or a default.
            String response = getResponse(userInput);
            System.out.println("Chatbot: " + response);

            // If the chatbot didn't have a specific response, it will ask for one.
            // This is where the learning happens.
            if (response.equals("I'm not sure how to respond to that. What should I say?")) {
                System.out.print("Chatbot: What is a good response for '" + userInput + "'? ");
                String learnedResponse = scanner.nextLine().trim();
                // Store the newly learned preference.
                learnedPreferences.put(userInput, learnedResponse);
                System.out.println("Chatbot: Thanks, I'll remember that!");
            }
        }
        // Close the scanner when done to prevent resource leaks.
        scanner.close();
    }

    // This method determines the chatbot's response.
    // It first checks if it has a learned response for the user's input.
    // If not, it returns a default "I don't know" message.
    private String getResponse(String userInput) {
        // Check if the user's input (or a part of it) is a key in our learnedPreferences map.
        // For simplicity, we directly check the exact user input here.
        // In a real chatbot, you'd use more sophisticated matching (e.g., keywords).
        if (learnedPreferences.containsKey(userInput)) {
            // If we have learned a response for this specific input, return it.
            return learnedPreferences.get(userInput);
        } else {
            // If we haven't learned a response for this input, return a default message.
            // This default message prompts the user to teach the chatbot.
            return "I'm not sure how to respond to that. What should I say?";
        }
    }

    // Example Usage:
    // To run this, compile the Java file (e.g., javac LearningChatbot.java)
    // and then run it (e.g., java LearningChatbot).
    //
    // Interaction Example:
    // You: hello
    // Chatbot: I'm not sure how to respond to that. What should I say?
    // Chatbot: What is a good response for 'hello'? Hi there!
    // Chatbot: Thanks, I'll remember that!
    // You: hello
    // Chatbot: Hi there!
    // You: how are you
    // Chatbot: I'm not sure how to respond to that. What should I say?
    // Chatbot: What is a good response for 'how are you'? I'm a chatbot, so I don't have feelings!
    // Chatbot: Thanks, I'll remember that!
    // You: how are you
    // Chatbot: I'm a chatbot, so I don't have feelings!
    // You: bye
    // Chatbot: Goodbye! It was nice talking to you.
}