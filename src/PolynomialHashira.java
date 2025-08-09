import java.io.*;
import java.util.*;

public class PolynomialHashira {

    public static void main(String[] args) throws Exception {
        // Read entire JSON file into a string
        String json = readFile("testcase.json");

        // Step 1: Extract n and k
        int n = extractInt(json, "\"n\":");
        int k = extractInt(json, "\"k\":");

        // Step 2: Extract all points (x, y)
        List<int[]> points = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (!json.contains("\"" + i + "\":")) continue;
            int base = extractInt(json, "\"base\":", "\"" + i + "\":");
            String valueStr = extractString(json, "\"value\":", "\"" + i + "\":");

            // Decode value from given base
            long decodedY = decodeBase(valueStr, base);
            points.add(new int[]{i, (int) decodedY});
        }

        // Step 3: Use first k points to find the constant term (secret C)
        double secret = lagrangeInterpolation(points.subList(0, k));

        System.out.println("Decoded points: " + points);
        System.out.println("Secret C = " + secret);
    }

    // --- Utility to read file ---
    private static String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
        }
        return sb.toString();
    }

    // --- Extract integer after a key ---
    private static int extractInt(String json, String key) {
        int idx = json.indexOf(key) + key.length();
        StringBuilder num = new StringBuilder();
        while (idx < json.length() && !Character.isDigit(json.charAt(idx))) idx++;
        while (idx < json.length() && Character.isDigit(json.charAt(idx))) {
            num.append(json.charAt(idx++));
        }
        return Integer.parseInt(num.toString());
    }

    // --- Extract integer from inside an object by key ---
    private static int extractInt(String json, String key, String sectionStart) {
        int secIdx = json.indexOf(sectionStart);
        int idx = json.indexOf(key, secIdx) + key.length();
        StringBuilder num = new StringBuilder();
        while (idx < json.length() && !Character.isDigit(json.charAt(idx))) idx++;
        while (idx < json.length() && Character.isDigit(json.charAt(idx))) {
            num.append(json.charAt(idx++));
        }
        return Integer.parseInt(num.toString());
    }

    // --- Extract string value ---
    private static String extractString(String json, String key, String sectionStart) {
        int secIdx = json.indexOf(sectionStart);
        int idx = json.indexOf(key, secIdx) + key.length();
        while (idx < json.length() && json.charAt(idx) != '"') idx++;
        idx++; // skip first quote
        StringBuilder val = new StringBuilder();
        while (idx < json.length() && json.charAt(idx) != '"') {
            val.append(json.charAt(idx++));
        }
        return val.toString();
    }

    // --- Decode a number from given base ---
    private static long decodeBase(String value, int base) {
        String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
        long result = 0;
        for (char c : value.toLowerCase().toCharArray()) {
            int digit = digits.indexOf(c);
            if (digit < 0 || digit >= base) throw new IllegalArgumentException("Invalid digit: " + c);
            result = result * base + digit;
        }
        return result;
    }

    // --- Lagrange Interpolation to get f(0) ---
    private static double lagrangeInterpolation(List<int[]> points) {
        double result = 0.0;
        for (int i = 0; i < points.size(); i++) {
            double term = points.get(i)[1];
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    term *= (0.0 - points.get(j)[0]) / (points.get(i)[0] - points.get(j)[0]);
                }
            }
            result += term;
        }
        return result;
    }
}
