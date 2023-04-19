import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        Thread printedThread = new Thread(() -> {
            while (!Thread.interrupted()){
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    int countRepetitionMaxValue = 0;
                    for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                        if (entry.getValue() > countRepetitionMaxValue) {
                            countRepetitionMaxValue = entry.getValue();
                        }
                    }
                    for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                        if (entry.getValue() == countRepetitionMaxValue) {
                            System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n",
                                    entry.getKey(), countRepetitionMaxValue);
                        }
                    }
                    System.out.println();
                }
            }
        });
        printedThread.start();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                    String route = generateRoute("RLRFR", 100);
                    int count = 0;
                    for (int c = 0; c < route.length(); c++) {
                        if (route.charAt(c) == 'R') {
                            count++;
                        }
                    }
                    synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(count)) {
                        sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                    } else {
                        sizeToFreq.put(count, 1);
                    }
                    sizeToFreq.notify();
                }
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
            Thread.sleep(10);
        }

        for (Thread thread : threads) {
            thread.join();
        }
        printedThread.interrupt();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
