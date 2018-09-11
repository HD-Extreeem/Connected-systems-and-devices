import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ClientGui {
    private JFrame frame;
    private JTextField textField;
    private JTextArea textArea;
    private JButton btnSend;
    private JLabel status;
    //private String server_ip = "192.168.20.247";
    //private String port = "24";
    private ClientThread clientThread;
    /**
     * Method for starting the whole process
     */
    public void start() {
        initializeGUI();

        clientThread = new ClientThread(btnSend,status);
        clientThread.setIsRunning(true);
        Thread cliThread  =  new Thread(clientThread);
        cliThread.start();

        frame.setVisible(true);
    }

    /**
     * Initializes the gui
     */
    private void initializeGUI() {
        frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.ORANGE);
        frame.setBounds(600, 250, 480, 370);

        status = new JLabel(" Status: Not Connected! ");
        status.setBounds(150, 5, 137, 20);
        status.setBackground(Color.RED);
        status.setOpaque(true);
        frame.add(status);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
               // poolClient.setIsRunning(false);
                //poolClient.stop();
                System.out.println("Closing client bye!");
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}
