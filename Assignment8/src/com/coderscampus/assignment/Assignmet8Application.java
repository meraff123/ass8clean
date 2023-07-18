package com.coderscampus.assignment;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Assignmet8Application {
    private List<Integer> numbers;

    public Assignmet8Application() {
        numbers = new ArrayList<>();
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void readDataFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                int number = Integer.parseInt(line);
                numbers.add(number);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Integer> countOccurrences() {
        Map<Integer, Integer> occurrences = new HashMap<>();
        for (int number : numbers) {
            occurrences.put(number, occurrences.getOrDefault(number, 0) + 1);
        }
        return occurrences;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Assignmet8Application assignment = new Assignmet8Application();
        assignment.readDataFromFile();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Map<Integer, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Future<Map<Integer, Integer>> future = executorService.submit(assignment::countOccurrences);
            futures.add(future);
        }

        Map<Integer, Integer> finalOccurrences = new HashMap<>();

        for (Future<Map<Integer, Integer>> future : futures) {
            Map<Integer, Integer> occurrences = future.get();
            for (Map.Entry<Integer, Integer> entry : occurrences.entrySet()) {
                int number = entry.getKey();
                int count = entry.getValue();
                finalOccurrences.put(number, finalOccurrences.getOrDefault(number, 0) + count);
            }
        }

        for (int number : assignment.getNumbers()) {
            if (!finalOccurrences.containsKey(number)) {
                finalOccurrences.put(number, 0);
            }
        }

        for (Map.Entry<Integer, Integer> entry : finalOccurrences.entrySet()) {
            int number = entry.getKey();
            int count = entry.getValue();
            System.out.println(number + "=" + count);
        }

        executorService.shutdown();
    }
}

