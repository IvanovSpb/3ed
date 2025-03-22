package org.example;

import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {
        // Input Parameters for the Computation
        double initialValueA = 3.0;
        double initialValueB = 4.0;
        double parameterC = 10.0;
        double parameterD = 16.0;

        orchestrateCalculation(initialValueA, initialValueB, parameterC, parameterD);
    }

    public static void orchestrateCalculation(double a, double b, double c, double d) {

        // Input Validation: C and D should be positive values.
        if (c <= 0) {
            System.out.println("Error: Parameter C must be greater than zero.");
            return;
        }
        if (d <= 0) {
            System.out.println("Error: Parameter D must be greater than zero.");
            return;
        }

        // Asynchronous Task Creation using CompletableFuture
        CompletableFuture<Double> squaredSumFuture = CompletableFuture.supplyAsync(() -> computeSquaredSum(a, b));
        CompletableFuture<Double> naturalLogFuture = CompletableFuture.supplyAsync(() -> computeNaturalLog(c));
        CompletableFuture<Double> squareRootFuture = CompletableFuture.supplyAsync(() -> computeSquareRoot(d));

        // Combining Results and Performing Final Calculation
        double squaredSum = squaredSumFuture.join();
        double naturalLog = naturalLogFuture.join();
        double squareRoot = squareRootFuture.join();

        double finalResult = squaredSum * naturalLog / squareRoot;
        System.out.println("The overall computed result is: " + finalResult);
    }

    private static double computeSquaredSum(double a, double b) {
        try {
            // Simulating Latency: Pausing for 5 seconds
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Restore interrupted state
        }

        double result = a * a + b * b;
        System.out.println("Calculation: a^2 + b^2 = " + result);
        return result;
    }

    private static double computeNaturalLog(double c) {
        try {
            // Emulating Delay: Waiting for 15 seconds
            Thread.sleep(15000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();  // Maintain interruption status
        }

        double result = Math.log(c);
        System.out.println("Computation: Natural Log of c is " + result);
        return result;
    }

    private static double computeSquareRoot(double d) {
        try {
            // Faking Work: Delaying for 10 seconds
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Ensure interruption handling
        }

        double result = Math.sqrt(d);
        System.out.println("Calculation: Square Root of d is " + result);
        return result;
    }
}