<<<<<<< HEAD
package com.sjf_priority.scheduler;

import com.sjf_priority.contract.CpuScheduler;
import com.sjf_priority.model.ExecutionRecord;
import com.sjf_priority.model.Process;

import java.util.ArrayList;
import java.util.List;

public class PriorityScheduling implements CpuScheduler {
	// default constructor that initializes the scheduler as non-preemptive
	public PriorityScheduling() {}

	private Process setHighestPriority(List<Process> temp, Process highestPriority, int currentTime) {
		// iterate over each process in the processes
		for (Process p : temp) {
			// check if the arrival time is less that the current time
			if (p.getArrivalTime() <= currentTime) {
				// if there is not any process OR the priority of the new process is slower than the current then update the highest priority process
				if (highestPriority == null || p.getPriority() < highestPriority.getPriority()) {
					highestPriority = p;
				}
				// if the new process and the current process have the same priority, then we will execute the one that arrived first
				else if (p.getPriority() == highestPriority.getPriority() && p.getArrivalTime() < highestPriority.getArrivalTime()) {
					highestPriority = p;
				}
			}
		}
		return highestPriority;
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
			Process highestPriority = null;

			highestPriority = setHighestPriority(temp, highestPriority, currentTime);

			// check if there is not any process then jump into the next process and update the time
			if (highestPriority == null) {
				currentTime++;
				continue;
			}

			// Context Switch Detection
			if (currentRunning != highestPriority) {
				// check if there is running process AND the remaining time is greater than 0, then add it to the execution record
				if (currentRunning != null && currentRunning.getRemainingTime() > 0) {
					executionRecords.add(new ExecutionRecord(currentRunning.getId(), currentBlockStartTime, currentTime));
				}

				// update the current running process and the start time of the new block
				currentRunning = highestPriority;
				currentBlockStartTime = currentTime;

				// if the response time is not set yet, set it to the current time minus the arrival time
				if (highestPriority.getResponseTime() == -1) {
					highestPriority.setResponseTime(currentTime - highestPriority.getArrivalTime());
				}
			}

			highestPriority.setRemainingTime(highestPriority.getRemainingTime() - 1);
			currentTime++;

			if (highestPriority.getRemainingTime() == 0) {
				completedCount++;
				executionRecords.add(new ExecutionRecord(highestPriority.getId(), currentBlockStartTime, currentTime));

				highestPriority.setCompletionTime(currentTime);
				highestPriority.setTurnaroundTime(highestPriority.getCompletionTime() - highestPriority.getArrivalTime());
				highestPriority.setWaitingTime(highestPriority.getTurnaroundTime() - highestPriority.getBurstTime());

				temp.remove(highestPriority);
				currentRunning = null;
			}
		}

		return executionRecords;
	}
=======
package com.sjf_priority.scheduler;

import com.sjf_priority.contract.CpuScheduler;
import com.sjf_priority.model.ExecutionRecord;
import com.sjf_priority.model.Process;

import java.util.ArrayList;
import java.util.List;

public class PriorityScheduling implements CpuScheduler {
	// default constructor that initializes the scheduler as non-preemptive
	public PriorityScheduling() {}

	private Process setHighestPriority(List<Process> temp, Process highestPriority, int currentTime) {
		// iterate over each process in the processes
		for (Process p : temp) {
			// check if the arrival time is less that the current time
			if (p.getArrivalTime() <= currentTime) {
				// if there is not any process OR the priority of the new process is slower than the current then update the highest priority process
				if (highestPriority == null || p.getPriority() < highestPriority.getPriority()) {
					highestPriority = p;
				}
				// if the new process and the current process have the same priority, then we will execute the one that arrived first
				else if (p.getPriority() == highestPriority.getPriority() && p.getArrivalTime() < highestPriority.getArrivalTime()) {
					highestPriority = p;
				}
			}
		}
		return highestPriority;
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
			Process highestPriority = null;

			highestPriority = setHighestPriority(temp, highestPriority, currentTime);

			// check if there is not any process then jump into the next process and update the time
			if (highestPriority == null) {
				currentTime++;
				continue;
			}

			// Context Switch Detection
			if (currentRunning != highestPriority) {
				// check if there is running process AND the remaining time is greater than 0, then add it to the execution record
				if (currentRunning != null && currentRunning.getRemainingTime() > 0) {
					executionRecords.add(new ExecutionRecord(currentRunning.getId(), currentBlockStartTime, currentTime));
				}

				// update the current running process and the start time of the new block
				currentRunning = highestPriority;
				currentBlockStartTime = currentTime;

				// if the response time is not set yet, set it to the current time minus the arrival time
				if (highestPriority.getResponseTime() == -1) {
					highestPriority.setResponseTime(currentTime - highestPriority.getArrivalTime());
				}
			}

			highestPriority.setRemainingTime(highestPriority.getRemainingTime() - 1);
			currentTime++;

			if (highestPriority.getRemainingTime() == 0) {
				completedCount++;
				executionRecords.add(new ExecutionRecord(highestPriority.getId(), currentBlockStartTime, currentTime));

				highestPriority.setCompletionTime(currentTime);
				highestPriority.setTurnaroundTime(highestPriority.getCompletionTime() - highestPriority.getArrivalTime());
				highestPriority.setWaitingTime(highestPriority.getTurnaroundTime() - highestPriority.getBurstTime());

				temp.remove(highestPriority);
				currentRunning = null;
			}
		}

		return executionRecords;
	}
>>>>>>> a7315e91bd65aba9141279da63a07af177371016
}