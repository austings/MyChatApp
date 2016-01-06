/**
 * Created by Austin Sierra on 1/6/2016.
 *
 * MyChatApp is a P2P based chat application that runs over any P2P enabled network.
 * Simply type in a  P2P group and port (or use the default), and you can begin chatting.
 *
 * Much of the code is typed with reference to RIT Prof Alan Kaminsky's notes on
 * P2P applications found
 * here->   https://www.cs.rit.edu/~ark/251/module09/notes.shtml
 *
 *
 * OTHER RESOURCES USED:
 *
 * Understanding group layouts:
 * https://docs.oracle.com/javase/7/docs/api/javax/swing/GroupLayout.html
 *
 * Chat program examples (non peer2peer):
 * http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214/Virtual/Lectures/chat-client-server.html
 * http://ashishmyles.com/tutorials/tcpchat/index.html
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/
 *      examples/components/ToolBarDemoProject/src/components/ToolBarDemo.java
 *
 */
public class MyChatApp
{
    public static void main(String[] args)
    {
        LoginView run = new LoginView();
    }
}
