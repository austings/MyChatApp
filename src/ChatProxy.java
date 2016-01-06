import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * Created by Austin Sierra on 1/2/2016.
 *
 * The chat proxy is the proxy between user clients. It
 * is in charge of sending messages between clients in addition to
 * reading message from them.
 */
public class ChatProxy
{
    //network details
    private InetAddress group;
    private int port;
    private MulticastSocket mailbox;

    //The uniqueID of this client. Used to differentiate users with the same name.
    private long uniqueID;

    //associated model
    private ChatModel model;

    //codes for certain actions
    private static final int USER_JOIN_CODE = 0;
    private static final int SEND_NAME_CODE = 1;
    private static final int SEND_MESSAGE_CODE = 2;
    private static final int USER_EXIT_CODE = 3;

    /**
     * Constructor for the chat proxy
     * @param group: P2P network group entered by user
     * @param port: Open port
     * @throws IOException: in case the connection cannot be made
     */
    public ChatProxy(InetAddress group,int port) throws IOException
    {
        this.group = group;
        this.port = port;
        mailbox = new MulticastSocket(port);
        mailbox.joinGroup(group);

        uniqueID = new DataInputStream(new ByteArrayInputStream(SecureRandom.getSeed(8))).readLong();
    }

    /**
     * Set the associated model for this proxy.
     * Also starts the Reader Thread.
     * @param model: associated model
     */
    public void setModelListener(ChatModel model)
    {
        this.model = model;
        new ReaderThread().start();
    }

    /**
     * The next several methods are similar in how they work.
     * They all create output streams and write relevant information to the other clients
     * when a specific action occurs.
     *
     * This method writes a message every time a new user joins the chatroom
     *
     * @throws IOException- if the message fails to write
     */
    public void userJoined() throws IOException
    {

        //open the output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        //write the specific message code, so the reader knows what kind of message this will be.
        out.writeByte(USER_JOIN_CODE);
        //write the uniqueID for this client. This way a client doesn't read its own messages.
        out.writeLong(uniqueID);
        //Write any further information needed when the task occurs. This method only needs to write
        //the client's name.
        out.write(model.getMyName().getBytes());
        out.close();
        byte[] buf = baos.toByteArray();
        //save this output stream to a packet and send it
        DatagramPacket packet = new DatagramPacket(buf,buf.length,group,port);
        mailbox.send(packet);
    }

    /**
     * This method sends the user's message to the other clients.
     *
     * @param message: the message the user wrote.
     * @throws IOException: if the message fails to write
     */
    public void sendMessage(String message) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeByte(SEND_MESSAGE_CODE);
        out.writeLong(uniqueID);
        out.write(message.getBytes());
        out.close();
        byte[] buf = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(buf,buf.length,group,port);
        mailbox.send(packet);
    }

    /**
     * This method sends the user's name and ID to the other clients.
     * It is used so that new users can collect the full list of users already in the chatroom.
     * @param id: the user's uniqueID
     * @param name: the user's name.
     * @throws IOException: if a message fails to send
     */
    public void sendName(long id,String name) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeByte(SEND_NAME_CODE);
        out.writeLong(uniqueID);
        out.writeLong(id);
        out.write(name.getBytes());
        out.close();
        byte[] buf = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(buf,buf.length,group,port);
        mailbox.send(packet);
    }

    /**
     * This method is called everytime a user leaves the chatroom.
     *
     * @param name: the name of the user who left.
     * @throws IOException: if the message fails to send
     */
    public void userLeave(String name) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream (baos);
        out.writeByte(USER_EXIT_CODE);
        out.writeLong(uniqueID);
        out.write(name.getBytes());
        out.close();
        byte[] buf = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket (buf, buf.length, group, port);
        mailbox.send(packet);
        System.exit(0);
    }

    /**
     * This ReaderThread constantly reads in messages from the other clients.
     */
    public class ReaderThread extends Thread
    {
        /**
         * Called when the thread starts.
         */
        public void run()
        {
            byte opcode;
            long id;
            byte[] buf = new byte[2200];

            try
            {
                //send a message that a new user has joined the chatroom.
                userJoined();
                //run continuously
                while(true)
                {
                    //set up an input stream
                    DatagramPacket packet = new DatagramPacket(buf,buf.length);
                    mailbox.receive(packet);

                    DataInputStream in = new DataInputStream
                            (new ByteArrayInputStream(buf,0,packet.getLength()));
                    //i only use a scanner for strings
                    Scanner scan = new Scanner(in);
                    opcode = in.readByte();
                    id = in.readLong();
                    if(id==uniqueID) continue;
                    //depending on the code we get, do something different.
                    switch(opcode)
                    {
                        //when a user joins the chatroom, add that user to the list.
                        case USER_JOIN_CODE:
                            model.addUser(id,scan.nextLine());
                            break;
                        //when a user joins the chatroom, ensure that they are sent everyone's name already
                        //in the chatroom.
                        case SEND_NAME_CODE:
                            long destination = in.readLong();
                            if(destination==uniqueID)
                                model.modifyNames(scan.nextLine(),1);
                            break;
                        //when a user sends a message
                        case SEND_MESSAGE_CODE:
                            model.redrawChat(scan.nextLine());
                            break;
                        //when a user leaves the chatroom
                        case USER_EXIT_CODE:
                            String user=scan.nextLine();
                            model.modifyNames(user,-1);
                            model.redrawChat(user+" left the chatroom");
                            break;
                    }

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
