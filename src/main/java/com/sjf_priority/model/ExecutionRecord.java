package com.sjf_priority.model;


public final class ExecutionRecord {
    private final int processId;
    private final int startTime;
    private final int endTime;

    public ExecutionRecord(int processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getProcessId() {
        return processId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ExecutionRecord r = (ExecutionRecord) o;

        return processId == r.processId &&
               startTime == r.startTime &&
               endTime == r.endTime;
    }

    @Override
    public String toString() {
        return "ExecutionRecord[" +
                "processId=" + processId +
                ", startTime=" + startTime +
                ", endTime=" + endTime + ']';
    }
}