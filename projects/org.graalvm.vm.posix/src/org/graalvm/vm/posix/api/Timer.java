package org.graalvm.vm.posix.api;

public class Timer {
    private int clockid;
    private Sigevent ev;
    private Timespec interval;
    private Timespec value;

    public Timer(int clockid, Sigevent ev) {
        this.clockid = clockid;
        this.ev = ev;
        interval = new Timespec();
        value = new Timespec();
    }

    public int getClockId() {
        return clockid;
    }

    public Sigevent getSigevent() {
        return ev;
    }

    public void destroy() {
        // TODO
    }

    public void setInterval(Timespec ts) {
        interval = new Timespec(ts);
    }

    public void setValue(Timespec ts) {
        value = new Timespec(ts);
    }

    public Timespec getInterval() {
        return interval;
    }

    public Timespec getValue() {
        return value;
    }
}
