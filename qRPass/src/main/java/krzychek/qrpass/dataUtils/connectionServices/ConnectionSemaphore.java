package krzychek.qrpass.dataUtils.connectionServices;

import java.util.concurrent.Semaphore;

public class ConnectionSemaphore {
    private static final int MAX_AVAILABLE = 10;
    private static ConnectionSemaphore instance;
    private Semaphore semaphore;

    private ConnectionSemaphore() {
        semaphore = new Semaphore(MAX_AVAILABLE, true);
    }

    public static ConnectionSemaphore getInstance() {
        if (instance == null) {
            instance = new ConnectionSemaphore();
        }
        return instance;
    }

    public void acquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        semaphore.release();
    }
}
