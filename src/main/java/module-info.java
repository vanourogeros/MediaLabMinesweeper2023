module com.medialab.medialabminesweeper2023 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.medialab.medialabminesweeper2023 to javafx.fxml;
    exports com.medialab.medialabminesweeper2023;
}