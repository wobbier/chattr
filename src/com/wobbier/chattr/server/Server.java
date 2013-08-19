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

package com.wobbier.chattr.server;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.*;

/*
 * A simple java chat server
 * 
 * @author Mitch Andrews
 * @version 1.0
 */

public class Server {
	ServerSocket      serverSocket;
	DataOutputStream  outputStream;
	DataInputStream   inputStream;
	ArrayList<User>   userPool;
	
	/*
	 * Usage: java Server port
	 * ex: java Server 6906
	 */
	public static void main(String[] args) {
		try {
			new Server((args.length > 0) ? Integer.parseInt(args[0]) : 6997);
		} catch (NumberFormatException e) {
			System.out.println("Expected integer for port got " + args[0]);
			System.exit(1);
		}
	}

	/*
	 * Start the server with specified port.
	 * 
	 * @param port server port.
	 */
	public Server(int port) {
		userPool = new ArrayList<User>();
		try {
			serverSocket = new ServerSocket(port);
				while(true) {
					Socket socket = serverSocket.accept();
					User user = new User(socket, this);
					Thread thread = new Thread(user);
					thread.start();
					userPool.add(user);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Broadcast a message to each user currently connected to the server.
	 * 
	 * @param message the message to be sent
	 */
	public void broadcast(String message) {
		try {
			for(int i = 0; i < userPool.size(); i++) {
				userPool.get(i).userOutput.writeBytes(message + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(message);
		}
	}

	/*
	 * Remove a user from the user pool.
	 * 
	 * @param user the address of the user to be removed.
	 */
	public void removeUser(User user) {
		userPool.remove(user);
	}
}