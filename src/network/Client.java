/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import sun.net.www.protocol.http.AuthCacheValue;

/**
 *
 * @author jan
 */
public class Client {

    public Socket socket;

    public ObjectInputStream sInput;

    public ObjectOutputStream sOutput;

    public int id;

    public String username;

    public Date ConDate;

    public ClientThread ListenThread;

    Client(Socket socket) {
        this.id = ++Server.uniqueId;
        this.socket = socket;

        try {
            this.sOutput = new ObjectOutputStream(socket.getOutputStream());
            this.sInput = new ObjectInputStream(socket.getInputStream());

            this.username = (String) sInput.readObject();

            this.ConDate = new Date();
            this.ListenThread = new ClientThread(this);

        } catch (IOException e) {
            Server.display("Exception .... " + e);
            return;
        } catch (ClassNotFoundException e) {

        }

    }

    public void start() {
        this.ListenThread = new ClientThread(this);
        this.ListenThread.start();

    }

    public void close() {
        try {
            if (this.ListenThread != null) {
                this.ListenThread.interrupt();
            }
            if (this.sOutput != null) {
                this.sOutput.close();

            }

            if (this.sInput != null) {
                this.sInput.close();

            }

            if (this.socket != null) {
                this.socket.close();

            }

        } catch (Exception e) {
        }

    }

    public boolean writeMsg(Object msg) {

        if (!this.socket.isConnected()) {
            close();
            return false;
        }

        try {
            this.sOutput.writeObject(msg);
        } catch (Exception e) {
            Server.display("Error sending message to " + username);
            Server.display(e.toString());
        }
        return true;

    }

    public class ClientThread extends Thread {

        Client TheClient;

        public ClientThread(Client TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {

            while (TheClient.socket.isConnected()) {

                try {

                    String message = (String) this.TheClient.sInput.readObject();

                } catch (IOException e) {
                    Server.display(this.TheClient.username + "exception reading Streams :" + e);
                    break;

                } catch (ClassNotFoundException ex) {

                    Server.display(this.TheClient.username + "Exception reading Streams " + ex);
                }

            }
            Server.remove(this.TheClient.id);

        }

    }

}
