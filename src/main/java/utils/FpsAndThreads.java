package utils;

import java.util.Set;

public class FpsAndThreads {
    private static long lastFpsCheck = 0;
    private static int totalFrames = 0;

    private static long lastThreadCheck = 0;
    private static Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

    private FpsAndThreads() {}

    public static void updateFps(long deltaTns) {
        FpsAndThreads.lastFpsCheck += deltaTns;
        FpsAndThreads.totalFrames += 1;
        if (lastFpsCheck / Math.pow(10, 9) > 1) {
            System.out.println("FPS : " + totalFrames);
            totalFrames = 0;
            lastFpsCheck = 0;
        }
    }

    public static void updateThread(long deltaTns) {
        FpsAndThreads.lastThreadCheck += deltaTns;
        if (lastThreadCheck / Math.pow(10, 9) > 5) {
            System.out.println("\n THREADS : " + threadSet);
            lastThreadCheck = 0;
        }
    }
}