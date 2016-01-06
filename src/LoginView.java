import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Austin Sierra on 1/2/2016.
 *
 * The LoginView is the window which first opens when the application starts up. It collects
 * the group, username, and port from the user. If it is able to sucessfully connect, it opens the
 * chatroom.
 */
public class LoginView extends JFrame implements ActionListener
{

    //editable textPanes
    private JTextField host;
    private JTextField user;
    private JTextField port;

    /**
     * Constructor for the loginview
     */
    public LoginView()
    {
        this.setTitle("MyChatApp");
        loginBox();
        addStatics();
        this.setPreferredSize(new Dimension(400,200));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        setVisible(true);
    }

    /**
     * This method adds the header to the window, as well as the enter button.
     *
     * Should only be called once by the constructor.
     */
    public void addStatics()
    {
        JLabel header = new JLabel("Welcome to MyChatApp");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(header,BorderLayout.NORTH);

        JButton enter = new JButton("Enter");
        enter.setHorizontalAlignment(SwingConstants.CENTER);
        enter.addActionListener(this);
        enter.setActionCommand("ENTER");
        this.add(enter,BorderLayout.SOUTH);
    }

    /**
     * Create the loginbox in which the user can input their connection
     * information.
     *
     * Should only be called once by the constructor
     */
    public void loginBox()
    {
        //Create the login box and create a group layout in which
        //a username, groupname, and port are requested.
        JPanel loginBox = new JPanel();
        GroupLayout layout = new GroupLayout(loginBox);
        loginBox.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        //labels
        JLabel username = new JLabel("Name: ");
        JLabel server = new JLabel("Group: ");
        JLabel portID = new JLabel("Port: ");

        user = new JTextField("");
        host = new JTextField("239.255.0.1");
        port = new JTextField("56789");
        host.setEditable(true);
        user.setEditable(true);
        port.setEditable(true);

        //arrange the labels next to the textfields
        //use two groups, horizontal group and vertical group.
        hGroup.addGroup(layout.createParallelGroup().
                addComponent(username).addComponent(server).addComponent(portID));
        hGroup.addGroup(layout.createParallelGroup().
                addComponent(user).addComponent(host).addComponent(port));
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(username).addComponent(user));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
            addComponent(server).addComponent(host));
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                addComponent(portID).addComponent(port));
        layout.setVerticalGroup(vGroup);

        this.add(loginBox,BorderLayout.CENTER);
    }

    /**
     * This method is called once the user presses the enter button.
     * It will attempt to connect using the user's given parameters.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if(action.equals("ENTER"))
        {
            //attempt connection
            try {
                //if name is too long or too short
                if(user.getText().length()<3||user.getText().length()>12)
                {
                    throw new Exception();
                }
                //create proxy, model, view, and connect the model to the view and proxy.
                ChatProxy proxy = new ChatProxy
                        (InetAddress.getByName(host.getText()), Integer.parseInt(port.getText()));
                ChatModel m = new ChatModel(user.getText());
                m.setProxy(proxy);
                ChatView view = new ChatView(m);
                m.setView(view);
                proxy.setModelListener(m);
                //if connection success, close the login window
                this.setVisible(false);

                //catch error if cant connect
            }catch(IOException exception){
                JOptionPane.showMessageDialog(this,"Could not connect to a network with those parameters.");
                host.setText("");
                port.setText("");
            }
            catch(Exception expp) {
                System.out.println(expp.getClass().getName());
                JOptionPane.showMessageDialog(this,"Your name must be between 3 and 12 characters.");
                user.setText("");
            }

        }
    }


}
