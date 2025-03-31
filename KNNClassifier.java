import java.io.*;
import java.util.*;

public class KNNClassifier {
    private List<double[]> trainingData = new ArrayList<>();
    private List<String> trainingLabels = new ArrayList<>();
    private List<double[]> testData = new ArrayList<>();
    private List<String> testLabels = new ArrayList<>();

    public void loadData(String fileName, boolean isTraining) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                double[] attributes = new double[parts.length - 1];

                for (int i = 0; i < parts.length - 1; i++) {
                    attributes[i] = Double.parseDouble(parts[i].replace(",", "."));
                }

                if (isTraining) {
                    trainingData.add(attributes);
                    trainingLabels.add(parts[parts.length - 1]);
                } else {
                    testData.add(attributes);
                    testLabels.add(parts[parts.length - 1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
        }
    }

    private double eDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private static class Neighbor {
        double distance;
        String label;

        Neighbor(double distance, String label) {
            this.distance = distance;
            this.label = label;
        }
    }

    public String classify(double[] sample, int k) {
        PriorityQueue<Neighbor> neighbors = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        for (int i = 0; i < trainingData.size(); i++) {
            double distance = eDistance(sample, trainingData.get(i));
            neighbors.add(new Neighbor(distance, trainingLabels.get(i)));
        }

        Map<String, Integer> classCount = new HashMap<>();
        for (int i = 0; i < k && !neighbors.isEmpty(); i++) {
            Neighbor neighbor = neighbors.poll();
            classCount.put(neighbor.label, classCount.getOrDefault(neighbor.label, 0) + 1);
        }

        return classCount.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .get().getKey();
    }

    public double evaluateModel(int k) {
        int correct = 0;
        for (int i = 0; i < testData.size(); i++) {
            String predicted = classify(testData.get(i), k);
            if (predicted.equals(testLabels.get(i))) {
                correct++;
            }
        }

        return 100.0 * correct / testData.size();
    }
}
