import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Main {
    public static final String INPUT_TXT = "input.txt";
    public static final String OUTPUT_TXT = "output.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        Map<String, Double> cityPrices = new ConcurrentHashMap<>();
        Map<String, Map<String, Double>> cityProducts = new ConcurrentHashMap<>();
        try(Stream<String > lines = Files.lines(Paths.get(INPUT_TXT))) {
            lines.forEach(line -> {
                        String[] parts = line.split(",");
                        String city = parts[0];
                        String product = parts[1];
                        double price = Double.parseDouble(parts[2]);
                        cityProducts.putIfAbsent(city, new ConcurrentHashMap<>());
                        Map<String, Double> currentCity = cityProducts.get(city);
                        currentCity.putIfAbsent(product, Double.MAX_VALUE); // Initialize product price with max value
                        currentCity.computeIfPresent(product, (p, currentPrice) -> Math.min(currentPrice, price)); // Update product price if it's cheaper
                        cityPrices.put(city, cityPrices.getOrDefault(city, 0.0) + price);
                    });
        } catch (IOException e) {
            e.getMessage();
        }
        writeResultToFile(cityPrices, cityProducts);
        long end = System.currentTimeMillis();
        System.out.println("time: " + (end - start));
    }

    private static void writeResultToFile(Map<String, Double> cityPrices, Map<String,
            Map<String, Double>> cityProducts) throws IOException {
        StringBuilder result = new StringBuilder();
        cityPrices.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(1)
                .forEach(city -> {
                            result.append(city.getKey())
                                    .append(" ")
                                    .append(Math.floor(city.getValue()))
                                    .append("\n");
                            cityProducts.get(city.getKey()).entrySet()
                                    .stream()
                                    .sorted(Map.Entry.comparingByValue())
                                    .forEach(entry -> {
                                        result.append(entry.getKey())
                                                .append(" ")
                                                .append(Math.floor(entry.getValue()))
                                                .append("\n");
                                    });
                        }
                );
        Files.write(Paths.get(OUTPUT_TXT), result.toString().getBytes());
    }
}
