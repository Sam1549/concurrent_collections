package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static final String LETTERS = "abc";
    public static final int LENGTH = 10_000;
    public static final int ROUTE = 100_000;
    public static ArrayBlockingQueue<String> listA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> listB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> listC = new ArrayBlockingQueue<>(100);

    public static AtomicInteger maxA = new AtomicInteger(0);
    public static AtomicInteger maxB = new AtomicInteger(0);
    public static AtomicInteger maxC = new AtomicInteger(0);

    public static void main(String[] args) {

        new Thread(() -> {
            for (int i = 0; i < ROUTE; i++) {
                String text = generateText(LETTERS, LENGTH);
                try {
                    listA.put(text);
                    listB.put(text);
                    listC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        threadStart(listA, 'a', maxA);
        threadStart(listB, 'b', maxB);
        threadStart(listC, 'c', maxC);


    }

    public static void counter(String text, char symb, AtomicInteger max) {
        int count = (int) text.chars().filter(ch -> ch == symb).count();
        if (max.get() < count) {
            max.set(count);
        }

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void threadStart(ArrayBlockingQueue<String> arrayBlockingQueue, char symb, AtomicInteger atomicInteger) {
        new Thread(() -> {
            for (int i = 0; i < ROUTE; i++) {
                try {
                    String rout = arrayBlockingQueue.take();
                    counter(rout, symb, atomicInteger);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.format("Самое большое количество символов '%c' в строке: %d \n", symb, atomicInteger.get());
        }).start();
    }
}
