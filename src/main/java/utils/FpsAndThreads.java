package utils;

import java.util.Set;

public final class FpsAndThreads {
    /** Somme de tick. */
    private static long fpsCheckTimer = 0;
    /** Frames totales depuis le dernier check. */
    private static int totalFrames = 0;

    /** Somme de tick. */
    private static long threadCheckTimer = 0;
    /** Ensemble de{@code Thread}. */
    private static Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

    /** Une nanoseconde. */
    private static final double NANOSECONDS = Math.pow(10, 9);

    private FpsAndThreads() {
    }

    /**
     * Incrémente {@link #fpsCheckTimer} chaque tick.
     * Si {@link #fpsCheckTimer} dépasse une seconde, les FPS
     * sont print.
     * @param deltaTns Un tick
     */
    public static void updateFps(final long deltaTns) {
        FpsAndThreads.fpsCheckTimer += deltaTns;
        FpsAndThreads.totalFrames += 1;
        if (fpsCheckTimer / NANOSECONDS > 1) {
            System.out.println("FPS : " + totalFrames);
            totalFrames = 0;
            fpsCheckTimer = 0;
        }
    }

    /**
     * Incrémente {@link #threadCheckTimer} chaque tick.
     * Si {@link #threadCheckTimer} dépasse les 5 secondes, les Threads
     * sont print.
     * @param deltaTns Un tick
     */
    public static void updateThread(final long deltaTns) {
        final int frequency = 5;
        FpsAndThreads.threadCheckTimer += deltaTns;
        if (threadCheckTimer / NANOSECONDS > frequency) {
            System.out.println("\n THREADS : " + threadSet);
            threadCheckTimer = 0;
        }
    }
}
