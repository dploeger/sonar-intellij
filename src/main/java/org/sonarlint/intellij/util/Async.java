package org.sonarlint.intellij.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class Async {
    private static final int FIFTY_MS = 50;

    private Async() {
    }

    @NotNull
    public static <T> T asyncResultOf(@NotNull final Callable<T> callable,
                                      @NotNull final T defaultValue) {
        try {
            return whenFinished(executeOnPooledThread(callable)).get();

        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static <T> Future<T> executeOnPooledThread(final Callable<T> callable) {
        return ApplicationManager.getApplication().executeOnPooledThread(callable);
    }

    private static <T> Future<T> whenFinished(final Future<T> future) {
        while (!future.isDone() && !future.isCancelled()) {
            ProgressManager.checkCanceled();
            waitFor(FIFTY_MS);
        }
        return future;
    }

    private static void waitFor(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            // ignored
        }
    }
}