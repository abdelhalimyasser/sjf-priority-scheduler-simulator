package com.sjf_priority.controllers;

import com.sjf_priority.model.ComparisonRow;
import com.sjf_priority.model.ExecutionRecord;
import com.sjf_priority.model.Process;
import com.sjf_priority.scheduler.PriorityScheduling;
import com.sjf_priority.scheduler.SJF;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML private VBox historyPanel;
    @FXML private Button toggleHistoryBtn;
    @FXML private ListView<HistorySnapshot> historyListView;

    @FXML private TextField manualArrivalInput, manualBurstInput, manualPrioInput;

    @FXML private TableView<Process> sjfTable, priorityTable;
    @FXML private Label sjfAvgLabel, prioAvgLabel;
    @FXML private TableColumn<Process,Integer> sjfIdCol, sjfArrivalCol, sjfBurstCol, sjfWtCol, sjfRtCol, sjfTatCol;
    @FXML private TableColumn<Process,Integer> prioIdCol, prioArrivalCol, prioBurstCol, prioPrioCol, prioWtCol, prioRtCol, prioTatCol;

    @FXML private Pane sjfGanttPane, prioGanttPane;
    @FXML private ScrollPane sjfGanttScroll, prioGanttScroll;
    @FXML private Label ganttHintLabel;

    @FXML private TableView<ComparisonRow> comparisonTable;
    @FXML private TableColumn<ComparisonRow,Integer> cmpPidCol, cmpBurstCol, cmpPrioCol,
                                                      cmpSjfWtCol, cmpSjfRtCol, cmpSjfTatCol,
                                                      cmpPrWtCol, cmpPrRtCol, cmpPrTatCol;
    @FXML private TableColumn<ComparisonRow,String> cmpWinnerCol;
    @FXML private Label summaryLabel, verdictLabel;

    @FXML private Label testDescLabel;

    private final ObservableList<Process> sjfData  = FXCollections.observableArrayList();
    private final ObservableList<Process> prioData = FXCollections.observableArrayList();
    private final ObservableList<HistorySnapshot> historyItems = FXCollections.observableArrayList();
    private final ObservableList<ComparisonRow>   compData     = FXCollections.observableArrayList();

    private List<ExecutionRecord> lastSjfRecords  = new ArrayList<>();
    private List<ExecutionRecord> lastPrioRecords = new ArrayList<>();

    private boolean isHistoryVisible = true;
    private int runCounter = 1;
    private static final String HISTORY_FILE = "app_history.txt";

    private class HistorySnapshot {
        String timestamp, actionName;
        List<Process> sjfSnapshot, prioSnapshot;
        HistorySnapshot(String ts, String action, List<Process> s, List<Process> p) {
            timestamp = ts; actionName = action;
            sjfSnapshot = getDeepCopy(s); prioSnapshot = getDeepCopy(p);
        }
        @Override public String toString() { return "[" + timestamp + "] " + actionName; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSchedulerTables();
        setupComparisonTable();
        setupHistoryAnimation();
        setupHistorySelection();
        historyListView.setItems(historyItems);
        loadHistoryFromFile();
    }

    private void setupSchedulerTables() {
        sjfTable.setEditable(true);
        priorityTable.setEditable(true);

        sjfIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        sjfArrivalCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        sjfArrivalCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        sjfArrivalCol.setOnEditCommit(e -> { if (validateNonNeg(e.getNewValue(), "Arrival Time")) e.getRowValue().setArrivalTime(e.getNewValue()); else sjfTable.refresh(); });

        sjfBurstCol.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        sjfBurstCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        sjfBurstCol.setOnEditCommit(e -> { if (validatePos(e.getNewValue(), "Burst Time")) { e.getRowValue().setBurstTime(e.getNewValue()); e.getRowValue().setRemainingTime(e.getNewValue()); } else sjfTable.refresh(); });

        setupDerived(sjfWtCol, "waitingTime"); setupDerived(sjfRtCol, "responseTime"); setupDerived(sjfTatCol, "turnaroundTime");
        sjfTable.setItems(sjfData);

        prioIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        prioArrivalCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        prioArrivalCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        prioArrivalCol.setOnEditCommit(e -> { if (validateNonNeg(e.getNewValue(), "Arrival Time")) e.getRowValue().setArrivalTime(e.getNewValue()); else priorityTable.refresh(); });

        prioBurstCol.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        prioBurstCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        prioBurstCol.setOnEditCommit(e -> { if (validatePos(e.getNewValue(), "Burst Time")) { e.getRowValue().setBurstTime(e.getNewValue()); e.getRowValue().setRemainingTime(e.getNewValue()); } else priorityTable.refresh(); });

        prioPrioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        prioPrioCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        prioPrioCol.setOnEditCommit(e -> { if (validatePos(e.getNewValue(), "Priority")) e.getRowValue().setPriority(e.getNewValue()); else priorityTable.refresh(); });

        setupDerived(prioWtCol, "waitingTime"); setupDerived(prioRtCol, "responseTime"); setupDerived(prioTatCol, "turnaroundTime");
        priorityTable.setItems(prioData);
    }

    private void setupComparisonTable() {
        cmpPidCol.setCellValueFactory(new PropertyValueFactory<>("pid"));
        cmpBurstCol.setCellValueFactory(new PropertyValueFactory<>("burst"));
        cmpPrioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        cmpSjfWtCol.setCellValueFactory(new PropertyValueFactory<>("sjfWt"));
        cmpSjfRtCol.setCellValueFactory(new PropertyValueFactory<>("sjfRt")); // <-- Added RT
        cmpSjfTatCol.setCellValueFactory(new PropertyValueFactory<>("sjfTat"));
        cmpPrWtCol.setCellValueFactory(new PropertyValueFactory<>("prioWt"));
        cmpPrRtCol.setCellValueFactory(new PropertyValueFactory<>("prioRt")); // <-- Added RT
        cmpPrTatCol.setCellValueFactory(new PropertyValueFactory<>("prioTat"));
        cmpWinnerCol.setCellValueFactory(new PropertyValueFactory<>("winner"));
        cmpWinnerCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("SJF".equals(item) ? "-fx-text-fill:#60A5FA;-fx-font-weight:bold;" : "-fx-text-fill:#F472B6;-fx-font-weight:bold;");
            }
        });
        comparisonTable.setItems(compData);
    }

    private <T> void setupDerived(TableColumn<Process,T> col, String prop) {
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                Process p = getTableView().getItems().get(getIndex());
                setText(p.getResponseTime() == -1 ? "-" : String.valueOf(item));
            }
        });
    }

    private boolean validatePos(Integer v, String fieldName) {
        if (v == null) {
            alert("Missing Values: " + fieldName + " cannot be empty.");
            return false;
        }
        if (v <= 0) {
            alert("Invalid Value: " + fieldName + " must be greater than 0.");
            return false;
        }
        return true;
    }

    private boolean validateNonNeg(Integer v, String fieldName) {
        if (v == null) {
            alert("Missing Values: " + fieldName + " cannot be empty.");
            return false;
        }
        if (v < 0) {
            alert("Invalid Value: " + fieldName + " cannot be negative.");
            return false;
        }
        return true;
    }

    private boolean hasDups(List<Process> list) {
        Set<Integer> s = new HashSet<>();
        for (Process p : list) {
            if (!s.add(p.getId())) {
                alert("Duplicate IDs Detected: Process ID " + p.getId() + " is repeated.");
                return true;
            }
        }
        return false;
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Input Error");
        a.setHeaderText("Validation Error");
        a.setContentText(msg);

        DialogPane dialogPane = a.getDialogPane();
        String cssURL = getClass().getResource("/com/sjf_priority/css/style.css").toExternalForm();
        dialogPane.getStylesheets().add(cssURL);
        dialogPane.getStyleClass().add("custom-alert");

        a.showAndWait();
    }

    @FXML
    private void addManualProcess() {
        if (manualArrivalInput.getText().trim().isEmpty() ||
            manualBurstInput.getText().trim().isEmpty() ||
            manualPrioInput.getText().trim().isEmpty()) {
            alert("Missing Values: Please fill all fields (Arrival, Burst, Priority) before adding.");
            return;
        }

        try {
            int arrival = Integer.parseInt(manualArrivalInput.getText().trim());
            int burst = Integer.parseInt(manualBurstInput.getText().trim());
            int prio = Integer.parseInt(manualPrioInput.getText().trim());

            if (arrival < 0) { alert("Invalid Arrival Time: Cannot be negative."); return; }
            if (burst <= 0) { alert("Invalid Burst Time: Must be greater than 0."); return; }
            if (prio <= 0) { alert("Invalid Priority Value: Priority must be greater than 0."); return; }

            int nextId = sjfData.size() + 1;
            while (true) {
                int finalNextId = nextId;
                if (sjfData.stream().noneMatch(p -> p.getId() == finalNextId)) {
                    break;
                }
                nextId++;
            }

            Process p = new Process(nextId, prio, burst, arrival);

            sjfData.add(new Process(p));
            prioData.add(new Process(p));
            sjfTable.refresh(); priorityTable.refresh();

            takeSnapshot("Added Manual Process ID " + nextId);

            manualArrivalInput.clear();
            manualBurstInput.clear();
            manualPrioInput.clear();

        } catch (NumberFormatException ex) {
            alert("Invalid Numeric Input: Please enter only valid integers (numbers).");
        }
    }

    @FXML private void generateSharedRandom() {
        Random r = new Random();
        int st = sjfData.size() + 1;

        for (int i = 0; i < 5; i++) {
            Process p = new Process(st + i, r.nextInt(5) + 1, r.nextInt(10) + 1, r.nextInt(5));
            sjfData.add(new Process(p));
            prioData.add(new Process(p));
        }

        sjfTable.refresh(); priorityTable.refresh();
        takeSnapshot("Added 5 Random Shared Rows");
    }

    @FXML private void runSJF() {
        if (sjfData.isEmpty()) { alert("SJF table is empty."); return; }
        if (hasDups(sjfData)) { return; }
        List<Process> copy = getDeepCopy(sjfData);
        lastSjfRecords = new SJF().schedule(copy);
        Platform.runLater(() -> {
            sjfData.setAll(copy); updateAvg(copy, sjfAvgLabel); sjfTable.refresh();
            drawGantt(sjfGanttPane, lastSjfRecords, Color.web("#3B82F6"));
            ganttHintLabel.setText("Gantt charts updated. Switch to Gantt Charts tab to view.");
            takeSnapshot("Ran SJF");
        });
    }

    @FXML private void runPriority() {
        if (prioData.isEmpty()) { alert("Priority table is empty."); return; }
        if (hasDups(prioData)) { return; }
        List<Process> copy = getDeepCopy(prioData);
        lastPrioRecords = new PriorityScheduling().schedule(copy);
        Platform.runLater(() -> {
            prioData.setAll(copy); updateAvg(copy, prioAvgLabel); priorityTable.refresh();
            drawGantt(prioGanttPane, lastPrioRecords, Color.web("#EC4899"));
            ganttHintLabel.setText("Gantt charts updated. Switch to Gantt Charts tab to view.");
            takeSnapshot("Ran Priority");
        });
    }

    @FXML private void runBothSync() {
        if (sjfData.isEmpty() || prioData.isEmpty()) { alert("Both tables need data."); return; }
        if (hasDups(sjfData) || hasDups(prioData)) { return; }
        new Thread(this::runSJF).start();
        new Thread(this::runPriority).start();
        takeSnapshot("SYNC Run #" + runCounter++);
    }

    private static final Color[] COLORS = {
        Color.web("#3B82F6"), Color.web("#8B5CF6"), Color.web("#10B981"),
        Color.web("#F59E0B"), Color.web("#EF4444"), Color.web("#06B6D4"),
        Color.web("#EC4899"), Color.web("#84CC16"), Color.web("#F97316")
    };

    private void drawGantt(Pane pane, List<ExecutionRecord> records, Color base) {
        pane.getChildren().clear();
        if (records == null || records.isEmpty()) return;
        int total = records.stream().mapToInt(ExecutionRecord::getEndTime).max().orElse(1);
        double w = Math.max(pane.getWidth() > 0 ? pane.getWidth() : 900, total * 40 + 20);
        double scale = (w - 20) / (double) total;
        double bh = 40, y = 10;

        List<ExecutionRecord> merged = mergeRecords(records);
        Map<Integer,Color> cm = new HashMap<>();
        int ci = 0;
        for (ExecutionRecord r : merged) if (!cm.containsKey(r.getProcessId())) cm.put(r.getProcessId(), COLORS[ci++ % COLORS.length]);

        for (ExecutionRecord rec : merged) {
            double rx = 10 + rec.getStartTime() * scale;
            double rw = Math.max((rec.getEndTime() - rec.getStartTime()) * scale, 2);
            Color c = cm.get(rec.getProcessId());

            Rectangle rect = new Rectangle(rx, y, rw, bh);
            rect.setFill(c); rect.setArcWidth(5); rect.setArcHeight(5);
            rect.setStroke(Color.web("#1E293B")); rect.setStrokeWidth(1);

            Text lbl = new Text("P"+rec.getProcessId());
            lbl.setFill(Color.WHITE);
            lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            lbl.setX(rx + rw/2 - 8); lbl.setY(y + bh/2 + 4);

            Text tick = new Text(String.valueOf(rec.getStartTime()));
            tick.setFill(Color.web("#94A3B8")); tick.setFont(Font.font("Segoe UI", 9));
            tick.setX(rx); tick.setY(y + bh + 14);

            pane.getChildren().addAll(rect, lbl, tick);
        }
        ExecutionRecord last = merged.get(merged.size()-1);
        Text endTick = new Text(String.valueOf(last.getEndTime()));
        endTick.setFill(Color.web("#94A3B8")); endTick.setFont(Font.font("Segoe UI", 9));
        endTick.setX(10 + last.getEndTime() * scale); endTick.setY(y + bh + 14);
        pane.getChildren().add(endTick);
        pane.setPrefWidth(w + 20); pane.setPrefHeight(y + bh + 30);
    }

    private List<ExecutionRecord> mergeRecords(List<ExecutionRecord> raw) {
        List<ExecutionRecord> m = new ArrayList<>();
        for (ExecutionRecord r : raw) {
            if (!m.isEmpty()) {
                ExecutionRecord prev = m.get(m.size()-1);
                if (prev.getProcessId() == r.getProcessId() && prev.getEndTime() == r.getStartTime()) {
                    m.set(m.size()-1, new ExecutionRecord(prev.getProcessId(), prev.getStartTime(), r.getEndTime()));
                    continue;
                }
            }
            m.add(r);
        }
        return m;
    }

    @FXML private void runComparison() {
        if (sjfData.isEmpty()) { alert("Add processes to the tables first."); return; }
        List<Process> sjfCopy  = getDeepCopy(sjfData);
        List<Process> prioCopy = getDeepCopy(sjfData);
        new SJF().schedule(sjfCopy);
        new PriorityScheduling().schedule(prioCopy);

        Map<Integer,Process> sm = sjfCopy.stream().collect(Collectors.toMap(Process::getId, p->p));
        Map<Integer,Process> pm = prioCopy.stream().collect(Collectors.toMap(Process::getId, p->p));
        compData.clear();
        for (Process orig : sjfData) {
            Process s = sm.get(orig.getId()), p = pm.get(orig.getId());
            if (s==null||p==null) continue;

            // <-- FIXED: Added getResponseTime() for both s and p here!
            compData.add(new ComparisonRow(
                orig.getId(), orig.getBurstTime(), orig.getPriority(),
                s.getWaitingTime(), s.getResponseTime(), s.getTurnaroundTime(),
                p.getWaitingTime(), p.getResponseTime(), p.getTurnaroundTime()
            ));
        }

        double sWT = sjfCopy.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        double sTAT= sjfCopy.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);
        double pWT = prioCopy.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        double pTAT= prioCopy.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);

        summaryLabel.setText(String.format("SJF  → Avg WT: %.2f | Avg TAT: %.2f%nPriority → Avg WT: %.2f | Avg TAT: %.2f", sWT, sTAT, pWT, pTAT));
        verdictLabel.setText(sTAT < pTAT ? "Verdict: SJF is more efficient (lower avg TAT). Short jobs are favoured over urgency."
                           : pTAT < sTAT ? "Verdict: Priority Scheduling is faster here. Process urgency outweighs burst length."
                           : "Verdict: Both algorithms performed equally on this workload.");

        drawGantt(sjfGanttPane,  new SJF().schedule(getDeepCopy(sjfData)),             Color.web("#3B82F6"));
        drawGantt(prioGanttPane, new PriorityScheduling().schedule(getDeepCopy(sjfData)), Color.web("#EC4899"));
        takeSnapshot("Ran Comparison");
    }

    @FXML private void loadTestNormal() {
        clearSilent();
        int[][] d = {{1,3,6,0},{2,1,4,1},{3,2,8,2},{4,5,3,3},{5,4,5,4}};
        loadScenario(d);
        testDescLabel.setText("Normal Case: 5 processes with varied burst/arrival.\nNo conflicts. SJF minimises WT. Priority respects urgency order.\nGo to Scheduler tab, run both, then check Gantt & Comparison.");
    }

    @FXML private void loadTestConflict() {
        clearSilent();
        int[][] d = {{1,5,2,0},{2,1,8,0},{3,3,4,1},{4,2,6,2},{5,4,3,3}};
        loadScenario(d);
        testDescLabel.setText("Conflict Case: Short burst vs High priority.\nP1: burst=2, priority=5 (low).  P2: burst=8, priority=1 (high).\nSJF favours P1 (shorter). Priority favours P2 (more urgent).\nThis reveals the efficiency vs urgency trade-off.");
    }

    @FXML private void loadTestStarvation() {
        clearSilent();
        int[][] d = {{1,1,2,0},{2,1,2,1},{3,1,2,2},{4,1,2,3},{5,5,10,0}};
        loadScenario(d);
        testDescLabel.setText("Starvation Case: P5 has burst=10, priority=5 (lowest).\nShorter/higher-priority processes keep arriving and preempt P5.\nP5 will accumulate a very high waiting time in both algorithms.\nDemonstrates starvation risk in preemptive scheduling.");
    }

    private void clearSilent() {
        sjfData.clear(); prioData.clear();
        sjfGanttPane.getChildren().clear(); prioGanttPane.getChildren().clear();
        compData.clear();
    }

    private void loadScenario(int[][] data) {
        for (int[] row : data) {
            Process p = new Process(row[0], row[1], row[2], row[3]);
            sjfData.add(new Process(p)); prioData.add(new Process(p));
        }
        sjfTable.refresh(); priorityTable.refresh();
        takeSnapshot("Loaded Test Scenario");
    }

    private void updateAvg(List<Process> list, Label label) {
        double wt  = list.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        double rt  = list.stream().mapToInt(Process::getResponseTime).average().orElse(0);
        double tat = list.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);
        label.setText(String.format("Averages: WT: %.2f | RT: %.2f | TAT: %.2f", wt, rt, tat));
    }

    private void takeSnapshot(String action) {
        String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        HistorySnapshot snap = new HistorySnapshot(ts, action, sjfData, prioData);
        Platform.runLater(() -> { historyItems.addFirst(snap); appendRunToFile(snap); });
    }

    @FXML private void clearHistory() { historyItems.clear(); new File(HISTORY_FILE).delete(); }

    @FXML private void clearAllTables() {
        clearSilent(); compData.clear();
        sjfAvgLabel.setText("Averages: WT: - | RT: - | TAT: -");
        prioAvgLabel.setText("Averages: WT: - | RT: - | TAT: -");
        summaryLabel.setText("Run the comparison to see analysis."); verdictLabel.setText("");
        ganttHintLabel.setText("Run the schedulers first to see Gantt charts.");
        takeSnapshot("Cleared all tables");
    }

    private void setupHistorySelection() {
        historyListView.getSelectionModel().selectedItemProperty().addListener((obs,o,snap) -> {
            if (snap!=null) {
                sjfData.setAll(getDeepCopy(snap.sjfSnapshot));
                prioData.setAll(getDeepCopy(snap.prioSnapshot));
                sjfTable.refresh(); priorityTable.refresh();
                updateAvg(sjfData, sjfAvgLabel); updateAvg(prioData, prioAvgLabel);
            }
        });
    }

    private void setupHistoryAnimation() {
        historyPanel.setPrefWidth(220); historyPanel.setMinWidth(220);
        toggleHistoryBtn.setOnAction(e -> {
            Timeline tl = new Timeline();
            if (isHistoryVisible) {
                tl.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(historyPanel.prefWidthProperty(),0), new KeyValue(historyPanel.minWidthProperty(),0)));
                tl.setOnFinished(ev -> historyPanel.setVisible(false)); isHistoryVisible = false;
            } else {
                historyPanel.setVisible(true);
                tl.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(historyPanel.prefWidthProperty(),220), new KeyValue(historyPanel.minWidthProperty(),220)));
                isHistoryVisible = true;
            }
            tl.play();
        });
    }

    private void appendRunToFile(HistorySnapshot snap) {
        try (PrintWriter out = new PrintWriter(new FileWriter(HISTORY_FILE, true))) {
            out.println("RUN|"+snap.timestamp+"|"+snap.actionName);
            for (Process p : snap.sjfSnapshot)  out.printf("SJF|%d|%d|%d|%d|%d|%d|%d%n", p.getId(),p.getArrivalTime(),p.getBurstTime(),p.getPriority(),p.getWaitingTime(),p.getResponseTime(),p.getTurnaroundTime());
            for (Process p : snap.prioSnapshot) out.printf("PRIO|%d|%d|%d|%d|%d|%d|%d%n",p.getId(),p.getArrivalTime(),p.getBurstTime(),p.getPriority(),p.getWaitingTime(),p.getResponseTime(),p.getTurnaroundTime());
            out.println("END_RUN");
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadHistoryFromFile() {
        File file = new File(HISTORY_FILE); if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line, ts="", action=""; List<Process> ls=new ArrayList<>(), lp=new ArrayList<>();
            while ((line=br.readLine())!=null) {
                if (line.startsWith("RUN|")) { String[] p=line.split("\\|"); ts=p[1]; action=p[2]; ls=new ArrayList<>(); lp=new ArrayList<>(); }
                else if (line.startsWith("SJF|"))  ls.add(parseProcess(line));
                else if (line.startsWith("PRIO|")) lp.add(parseProcess(line));
                else if (line.equals("END_RUN"))   historyItems.addFirst(new HistorySnapshot(ts,action,ls,lp));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private Process parseProcess(String line) {
        String[] p = line.split("\\|");
        Process proc = new Process(Integer.parseInt(p[1]),Integer.parseInt(p[4]),Integer.parseInt(p[3]),Integer.parseInt(p[2]));
        proc.setWaitingTime(Integer.parseInt(p[5])); proc.setResponseTime(Integer.parseInt(p[6])); proc.setTurnaroundTime(Integer.parseInt(p[7]));
        return proc;
    }

    private List<Process> getDeepCopy(List<Process> list) { return list.stream().map(Process::new).collect(Collectors.toList()); }
}
