package com.example.yavor.naxexmobile;

import android.support.v4.content.Loader;

/**
 * Created by mitevyav.
 */
public class PollSymbolsThread implements Runnable {

    private final long DELAY = 500; // milliseconds

    private final Loader loader;

    private volatile boolean isRunning = false;

    private Thread thread;

    PollSymbolsThread(Loader loader) {
        this.loader = loader;

        isRunning = true;
        thread = new Thread(this, "PollEventsThread");
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        isRunning = false;

        thread.interrupt();
        thread = null;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!isRunning) {
                    return;
                }

                try {
                    wait(DELAY);
                } catch (InterruptedException e) {
                    isRunning = false;

                    // When interrupted we don't want to call forceLoad() method anymore
                    // and we want to stop execution immediately.
                    return;
                }

                loader.forceLoad();
            }
        }
    }
}
