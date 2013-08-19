/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Mitch Andrews <michdandrews@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.wobbier.chattr.example;

import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.Thread;
import javax.swing.*;
import javax.swing.text.*;

/*
 * A simple chattr client with GUI
 * 
 * @author Mitch Andrews
 * @version 1.0
 */
public class Chattr extends JFrame implements Runnable, ActionListener{
	private static final long serialVersionUID = -271275853743441174L;
	final int DEFAULT_PORT = 6997;
	String input;
	String name;
	String address;
	int port;
	Socket socket;
	DataOutputStream outputStream;
	BufferedReader inputStream;
	StyledDocument doc;
	JTextField inputBox = new JTextField(128);

	public static void main(String[] args){
		new Chattr();
	}
	
	/*
	 * Starts the client.
	 * 
	 * Gets user server credentials.
	 * Creates chat window.
	 * Connects to specified server.
	 * Gets screen name from user.
	 */
	public Chattr() {
		input = (String)JOptionPane.showInputDialog(
			this,
			"Please enter your server address (Default localhost):",
			"Address Required",
			JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			"");
		address = (input.length() <= 0) ? "localhost" : input;
		input = (String)JOptionPane.showInputDialog(
			this,
			"Please enter your server port (Default "+ DEFAULT_PORT +"):",
			"Port Required",
			JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			"");
		port = (input.length() <= 0) ? DEFAULT_PORT : Integer.parseInt(input);
		setTitle("Chattr: Chat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setPreferredSize(new Dimension(300, 500));
		setLayout(null);
		setResizable(false);
		JTextPane txt = new JTextPane();
		doc = txt.getStyledDocument();
		txt.setEditable(false);
		DefaultCaret caret = (DefaultCaret) txt.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane jsp = new JScrollPane(txt);
		jsp.setBounds(5,5,290,460);
		this.add(jsp);
		inputBox.addActionListener(this);
		inputBox.setBounds(5,470,290,25);
		this.add(inputBox);
		this.pack();
		setVisible(true);
		try {
			socket = new Socket(address, port);
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			do {
				name = (String)JOptionPane.showInputDialog(
					this,
					"Please enter your name:",
					"Name Required",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");
			} while (name == null || name.length() < 1);
			outputStream.writeBytes(name + ", has entered chat.\n");
		} catch(ConnectException e) {
			JOptionPane.showMessageDialog(this,
				"The chat server is currently offline.",
				"Connection Error",
				JOptionPane.ERROR_MESSAGE);
			dispose();
		} catch(IOException e){
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	
	/*
	 * Reads incoming messages to the client and adds them to the chat field.
	 */
	public void run() {
		String output = null;
		try {
			while((output = inputStream.readLine()) != null)
				doc.insertString(doc.getLength(), output + "\n", null);
		} catch(IOException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * When the user hits enter we write the message to the socket.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String inputString = inputBox.getText();
		inputBox.setText("");
		if (socket != null && outputStream != null && inputStream != null) {
			try {
				outputStream.writeBytes(name + ": " + inputString + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}