package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Main2 {

    record Transaction(int fromId, int toId, int amount) {}

    static class TransactionProcessor implements Runnable {
        private final Transaction transaction;
        private final AtomicIntegerArray balances;
        private final Random random = new Random();

        public TransactionProcessor(Transaction transaction, AtomicIntegerArray balances) {
            this.transaction = transaction;
            this.balances = balances;
        }

        @Override
        public void run() {
            int fromId = transaction.fromId;
            int toId = transaction.toId;
            int amount = transaction.amount;

            if (amount <= 0) {
                System.out.println("Invalid transaction amount: " + amount);
                return;
            }

            while (true) {
                int currentBalance = balances.get(fromId);
                if (currentBalance < amount) {
                    System.out.println("Transaction failed. User " + fromId + " has insufficient funds.");
                    return;
                }

                int newBalance = currentBalance - amount;
                if (balances.compareAndSet(fromId, currentBalance, newBalance)) {
                    // Simulate some processing time (e.g., database operation, network latency)
                    try {
                        Thread.sleep(random.nextInt(50)); // Sleep for up to 50ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    balances.getAndAdd(toId, amount);
                    System.out.println("Transaction: User " + fromId + " -> User " + toId + ", Amount: " + amount);
                    return;
                } else {
                    // Another thread modified the balance before we could complete the transaction.
                    // Retry the transaction. To avoid being trapped, let's add a very short delay.
                    try {
                        Thread.sleep(random.nextInt(10)); // short retry delay to avoid being trapped
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of users: ");
        int numberOfUsers = scanner.nextInt();

        AtomicIntegerArray balances = new AtomicIntegerArray(numberOfUsers);
        for (int i = 0; i < numberOfUsers; i++) {
            System.out.println("Enter initial balance for User " + i + ":");
            int initialBalance = scanner.nextInt();
            balances.set(i, Math.max(0, initialBalance)); // Ensure initial balance is not negative
        }

        System.out.println("Enter number of transactions: ");
        int numberOfTransactions = scanner.nextInt();
        scanner.nextLine();

        List<Transaction> transactions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numberOfTransactions; i++) {
            // Generate random transactions
            int fromId = random.nextInt(numberOfUsers);
            int toId = random.nextInt(numberOfUsers);
            while (toId == fromId) {
                toId = random.nextInt(numberOfUsers); // Ensure from and to are different
            }
            int amount = random.nextInt(100) + 1; // Random amount between 1 and 100

            transactions.add(new Transaction(fromId, toId, amount));
        }

        scanner.close();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(numberOfTransactions, 10)); // Limit thread pool size

        for (Transaction transaction : transactions) {
            executor.submit(new TransactionProcessor(transaction, balances));
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted: " + e.getMessage());
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("\nFinal balances:");
        for (int i = 0; i < numberOfUsers; i++) {
            System.out.println("User " + i + " final balance: " + balances.get(i));
        }
    }
}