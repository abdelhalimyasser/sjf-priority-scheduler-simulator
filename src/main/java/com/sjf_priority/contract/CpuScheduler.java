package com.sjf_priority.contract;

import com.sjf_priority.model.ExecutionRecord;
import com.sjf_priority.model.Process;
import java.util.List;

public interface CpuScheduler {
    List<ExecutionRecord> schedule(List<Process> processes);
}
