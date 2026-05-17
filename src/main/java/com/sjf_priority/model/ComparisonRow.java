package com.sjf_priority.model;

public class ComparisonRow {
    private final int pid;
    private final int burst;
    private final int priority;
    private final int sjfWt;
    private final int sjfRt;
    private final int sjfTat;
    private final int prioWt;
    private final int prioRt;
    private final int prioTat;
    private final String winner;

    public ComparisonRow(int pid,
                         int burst, int priority,
                         int sjfWt, int sjfRt, int sjfTat,
                         int prioWt, int prioRt, int prioTat) {
        this.pid      = pid;
        this.burst    = burst;
        this.priority = priority;
        this.sjfWt    = sjfWt;
        this.sjfRt    = sjfRt;
        this.sjfTat   = sjfTat;
        this.prioWt   = prioWt;
        this.prioRt   = prioRt;
        this.prioTat  = prioTat;
        this.winner   = sjfTat <= prioTat ? "SJF" : "Priority";
    }

    public int getPid()     { return pid; }
    public int getBurst()   { return burst; }
    public int getPriority(){ return priority; }

    public int getSjfWt()   { return sjfWt; }
    public int getSjfRt()   { return sjfRt; }
    public int getSjfTat()  { return sjfTat; }

    public int getPrioWt()  { return prioWt; }
    public int getPrioRt()  { return prioRt; }
    public int getPrioTat() { return prioTat; }

    public String getWinner()  { return winner; }
}