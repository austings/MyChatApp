import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Austin on 1/2/2016.
 *
 * The model for the chat application. Stores all the data associated with the application, such
 * as the chatlog or list of users. It connects the view to the proxy, which is how the different clients
 * are able to communicate with each other
 */
public class ChatModel
{
    //private variables
    private ChatProxy proxy;
    private ChatView view;

    private String myName;
    private String chatLog="Welcome to MyChatApp";
    private ArrayList<String> allNames= new ArrayList<String>();

    /**
     * Constructor
     * @param name- the name of the user
     */
    public ChatModel(String name)
    {
        this.myName = name;
        allNames.add(myName);
    }

    /**
     * Modifies the master list of names.
     * @param name- the name which is to be added or removed
     * @param value- A positive or 0 value will add the name to the list.
     *               A negative value will remove the name if it exists.
     * @return boolean- if the operation was a success.
     */
    public synchronized boolean modifyNames(String name,int value)
    {
        //adding a name
        if(value>=0)
        {
            allNames.add(name);
            view.redraw(1);
            return true;
        }
        else
        {
            //removing a name
            for(int i =0;i<allNames.size();i++)
            {
                if(allNames.get(i).equals(name))
                {
                    allNames.remove(i);
                    view.redraw(1);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set the associated proxy for this model
     * @param proxy- associated proxy
     */
    public synchronized void setProxy(ChatProxy proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Set the associated View controller for this model
     * @param view- associated view
     * @throws IOException- failure of sending the message that you've joined the chatroom
     */
    public synchronized void setView(ChatView view) throws IOException
    {
        this.view = view;
        addMessage(myName+" has joined the chatroom");
        view.redraw(0);
    }

    /**
     * Getter for name list array
     * @return- allNames: the list of current users in the chatroom
     */
    public ArrayList<String> getAllNames()
    {
        return allNames;
    }

    /**
     * Getter for the user's name
     * @return: myName- the user's name
     */
    public String getMyName()
    {
        return myName;
    }

    /**
     * Getter for the message chat log.  The chat log only displays messages from the point after the
     * user joins the chatroom.
     * @return- chatLog: the chat messages
     */
    public String getChatLog()
    {
        return chatLog;
    }

    /**
     * Function that is called from the view, after the user clicks to exit the program.
     * @throws IOException: if the client fails to send the exit message
     */
    public synchronized void quit() throws IOException
    {
        proxy.userLeave(myName);
    }

    /**
     * Updates the model from the proxy and refreshes the view
     * @param message: the message which was sent from another user
     */
    public synchronized void redrawChat(String message)
    {
        this.chatLog=this.chatLog+"\n"+message;
        view.redraw(0);
    }

    /**
     * Updates this model from the view and forwards the message to the proxy so that it can be
     * sent to other users
     * @param data- the new message.
     */
    public synchronized void addMessage(String data) throws IOException
    {
        chatLog = chatLog+"\n"+data;
        proxy.sendMessage(data);
    }

    /**
     * Adds a user to the userlist whenever someone joins the chat
     * Then, it sends the new user this client's name.
     * @param id- the new users uniqueID
     * @param name- the new users name
     */
    public synchronized void addUser(long id,String name) throws IOException
    {
        modifyNames(name,1);
        proxy.sendName(id,myName);
    }


}
