module com.sjf_priority {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens com.sjf_priority to javafx.fxml;
    opens com.sjf_priority.controllers to javafx.fxml;
<<<<<<< HEAD
=======
    opens com.sjf_priority.model to javafx.base;
>>>>>>> a7315e91bd65aba9141279da63a07af177371016

    exports com.sjf_priority;
    exports com.sjf_priority.controllers;
    exports com.sjf_priority.model;
<<<<<<< HEAD
}
=======
}
>>>>>>> a7315e91bd65aba9141279da63a07af177371016
