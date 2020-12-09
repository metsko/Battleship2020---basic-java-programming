package view;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
public class ErrorMessage {

	public static void message(String errorMessage) {
    JOptionPane.showMessageDialog(new JFrame(), errorMessage, "Error",
        JOptionPane.ERROR_MESSAGE);
	}
}