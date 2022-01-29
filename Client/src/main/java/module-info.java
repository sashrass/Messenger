module com.messenger.minimessenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.naming;
    requires spring.security.crypto;
    requires dropbox.core.sdk;
    requires org.apache.commons.io;
    requires java.desktop;
    requires API;
    requires hessian;


    opens com.messenger.minimessenger to javafx.fxml;
    exports com.messenger.minimessenger;

}