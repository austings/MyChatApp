import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Austin on 1/2/2016.
 *
 * The view controller for the chat application. Displays the information contained in the model on the
 * screen. Takes input from the user which can be stored in the model or sent to other clients.
 */
public class ChatView extends JFrame implements ActionListener
{
    private ChatModel model;

    //components
    private JTextField field;
    private JTextArea messageArea;
    private JTextArea userArea;
    private JScrollPane messageScroll;
    private JScrollPane userScroll;

    /**
     * Constructor for the view. Builds the UI and basic parameters for the chat window
     * Takes in the model that would be associated with the view.
     * @param model: associated model
     */
    public ChatView(ChatModel model) throws IOException
    {
        this.model = model;
        this.setTitle("MyChatApp");
        this.setLayout(new BorderLayout());
        //build the components of the window
        buildUserList(model.getAllNames());
        buildMessageScreen();
        buildInputSection();
        //set window parameters
        this.setPreferredSize(new Dimension(600,400));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        setVisible(true);

        //add listener for when the window is closed
        addWindowListener (new WindowAdapter()
        {
            public void windowClosing (WindowEvent e)
            {
                try {
                    onClose();
                }catch(IOException exception)
                {
                }
            }
        });
    }

    /**
     * Take an ArrayList of users and arrange them on the east side
     * of the screen.
     *
     * Should only be called once by the constructor.
     * @param users- arraylist of users in the chatroom
     */
    public void buildUserList(ArrayList<String> users)
    {
        //compile the arraylist into a single string
        StringBuilder oneString = new StringBuilder();
        for(int i =0;i<users.size();i++)
        {
            oneString = oneString.append(users.get(i)+"\n");
        }
        //create the area for the text and nest it in a scroll pane
        userArea = new JTextArea(oneString.toString());
        userArea.setEditable(false);
        userArea.setFont(new Font("Verdana", Font.BOLD, 12));
        userScroll = new JScrollPane(userArea);
        userScroll.setPreferredSize(new Dimension(100, 400));
        userScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(userScroll, BorderLayout.EAST);
    }

    /**
     * Builds the Message Center area, the part of the screen which
     * shows messages that have been sent.
     *
     * Should only be called once by the constructor
     */
    public void buildMessageScreen() throws IOException
    {
        messageArea = new JTextArea(model.getChatLog());
        messageArea.setEnabled(false);
        messageArea.setDisabledTextColor(Color.BLACK);
        messageArea.setFont(new Font("Verdana",Font.PLAIN, 12));
        messageScroll = new JScrollPane(messageArea);
        this.add(messageScroll,BorderLayout.CENTER);
    }

    /**
     * Builds the south panel of the view, which contains the field
     * in which the user types their message and the button to send it.
     *
     * Should only be called once by the constructor.
     */
    public void buildInputSection()
    {
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        field = new JTextField();
        //build the send button and make it listen for when the user clicks it.
        JButton send = new JButton("Send");
        send.setActionCommand("SEND");
        send.addActionListener(this);
        southPanel.add(field,BorderLayout.CENTER);
        southPanel.add(send, BorderLayout.EAST);
        this.add(southPanel,BorderLayout.SOUTH);
    }

    /**
     * Occurs when the user presses the send button.
     * Updates the model and redraws the view- then clears the text box.
     * @throws IOException: for if a message fails to send
     */
    public void onMessageSend() throws IOException
    {
        String message = model.getMyName()+": "+field.getText();
        model.addMessage(message);
        redraw(0);
        field.setText("");
    }

    /**
     * Occurs when the user exits the application.
     * @throws IOException: for if the exit message fails to send.
     */
    private void onClose() throws IOException
    {
        model.quit();
    }

    /**
     * Redraws the view without updating the model.
     * Takes in the code parameter, which tells the view which part of the window should be updated.
     * Similar in function to the build methods, but more flexible so that this method can be
     * called upon whenever a change in the UI should occur.
     * @param code - 0: new message
     *             - 1: new username
     */
    public void redraw(int code)
    {
        switch(code) {
            //in the case of a new message
            case 0:
                messageArea.setText(model.getChatLog());
                messageScroll.setViewportView(messageArea);
                break;
            case 1:
                //in the case of a new user
                StringBuilder oneString = new StringBuilder();
                for(int i =0;i<model.getAllNames().size();i++)
                {
                    oneString = oneString.append(model.getAllNames().get(i)+"\n");
                }
                userArea.setText(oneString.toString());
                userScroll.setViewportView(userArea);
                break;
        }
        revalidate();
        repaint();
    }

    /**
     * The only action which can be preformed is clicking the send message button.
     * However, if other buttons were to be added their effect would be detailed here.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();
        //if the send button was clicked
        if(action.equals("SEND"))
        {
            try {
                onMessageSend();
            }catch(IOException exp)
            {
                exp.printStackTrace();
            }
        }
    }

}
