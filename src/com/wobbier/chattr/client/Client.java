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

package com.wobbier.chattr.client;

import java.net.*;
import java.io.*;
import java.lang.Thread;

public class Client implements Runnable {
	String name;
	Socket socket;
	DataOutputStream outputStream;
    BufferedReader inputStream;
    BufferedReader localInputStream = new BufferedReader(new InputStreamReader(System.in));
    
	/*
	 * Usage: java Client address port
	 * ex:    java Client localhost 6906
	 */
	public static void main(String[] args) {
		try {
			new Client((args.length > 0) ? args[0] : "localhost", (args.length > 1) ? Integer.parseInt(args[1]) : 6997);
		} catch (NumberFormatException e) {
			System.out.println("Expected integer for port got " + args[1]);
			System.exit(1);
		}
	}

	/*
	* Start a new instance of the client and connect to the specified server.
	* 
	* @param server the server's ip address that you're connecting to.
	* @param port   the server's port that you're connecting to.
	*/
	public Client(String server, int port) {
		try {
			socket = new Socket(server, port);
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			do {
				System.out.print("Please enter a username: ");
				name = localInputStream.readLine();
			} while (name == null || name.length() < 1);
			outputStream.writeBytes(name + ", has entered chat.\n");
		} catch(ConnectException e) {
			System.out.println("Could not connect to specified server `"+ server + ":" + port +"`");
			System.exit(1);
		} catch(IOException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
		while(true)	{
			String inputString = null;
			try {
				inputString = localInputStream.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(inputString != null) {
				try {
					outputStream.writeBytes(name + ": " + inputString);
					outputStream.writeByte('\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
	 * Check for other messages from the server on a seperate thread.
	 */
	public void run(){
		String output;
		try {
			while((output = inputStream.readLine()) != null)
				System.out.println(output + "\n");
		} catch(NullPointerException | IOException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
}