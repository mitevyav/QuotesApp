package com.example.yavor.naxexmobile;

import android.support.v4.content.Loader;

/**
 * Created by mitevyav.
 */
public class PollSymbolsThread implements Runnable {

    private final long DELAY = 500; // milliseconds

    private final Loader loader;

    /**
     * If the request takes too long time to response we need to prevent the restart of loader
     * which will prevent the loader from terminating ever.
     */
    private volatile boolean isLoaderStarted = false;

    private volatile boolean isRunning = false;

    private Thread thread;

    PollSymbolsThread(Loader loader) {
        this.loader = loader;

        isRunning = true;
        thread = new Thread(this, "PollEventsThread");
    }

    public void setLoaderStarted(boolean loaderStarted) {
        isLoaderStarted = loaderStarted;
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

                if (!isLoaderStarted) {
                    loader.forceLoad();
                }
            }
        }
    }
}
