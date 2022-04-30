package Game.Graphic;

import Game.utils.Request;
import Server.Server;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StartFrame extends JFrame {
    /**
     *  Button to start a new server
    **/
    public JButton startServerButton;
    /**
     * Button to start a new client
     **/
    public JButton   startClientButton;
    private Server theStartApp;                              // The StartPoker class to which this window belongs
    private JTextField  messageText;                              // Message to be displayed on this window on the bottom line
    private JTextField  portText;                                 // The text field where the user can enter the port number on which to start the server.

    public StartFrame( String title, Server a ) {
        super( title );
        theStartApp = a;

        //régler la taille de JFrame

        setSize(500, 150);

        setLocationRelativeTo(null);


        JPanel panel = new JPanel( new GridLayout( 3,1,2,2 ) );
        panel.setBorder( new EmptyBorder( 3,3,3,3 ) );

        Box serverBox = Box.createHorizontalBox();

        JLabel message=new JLabel( "Server Port Number : " );
        message.setForeground(new Color(255,215,0));
        serverBox.add(message);

        portText = new JTextField( Request.PORT);
        portText.setBackground(new Color(255,228,181));
        serverBox.add( portText );

        JPanel buttonPanel = new JPanel();

        startServerButton = new JButton( "Start a server" );
        startServerButton.setBackground(new Color(224,255,255));
        //startServerButton.addActionListener();
        startClientButton = new JButton( "Start a client" );
        startClientButton.setBackground(new Color(224,255,255));
        // startClientButton.addActionListener( new startClientAction() );

        buttonPanel.setBorder( new EmptyBorder( 0,15,0,15 ) );
        buttonPanel.add( startServerButton );
        buttonPanel.add( startClientButton );
        buttonPanel.setBackground(new Color(0,100,0));  //Whatever color


        messageText = new JTextField();
        messageText.setEditable( false );
        messageText.setBackground(new Color(0,100,0));  //Whatever color


        panel.add( serverBox );
        panel.add( buttonPanel );
        panel.add( messageText );
        panel.setBackground(new Color(0,100,0));  //Whatever color
        getContentPane().add( panel );

        setResizable( false );

    }
    /***********************
     * startServerAction class is used to define the action that occurs when the start server button is pressed.
     **/
    class startServerAction extends AbstractAction {

        //----------------------
//    Constructor
//
        startServerAction() {
            super();
        }

        //----------------------
//    The actionPerformed() function is called when the button is pressed
//
        public void actionPerformed( ActionEvent e ) {

        }
    }

    /***********************
     * startClientAction class is used to define the action that occurs when the start client button is pressed.
     **/
    class startClientAction extends AbstractAction {

        //----------------------
//    Constructor
//
        startClientAction() {
            super();
        }

        //----------------------
//    The actionPerformed() function is called when the button is pressed
//
        public void actionPerformed( ActionEvent e ) {

        }
    }
    public static void main(String [] args){
        StartFrame sf=new StartFrame("poker",null);
        sf.setVisible(true);
    }
}
