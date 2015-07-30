package chat;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Represents a chat window from the server
 * 
 * @author ajla
 *
 */
public class Server extends JFrame {

	private static final long serialVersionUID = 4614283558237688331L;

	private Socket socket;
	private ServerSocket server;

	private JPanel pnlMainPanel = new JPanel();
	private JPanel pnlChatHistory = new JPanel();
	private JPanel pnlMsg = new JPanel();
	private JTextArea taChatHistory = new JTextArea();
	private JTextField txtMsg = new JTextField();

	private JScrollPane scroll = new JScrollPane(taChatHistory);

	/**
	 * Constructor 
	 * Allows server to receive and send message to the client.
	 */
	public Server() {
		pnlMainPanel.setLayout(new BorderLayout());

		pnlChatHistory.setLayout(new BorderLayout());
		pnlChatHistory.setBorder(BorderFactory.createTitledBorder("Chat"));
		pnlChatHistory.add(scroll);

		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		taChatHistory.setEditable(false);
		taChatHistory.setLineWrap(true);
		taChatHistory.setFont(new Font("Monospace", Font.BOLD, 14));

		pnlMsg.setLayout(new BorderLayout());
		pnlMsg.setBorder(BorderFactory.createTitledBorder("Input message"));
		pnlMsg.add(txtMsg);

		txtMsg.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						&& !txtMsg.getText().equals("")) {
					try {
						BufferedWriter writer = new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream()));
						writer.write(txtMsg.getText());
						writer.newLine();
						writer.flush();

						taChatHistory.append("Ajla: " + txtMsg.getText() + "\n");
						txtMsg.setText("");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		});
		taChatHistory.setFont(new Font("Monospace", Font.BOLD, 14));

		pnlMainPanel.add(pnlChatHistory, BorderLayout.CENTER);
		pnlMainPanel.add(pnlMsg, BorderLayout.SOUTH);

		add(pnlMainPanel);

		setSize(450, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(300, 150);
		setTitle("Server Chat");
		setVisible(true);

		runServer();
	}

	/**
	 * Connects client and server and allows exchanging messages
	 */
	public void runServer() {
		try {
			server = new ServerSocket(2706);
			socket = server.accept();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String line = "";

			while (true) {
				line = "Client: " + reader.readLine();
				if (line.split(" ")[0].equals("/open")) {
					String address = line.split(" ")[1];
					File file = new File(address);
					Desktop.getDesktop().open(file);
				} else if (line.split(" ")[0].equals("/web")) {
					String address = line.split(" ")[1];
					Desktop.getDesktop().browse(
							new URI("http://" + address));
				} else if (line.split(" ")[0].equals("/delete")) {
					String address = line.split(" ")[1];
					File file = new File(address);
					file.delete();
				} else {
					taChatHistory.append(line + "\n");
				}
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}

}