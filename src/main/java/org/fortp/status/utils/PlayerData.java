package org.fortp.status.utils;

public interface PlayerData {
    int status$getAvailability();
    void status$setAvailability(int availability);

    int status$getStatus();
    void status$setStatus(int status);

    boolean status$getNoSleep();
    void status$setNoSleep(boolean noSleep);
}
