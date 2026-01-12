// Dungeon Generation Tutorial: Procedural Room Placement

// Learning Objective:
// This tutorial demonstrates a simple, yet effective, method for procedurally
// generating a randomized dungeon layout by placing rectangular rooms randomly
// within a grid. We'll cover basic array manipulation, random number generation,
// and collision detection between rooms.

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class DungeonGenerator {

    // Constants defining the dungeon's dimensions and room properties
    private static final int DUNGEON_WIDTH = 50;
    private static final int DUNGEON_HEIGHT = 30;
    private static final int MAX_ROOMS = 10;
    private static final int MIN_ROOM_SIZE = 5;
    private static final int MAX_ROOM_SIZE = 10;

    // Represents a single rectangular room in the dungeon
    static class Room {
        int x, y, width, height; // Coordinates of the top-left corner and dimensions

        // Constructor to create a room with random dimensions and position
        public Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        // Checks if this room overlaps with another room
        public boolean intersects(Room other) {
            // The logic here is to check if there's ANY overlap.
            // Two rectangles *don't* overlap if one is entirely to the left,
            // right, above, or below the other. We invert this logic to find overlap.
            return this.x < other.x + other.width &&
                   this.x + this.width > other.x &&
                   this.y < other.y + other.height &&
                   this.y + this.height > other.y;
        }
    }

    // Main method to generate and print the dungeon
    public static void main(String[] args) {
        // Initialize the dungeon grid with walls (represented by '#')
        char[][] dungeon = new char[DUNGEON_HEIGHT][DUNGEON_WIDTH];
        for (int y = 0; y < DUNGEON_HEIGHT; y++) {
            for (int x = 0; x < DUNGEON_WIDTH; x++) {
                dungeon[y][x] = '#'; // Initialize entire grid as wall
            }
        }

        // List to store the successfully placed rooms
        List<Room> rooms = new ArrayList<>();
        Random random = new Random(); // For generating random numbers

        // Attempt to place rooms
        for (int i = 0; i < MAX_ROOMS; i++) {
            // Generate random dimensions for the room
            int roomWidth = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + MIN_ROOM_SIZE;
            int roomHeight = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + MIN_ROOM_SIZE;

            // Generate random top-left corner coordinates, ensuring the room stays within bounds
            // We subtract room size to avoid placing rooms partially off the edge.
            int x = random.nextInt(DUNGEON_WIDTH - roomWidth);
            int y = random.nextInt(DUNGEON_HEIGHT - roomHeight);

            // Create a potential new room
            Room newRoom = new Room(x, y, roomWidth, roomHeight);

            // Check if the new room overlaps with any existing rooms
            boolean collision = false;
            for (Room existingRoom : rooms) {
                if (newRoom.intersects(existingRoom)) {
                    collision = true;
                    break; // No need to check further if a collision is found
                }
            }

            // If no collision, place the room on the dungeon grid and add it to our list
            if (!collision) {
                // Carve out the room (replace walls with floor tiles '.')
                for (int ry = 0; ry < newRoom.height; ry++) {
                    for (int rx = 0; rx < newRoom.width; rx++) {
                        dungeon[newRoom.y + ry][newRoom.x + rx] = '.'; // '.' represents floor
                    }
                }
                rooms.add(newRoom); // Add the successfully placed room
            }
        }

        // Print the generated dungeon
        System.out.println("Generated Dungeon Layout:");
        for (int y = 0; y < DUNGEON_HEIGHT; y++) {
            for (int x = 0; x < DUNGEON_WIDTH; x++) {
                System.out.print(dungeon[y][x]);
            }
            System.out.println(); // Newline after each row
        }
    }
}