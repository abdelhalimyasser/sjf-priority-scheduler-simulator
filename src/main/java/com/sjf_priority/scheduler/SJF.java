package com.sjf_priority.scheduler;

import com.sjf_priority.contract.CpuScheduler;
import com.sjf_priority.model.ExecutionRecord;
import com.sjf_priority.model.Process;

import java.util.ArrayList;
import java.util.List;

public class SJF implements CpuScheduler {
	// default constructor that initializes the scheduler
	public SJF() {}

	private Process setShortest(List<Process> temp, Process shortest, int currentTime, boolean useRemainingTime) {
		// iterate over each process in the processes
		for (Process p : temp) {
			// check if the arrival time is less than or equal to the current time
			if (p.getArrivalTime() <= currentTime) {
				int pTime = useRemainingTime ? p.getRemainingTime() : p.getBurstTime();
				int shortestTime = shortest == null ? Integer.MAX_VALUE : (useRemainingTime ? shortest.getRemainingTime() : shortest.getBurstTime());

				// if there is not any process OR the time of the new process is shorter than the current then update the shortest process
				if (shortest == null || pTime < shortestTime) {
					shortest = p;
				}
				// if the new process and the current process have the same time, then we will execute the one that arrived first
				else if (pTime == shortestTime && p.getArrivalTime() < shortest.getArrivalTime()) {
					shortest = p;
				}
			}
		}
		return shortest;
	}

	@Override
	public List<ExecutionRecord> schedule(List<Process> processes) {
		List<ExecutionRecord> executionRecords = new ArrayList<>();
		List<Process> temp = new ArrayList<>(processes);

		for (Process p : temp) {
			p.setRemainingTime(p.getBurstTime());
		}

		int currentTime = 0;
		int completedCount = 0;
		int n = temp.size();

		Process currentRunning = null;
		int currentBlockStartTime = -1;

		while (completedCount < n) {
			Process shortest = null;

			shortest = setShortest(temp, shortest, currentTime, true);

			// check if there is not any process then jump into the next process and update the time
			if (shortest == null) {
				currentTime++;
				continue;
			}

			// Context Switch Detection
			if (currentRunning != shortest) {
				// check if there is running process AND the remaining time is greater than 0, then add it to the execution record
				if (currentRunning != null && currentRunning.getRemainingTime() > 0) {
					executionRecords.add(new ExecutionRecord(currentRunning.getId(), currentBlockStartTime, currentTime));
				}

				// update the current running process and the start time of the new block
				currentRunning = shortest;
				currentBlockStartTime = currentTime;

				// if the response time is not set yet, set it to the current time minus the arrival time
				if (shortest.getResponseTime() == -1) {
					shortest.setResponseTime(currentTime - shortest.getArrivalTime());
				}
			}

			shortest.setRemainingTime(shortest.getRemainingTime() - 1);
			currentTime++;

			if (shortest.getRemainingTime() == 0) {
				completedCount++;
				executionRecords.add(new ExecutionRecord(shortest.getId(), currentBlockStartTime, currentTime));

				shortest.setCompletionTime(currentTime);
				shortest.setTurnaroundTime(shortest.getCompletionTime() - shortest.getArrivalTime());
				shortest.setWaitingTime(shortest.getTurnaroundTime() - shortest.getBurstTime());

				temp.remove(shortest);
				currentRunning = null;
			}
		}

		return executionRecords;
	}
}