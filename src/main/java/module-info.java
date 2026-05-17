module com.sjf_priority {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens com.sjf_priority to javafx.fxml;
    opens com.sjf_priority.controllers to javafx.fxml;

    exports com.sjf_priority;
    exports com.sjf_priority.controllers;
    exports com.sjf_priority.model;
}