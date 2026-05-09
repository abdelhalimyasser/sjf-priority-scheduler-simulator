package com.sjf_priority.model;

public class Process {
    private int id;
    private int priority;
    private int burstTime;
    private int arrivalTime;

    private int waitingTime    = 0;
    private int remainingTime  = 0;
    private int turnaroundTime = 0;
    private int completionTime = 0;
    private int responseTime   = -1;

    // Main Constructor to initialize the process attributes
    public Process(int id, int priority, int burstTime, int arrivalTime) {
        this.id = id;
        this.priority = priority;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.remainingTime = burstTime;
    }

    // Constructor to copy an existing process, used in some algorithms to create a copy of the process list
    public Process(Process source) {
        this.id = source.id;
        this.priority = source.priority;
        this.burstTime = source.burstTime;
        this.arrivalTime = source.arrivalTime;
        this.remainingTime = source.burstTime;
        this.responseTime = -1;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public int getBurstTime() { return burstTime; }
    public void setBurstTime(int burstTime) { this.burstTime = burstTime; }

    public int getArrivalTime() {return arrivalTime; }
    public void setArrivalTime(int arrivalTime) {this.arrivalTime = arrivalTime; }

    public int getWaitingTime() {return waitingTime; }
    public void setWaitingTime(int waitingTime) {this.waitingTime = waitingTime; }

    public int getRemainingTime() {return remainingTime; }
    public void setRemainingTime(int remainingTime) {this.remainingTime = remainingTime; }

    public int getTurnaroundTime() {return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) {this.turnaroundTime = turnaroundTime; }

    public int getCompletionTime() {return completionTime; }
    public void setCompletionTime(int completionTime) {this.completionTime = completionTime; }

    public int getResponseTime() {return responseTime; }
    public void setResponseTime(int responseTime) {this.responseTime = responseTime; }

    @Override
    public String toString() {
        return String.format("Process ID: " + id
                + "\nPriority: " + priority
                + "\nBurst Time: " + burstTime
                + "\nArrival Time: " + arrivalTime
                + "\nWaiting Time: " + waitingTime
                + "\nRemaining Time: " + remainingTime
                + "\nTurnaround Time: " + turnaroundTime
                + "\nCompletion Time: " + completionTime
                + "\nResponse Time: " + responseTime
        );
    }
}
