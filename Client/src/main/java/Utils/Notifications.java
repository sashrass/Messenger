package Utils;

import Data.Message;

import java.awt.*;

public class Notifications
{
    public static void showMessage(Message message)
    {
        java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage("/main/resources/message.png");


        java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Tray Demo");

        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        try
        {
            tray.add(trayIcon);
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
        trayIcon.displayMessage(message.getSenderName(), message.getText(), java.awt.TrayIcon.MessageType.INFO);
        tray.remove(trayIcon);
    }
}
