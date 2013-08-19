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

/*
 * A user class that holds socket info and parses incoming messages.
 * 
 * @author Mitch Andrews
 * @version 1.0
 */
public class User implements Runnable {
	Socket userSocket;
	Server userServer;
	public DataOutputStream userOutput;
	public BufferedReader userInput;

	/*
	* Create a new user instance attached to the server.
	* 
	* @param socket The user's socket info.
	* @param server The main server class which holds the user list.
	*/
	public User(Socket socket, Server server) {
		userSocket = socket;
		userServer = server;
		try {
			userOutput = new DataOutputStream(userSocket.getOutputStream());
			userInput = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
        
	/*
	* Each user has their own thread so we can handle simultaneous input from multiple users.
	*/
	public void run() {
                String output = null;
                try {
					while((output = userInput.readLine()) != null)
						userServer.broadcast(output);
                } catch(IOException e) {
                        e.printStackTrace();
                } finally {
                        userServer.removeUser(this);
                }
        }
}
