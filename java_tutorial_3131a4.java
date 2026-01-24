// Learning Objective: This tutorial will teach you how to simulate basic physics for moving objects and implement simple collision detection between them.
// We will create a simple scenario where a player-controlled spaceship defends against incoming alien ships.
// We'll use Java's `java.awt.geom.Rectangle2D` for easy collision detection and `java.awt.Point` for positions.

import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

// Represents a generic game entity with a position, velocity, and size.
class GameObject {
    // The current position of the object on the screen.
    Point position;
    // The speed and direction of the object. Units are pixels per frame.
    Point velocity;
    // The bounding box of the object, used for collision detection.
    Rectangle2D bounds;
    // A unique identifier for the object. Useful for managing game entities.
    String id;

    // Constructor to initialize a GameObject.
    public GameObject(String id, int x, int y, int width, int height, int vx, int vy) {
        this.id = id;
        this.position = new Point(x, y);
        this.velocity = new Point(vx, vy);
        // Initialize the bounding box based on position and size.
        this.bounds = new Rectangle2D.Double(x, y, width, height);
    }

    // Updates the object's position based on its velocity.
    // This is our basic physics simulation step.
    public void update() {
        // Move the object by adding its velocity to its current position.
        this.position.x += this.velocity.x;
        this.position.y += this.velocity.y;

        // Crucially, update the bounding box to match the new position.
        // Otherwise, collision detection will be based on old positions!
        this.bounds.setRect(this.position.x, this.position.y, this.bounds.getWidth(), this.bounds.getHeight());
    }

    // Checks if this object is colliding with another object.
    public boolean collidesWith(GameObject other) {
        // Rectangle2D provides a convenient 'intersects' method for collision.
        // It returns true if the rectangles overlap, meaning a collision occurred.
        return this.bounds.intersects(other.bounds);
    }

    // A simple way to represent the object as a string for debugging.
    @Override
    public String toString() {
        return id + " at (" + position.x + "," + position.y + ") with velocity (" + velocity.x + "," + velocity.y + ")";
    }
}

// Represents the player's spaceship. It can move and shoot.
class PlayerShip extends GameObject {
    // The player's ship has a fixed size.
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;

    // Constructor for the player ship. Starts at the bottom center of a hypothetical screen.
    public PlayerShip(int x, int y) {
        // Player ship has no initial velocity, it's controlled by input.
        super("Player", x, y, WIDTH, HEIGHT, 0, 0);
    }

    // Allows the player to move left.
    public void moveLeft() {
        this.velocity.x = -5; // Move 5 pixels to the left per update.
    }

    // Allows the player to move right.
    public void moveRight() {
        this.velocity.x = 5; // Move 5 pixels to the right per frame.
    }

    // Stops horizontal movement.
    public void stopHorizontalMovement() {
        this.velocity.x = 0;
    }

    // Player ships don't typically move vertically in simple games, but you could add this.
}

// Represents an enemy alien ship. It moves downwards and shoots.
class AlienShip extends GameObject {
    // Alien ships have a fixed size.
    private static final int WIDTH = 25;
    private static final int HEIGHT = 25;
    // The speed at which aliens move downwards.
    private static final int ALIEN_SPEED = 2;

    // Constructor for an alien ship. Spawns at the top with downward velocity.
    public AlienShip(String id, int x, int y) {
        // Aliens move downwards.
        super(id, x, y, WIDTH, HEIGHT, 0, ALIEN_SPEED);
    }
}

// Manages the game state, including the player and alien ships.
class GameSimulation {
    // The player's spaceship.
    private PlayerShip player;
    // A list to hold all active alien ships.
    private List<AlienShip> aliens;
    // A list to hold all active player projectiles (e.g., lasers).
    private List<GameObject> projectiles;

    // Game screen dimensions for boundary checks.
    private final int screenWidth;
    private final int screenHeight;

    // Constructor for the game simulation.
    public GameSimulation(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.aliens = new ArrayList<>();
        this.projectiles = new ArrayList<>();

        // Create the player ship at the bottom center of the screen.
        this.player = new PlayerShip(screenWidth / 2 - PlayerShip.WIDTH / 2, screenHeight - PlayerShip.HEIGHT - 10);
    }

    // Adds a new alien ship to the game.
    public void addAlien(int x, int y) {
        aliens.add(new AlienShip("Alien_" + aliens.size(), x, y));
    }

    // Adds a player projectile to the game.
    public void fireProjectile(int x, int y) {
        // Projectiles move upwards. We'll define a simple projectile object.
        // For simplicity, we'll use GameObject with appropriate properties.
        int projectileWidth = 5;
        int projectileHeight = 15;
        // Projectiles move upwards, hence negative Y velocity.
        GameObject projectile = new GameObject("Projectile", x - projectileWidth / 2, y, projectileWidth, projectileHeight, 0, -10);
        projectiles.add(projectile);
    }

    // This is the core game loop update method.
    // It simulates physics, handles collisions, and cleans up dead objects.
    public void updateGame() {
        // 1. Update all game objects' positions (physics simulation).
        player.update();
        for (AlienShip alien : aliens) {
            alien.update();
        }
        for (GameObject projectile : projectiles) {
            projectile.update();
        }

        // 2. Handle collisions.
        handleCollisions();

        // 3. Remove off-screen or destroyed objects.
        cleanupObjects();
    }

    // Checks for and resolves collisions between different game entities.
    private void handleCollisions() {
        // Collision between player projectiles and aliens.
        // We use an Iterator to safely remove elements while iterating.
        Iterator<GameObject> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            GameObject projectile = projectileIterator.next();
            Iterator<AlienShip> alienIterator = aliens.iterator();
            while (alienIterator.hasNext()) {
                AlienShip alien = alienIterator.next();
                // If a projectile hits an alien:
                if (projectile.collidesWith(alien)) {
                    System.out.println("HIT: " + projectile.id + " hit " + alien.id);
                    // Remove both the projectile and the alien.
                    projectileIterator.remove();
                    alienIterator.remove();
                    // Once an alien is hit, we don't need to check it against other projectiles.
                    break;
                }
            }
        }

        // Collision between player and aliens.
        // If the player hits an alien, the player might be destroyed or take damage (not implemented here).
        for (AlienShip alien : aliens) {
            if (player.collidesWith(alien)) {
                System.out.println("COLLISION: Player hit " + alien.id);
                // In a real game, you'd handle player damage or game over here.
                // For this tutorial, we'll just print the collision.
            }
        }
    }

    // Removes objects that are off-screen or have been destroyed.
    private void cleanupObjects() {
        // Remove projectiles that have gone off the top of the screen.
        projectiles.removeIf(p -> p.position.y < 0);

        // Remove aliens that have gone off the bottom of the screen.
        aliens.removeIf(a -> a.position.y > screenHeight);

        // Keep the player within screen bounds (optional, but good practice).
        // Ensure player X position stays within screen width.
        if (player.position.x < 0) {
            player.position.x = 0;
            player.bounds.setRect(player.position.x, player.position.y, player.bounds.getWidth(), player.bounds.getHeight());
        }
        if (player.position.x > screenWidth - player.bounds.getWidth()) {
            player.position.x = screenWidth - (int) player.bounds.getWidth();
            player.bounds.setRect(player.position.x, player.position.y, player.bounds.getWidth(), player.bounds.getHeight());
        }
    }

    // --- Getters for UI rendering (not implemented, but useful for a real game) ---
    public PlayerShip getPlayer() { return player; }
    public List<AlienShip> getAliens() { return aliens; }
    public List<GameObject> getProjectiles() { return projectiles; }
}


// Example Usage:
public class PhysicsAndCollisionDemo {

    public static void main(String[] args) {
        // Define the game screen dimensions.
        int screenWidth = 800;
        int screenHeight = 600;

        // Create an instance of our game simulation.
        GameSimulation game = new GameSimulation(screenWidth, screenHeight);

        // Add some initial alien ships.
        game.addAlien(100, 50);
        game.addAlien(300, 50);
        game.addAlien(500, 50);
        game.addAlien(700, 50);

        System.out.println("--- Starting Simulation ---");

        // Simulate a few game "frames" or updates.
        for (int frame = 0; frame < 200; frame++) {
            // Simulate player movement (example: moving right and then stopping)
            if (frame == 20) {
                game.getPlayer().moveRight();
            }
            if (frame == 60) {
                game.getPlayer().stopHorizontalMovement();
            }
            if (frame == 100) {
                game.getPlayer().moveLeft();
            }
            if (frame == 140) {
                game.getPlayer().stopHorizontalMovement();
            }

            // Simulate firing projectiles at certain frames.
            if (frame % 30 == 0 && frame > 0) {
                game.fireProjectile(game.getPlayer().position.x + game.getPlayer().bounds.getWidth() / 2, game.getPlayer().position.y);
                System.out.println("Frame " + frame + ": Player fired a projectile.");
            }

            // Update the game state. This is where physics and collisions are processed.
            game.updateGame();

            // Print game state for demonstration. In a real game, you would render this.
            if (frame % 50 == 0) {
                System.out.println("\n--- Frame " + frame + " ---");
                System.out.println("Player: " + game.getPlayer());
                System.out.println("Aliens: " + game.getAliens().size());
                for (AlienShip alien : game.getAliens()) {
                    System.out.println("  " + alien);
                }
                System.out.println("Projectiles: " + game.getProjectiles().size());
                for (GameObject proj : game.getProjectiles()) {
                    System.out.println("  " + proj);
                }
            }

            // In a real game, you would have a delay here to control the frame rate.
            // try { Thread.sleep(16); } catch (InterruptedException e) {}
        }

        System.out.println("\n--- Simulation Ended ---");
    }
}