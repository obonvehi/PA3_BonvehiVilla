package ex01_CS;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class Client implements ActionListener {

	private JFrame frmGuessTheNumber;
	private JButton btnConnect;
	private JTextField textField;
	private JLabel lblGuess;
	private JButton btnSend;
	private JButton btnReset;
	private JButton btnTerminate;
	private JScrollPane scrollPane;
	private JTextArea messages;
	
	private Socket connection;
	private BufferedReader inputChannel;
	private PrintWriter outputChannel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frmGuessTheNumber.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.frmGuessTheNumber = new JFrame();
		this.frmGuessTheNumber.setTitle("GUESS THE NUMBER C/S-versions (socket based)");
		this.frmGuessTheNumber.setBounds(100, 100, 637, 417);
		this.frmGuessTheNumber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frmGuessTheNumber.getContentPane().setLayout(null);
		
		this.btnConnect = new JButton("Connect");
		this.btnConnect.addActionListener(this);
		this.btnConnect.setBounds(10, 11, 89, 23);
		this.frmGuessTheNumber.getContentPane().add(this.btnConnect);
		
		this.textField = new JTextField();
		this.textField.addActionListener(this);
		this.textField.setEnabled(false);
		this.textField.setBounds(65, 45, 86, 20);
		this.frmGuessTheNumber.getContentPane().add(this.textField);
		this.textField.setColumns(10);
		
		this.lblGuess = new JLabel("Guess:");
		this.lblGuess.setBounds(20, 48, 46, 14);
		this.frmGuessTheNumber.getContentPane().add(this.lblGuess);
		
		this.btnSend = new JButton("Send");
		this.btnSend.setEnabled(false);
		this.btnSend.addActionListener(this);
		this.btnSend.setBounds(161, 44, 89, 23);
		this.frmGuessTheNumber.getContentPane().add(this.btnSend);
		
		this.btnReset = new JButton("Reset");
		this.btnReset.addActionListener(this);
		this.btnReset.setEnabled(false);
		this.btnReset.setBounds(10, 87, 105, 23);
		this.frmGuessTheNumber.getContentPane().add(this.btnReset);
		
		this.btnTerminate = new JButton("Terminate");
		this.btnTerminate.addActionListener(this);
		this.btnTerminate.setEnabled(false);
		this.btnTerminate.setBounds(10, 121, 105, 23);
		this.frmGuessTheNumber.getContentPane().add(this.btnTerminate);
		
		this.scrollPane = new JScrollPane();
		this.scrollPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "INFO", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 0, 0)));
		this.scrollPane.setBounds(324, 45, 266, 304);
		this.frmGuessTheNumber.getContentPane().add(this.scrollPane);
		
		this.messages = new JTextArea();
		this.scrollPane.setViewportView(this.messages);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.textField) {
			do_textField_actionPerformed(e);
		}
		if (e.getSource() == this.btnTerminate) {
			do_btnTerminate_actionPerformed(e);
		}
		if (e.getSource() == this.btnReset) {
			do_btnReset_actionPerformed(e);
		}
		if (e.getSource() == this.btnConnect) {
			do_btnConnect_actionPerformed(e);
		}
		if (e.getSource() == this.btnSend) {
			do_btnSend_actionPerformed(e);
		}
	}
	
	protected void do_btnConnect_actionPerformed(ActionEvent e) {
		try  {
			connection = new Socket ("localhost", 6666);
			inputChannel = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			outputChannel = new PrintWriter(connection.getOutputStream(),true);
			messages.append("Connection established\n");
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(this.frmGuessTheNumber,
				    "Failed to establish a connection.", 
				    "Connection Failure",
				    JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			//this.messages.append("Server unreacheable or not running...\n");
			
		}
		this.btnConnect.setEnabled(false);
		this.btnReset.setEnabled(true);
		this.btnTerminate.setEnabled(true);
	}
	
	protected  void do_btnReset_actionPerformed(ActionEvent e) {
		this.outputChannel.println("RESET");
		this.outputChannel.flush();
		try {
			String answer = this.inputChannel.readLine();
		    this.messages.append("Server says: "+answer+" \n");
		    this.btnSend.setEnabled(true);
		    this.textField.setEnabled(true);
		}
		catch(Exception ioex) {
			JOptionPane.showMessageDialog(this.frmGuessTheNumber,
				    "IO error when getting response from server", 
				    "Connection Failure",
				    JOptionPane.ERROR_MESSAGE);
			this.messages.append("\"IO error when getting response from server\n");
			this.messages.append("Connection Failure\n");
		}
	}
	
	protected  void do_btnSend_actionPerformed(ActionEvent e) {
		int number = 0;
		try {
			number = Integer.parseInt(this.textField.getText());
			if (number<0) throw new NumberFormatException();
		}
		catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this.frmGuessTheNumber,
				    "Only positive integers allowed", 
				    "Bad Number",
				    JOptionPane.WARNING_MESSAGE);
			this.textField.setText("");
			messages.append("Bad number: Only positive integers allowed\n");
			return;
		}
		// send number to server
		this.outputChannel.println("CHECK " + number);
		this.outputChannel.flush();
		String response = null;
		try {
			response = this.inputChannel.readLine();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this.frmGuessTheNumber, "IO error when getting response from server",
					"Connection Failure", JOptionPane.ERROR_MESSAGE);
			messages.append("\"IO error when getting response from server\n");
			messages.append("consider terminating...\n");
			// System.exit(1);
		}
		if (!response.equalsIgnoreCase("EQUAL")) {
			messages.append("Server says (my number is): "+response+"\n");
		}
		else {
			// equal-> number correctly guessed.
			JOptionPane.showMessageDialog(this.frmGuessTheNumber,
				    "You got it! Number was: "+number+"\nPress Reset to play again\npress Terminate to quit", 
				    "NUMBER GUESSED!!!",
				    JOptionPane.INFORMATION_MESSAGE);
			messages.append("Server says (my number is): EQUAL\n");
			messages.append("NUMBER GUESSED!!! "+number+" \n");
			this.btnSend.setEnabled(false);
		}
		this.textField.setText("");
	}
	protected void do_btnTerminate_actionPerformed(ActionEvent e) {
		this.outputChannel.println("terminate"); this.outputChannel.flush();
		try {
			String response = inputChannel.readLine();
			JOptionPane.showMessageDialog(this.frmGuessTheNumber,
				    "Server says: "+response, 
				    "TERMINATING...!!!",
				    JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ioex) {
			JOptionPane.showMessageDialog(this.frmGuessTheNumber,
				    "?!?!?!?!?!?!", 
				    "Abnormal Termination",
				    JOptionPane.ERROR_MESSAGE);
		}
		finally {
			try {
				this.outputChannel.close();
				this.inputChannel.close();
				this.connection.close();
			} catch (Exception ex) {}
			System.exit(0);
		}
		
		
	}
	protected  void do_textField_actionPerformed(ActionEvent e) {
		do_btnSend_actionPerformed(e);
	}
}
