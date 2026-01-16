// --- Learning Objective: Fetching and Displaying Live Stock Data ---
// This tutorial will guide you through building a simple Java application
// that fetches real-time stock data from a public API and displays it
// using a basic charting library. We will focus on the fundamental steps
// of making HTTP requests, parsing JSON responses, and visualizing data.
// This is a great starting point for understanding how to integrate
// external data into your Java applications and create dynamic user interfaces.

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// For charting, we'll use a very basic approach for this example.
// In a real-world application, you'd likely use a dedicated charting library
// like JFreeChart or JavaFX Charts. For simplicity here, we'll just
// print a text-based representation.
public class LiveStockFetcher {

    // The API endpoint for fetching stock data.
    // IMPORTANT: You'll need to get your own free API key from a provider like Alpha Vantage,
    // Finnhub, or others. Replace "YOUR_API_KEY" with your actual key.
    // This example uses a placeholder URL.
    private static final String STOCK_API_URL_TEMPLATE = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=YOUR_API_KEY";

    // Represents a single data point for a stock (timestamp and price).
    private static class StockDataPoint {
        String timestamp;
        double price;

        StockDataPoint(String timestamp, double price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        @Override
        public String toString() {
            return timestamp + ": " + String.format("%.2f", price);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter stock symbol (e.g., IBM, AAPL): ");
        String stockSymbol = scanner.nextLine().toUpperCase(); // Get user input and convert to uppercase

        try {
            List<StockDataPoint> stockData = fetchStockData(stockSymbol); // Call our data fetching method

            if (stockData != null && !stockData.isEmpty()) {
                System.out.println("\n--- Live Stock Data for " + stockSymbol + " ---");
                displayChart(stockData); // Call our method to display the data
            } else {
                System.out.println("Could not fetch stock data for " + stockSymbol + ". Please check the symbol and your API key.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
        } finally {
            scanner.close(); // Close the scanner to release resources
        }
    }

    // Fetches stock data from the API.
    // This method demonstrates how to make an HTTP GET request and parse a JSON response.
    private static List<StockDataPoint> fetchStockData(String symbol) throws Exception {
        // Construct the full API URL with the provided stock symbol.
        String apiUrl = String.format(STOCK_API_URL_TEMPLATE, symbol);
        URL url = new URL(apiUrl); // Create a URL object from the string.

        // Open a connection to the URL. This is the first step in making an HTTP request.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // Specify the HTTP method (GET for retrieving data).
        connection.setRequestProperty("User-Agent", "Java StockFetcher"); // Set a user agent for better compatibility.

        // Get the response code (e.g., 200 for OK, 404 for Not Found).
        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode);

        // Check if the request was successful (response code 200).
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Use BufferedReader to read the response from the input stream.
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line); // Append each line of the response to a StringBuilder.
            }
            in.close(); // Close the BufferedReader.

            // Here's where JSON parsing would happen in a real application.
            // For this simple example, we'll simulate parsing.
            // In a production app, you'd use a library like Jackson or Gson.
            // Example JSON structure for Alpha Vantage TIME_SERIES_INTRADAY:
            // {
            //   "Time Series (5min)": {
            //     "2023-10-27 10:00:00": {
            //       "1. open": "150.0000",
            //       "2. high": "151.5000",
            //       "3. low": "149.8000",
            //       "4. close": "151.0000",
            //       "5. volume": "100000"
            //     },
            //     // ... more data points
            //   }
            // }

            // Basic simulation of parsing the 'close' price from the JSON.
            // This is a very fragile approach and only works for a specific JSON format.
            List<StockDataPoint> dataPoints = new ArrayList<>();
            String timeSeriesKey = "\"Time Series (5min)\": {";
            int timeSeriesStartIndex = response.indexOf(timeSeriesKey);

            if (timeSeriesStartIndex != -1) {
                String timeSeriesJson = response.substring(timeSeriesStartIndex + timeSeriesKey.length());
                // This is a highly simplified way to extract data.
                // Real JSON parsing would be robust.
                int dataPointEndIndex = timeSeriesJson.indexOf("}");
                if (dataPointEndIndex != -1) {
                    String dataPointsJson = timeSeriesJson.substring(0, dataPointEndIndex);
                    String[] entries = dataPointsJson.split("\\},\\s*\""); // Split by closing brace and quote
                    for (String entry : entries) {
                        String[] parts = entry.split(":");
                        if (parts.length >= 2) {
                            String timestamp = parts[0].replace("\"", "").trim();
                            String priceStr = parts[1].split(",")[3].split("\"")[1]; // Extracting the "close" price
                            try {
                                double price = Double.parseDouble(priceStr);
                                dataPoints.add(new StockDataPoint(timestamp, price));
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing price: " + priceStr);
                            }
                        }
                    }
                }
            }
            return dataPoints; // Return the list of parsed data points.
        } else {
            // If the response code is not OK, print an error message.
            System.err.println("HTTP Error: " + responseCode + " - " + connection.getResponseMessage());
            return null; // Return null to indicate an error.
        }
    }

    // Displays a simple text-based chart of the stock data.
    // This is a very basic visualization.
    private static void displayChart(List<StockDataPoint> dataPoints) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }

        // Determine the range of prices for scaling the chart.
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        for (StockDataPoint point : dataPoints) {
            if (point.price < minPrice) minPrice = point.price;
            if (point.price > maxPrice) maxPrice = point.price;
        }

        // Define the height of our text-based chart.
        final int CHART_HEIGHT = 10;
        double priceRange = maxPrice - minPrice;

        // Iterate through the data points and create rows for the chart.
        for (int i = CHART_HEIGHT - 1; i >= 0; i--) {
            double priceLevel = minPrice + (priceRange * (double) i / (CHART_HEIGHT - 1));
            System.out.printf("%.2f | ", priceLevel); // Print the price level for the row.

            // Draw a marker if a data point falls within this price level.
            for (StockDataPoint point : dataPoints) {
                // This is a very crude way to check if a point is "on" this line.
                // In a real chart, you'd scale points to specific pixel positions.
                if (point.price >= priceLevel && point.price < priceLevel + (priceRange / (CHART_HEIGHT - 1)) * 1.1) {
                    System.out.print("*"); // Use an asterisk to represent a data point.
                } else {
                    System.out.print(" "); // Otherwise, print a space.
                }
            }
            System.out.println(); // Move to the next line for the next price level.
        }

        // Print the minimum and maximum prices below the chart for reference.
        System.out.printf("%.2f |", minPrice);
        for (int i = 0; i < dataPoints.size(); i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.printf("%.2f |", maxPrice);
        for (int i = 0; i < dataPoints.size(); i++) {
            System.out.print("-");
        }
        System.out.println();


        // Print the actual data points below the chart.
        System.out.println("\nDetailed Data:");
        for (StockDataPoint point : dataPoints) {
            System.out.println(point);
        }
    }
}

// --- Example Usage ---
// To run this application:
// 1. Save the code as LiveStockFetcher.java
// 2. Obtain an API key from a stock data provider (e.g., Alpha Vantage).
// 3. Replace "YOUR_API_KEY" in the STOCK_API_URL_TEMPLATE with your actual API key.
// 4. Compile the code: javac LiveStockFetcher.java
// 5. Run the compiled code: java LiveStockFetcher
//
// When prompted, enter a stock symbol like "IBM" or "AAPL".
// The application will attempt to fetch and display the latest 5-minute interval data.
//
// --- Key Concepts Learned ---
// - Making HTTP GET requests using HttpURLConnection.
// - Reading data from network streams.
// - Basic string manipulation for parsing (though JSON libraries are recommended for real applications).
// - Creating simple data structures (StockDataPoint).
// - Designing a rudimentary text-based visualization.
// - Handling potential errors (e.g., network issues, invalid symbols).
// - User input handling with Scanner.
// - Using `try-catch-finally` for resource management and error handling.