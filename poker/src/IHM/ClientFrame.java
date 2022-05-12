package IHM;
import Client.Client;
import Client.States.MenuState;
import Client.States.Playing5CardPokerState;
import Client.States.PlayingTexasHoldemState;
import Client.States.WaitingState;
import Game.Player;
import Game.PokerFerme;
import Game.PokerGame;
import Game.TexasHoldem;
import Game.simulator.FiveCardSimulator;
import Game.simulator.TexasHoldemSimulator;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.*;

import static Game.simulator.TexasHoldemSimulator.simulate;

public class ClientFrame extends JFrame {
    private  final int  MAX_PLAYERS = 10;
    private  JPanel         messagePanel;                   // Panel where messages are displayed
    private  JPanel         cashPanel;                      // Panel where info about the game and betting is displayed
    private  JTextField     messageText;                    // The message text being displayed
    private  JTextField     bankText;                       // How much money this player had
    private  JTextField     potText;                        // How much money is in the pot
    private  JTextField     betText;                        // How much is the current bet
    private  JTextField     yourBetText;                    // How much is this player about to bet
    private  ArrayList      positionLabels;                 // Labels under each possible player location
    private  ArrayList      cashLabels;                     // Labels showing how much money each player has.
    private  ArrayList      dealerLabels;                   // Labels with icon of dealer button to designate who is the dealer.
    private  String         playerText[];                   // String of the text under each player
    private  JButton        foldButton;                     // Button used to fold
    private  JButton        checkButton;                    // Button used to check
    private  JButton        callButton;                     // Button used to call the current bet
    private  JButton        raiseButton;                     // Button used to raise
    private  JButton        startResquest;
    private  JPanel        table;
    private  JButton        startGame;
    private JLabel         nameMessage;
    private ArrayList<JComponent> startGamePanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> joinPanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> getListPanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> roundPanel = new ArrayList<JComponent>() ;
    private ArrayList<JComponent> createPanel = new ArrayList<JComponent>() ;
    private ArrayList<JLabel> cardsLabels = new ArrayList<JLabel>();
    private ArrayList<JLabel> namesLabels = new ArrayList<JLabel>();
    private ArrayList<JLabel> cardsOnTable = new ArrayList<JLabel>();
    private ArrayList<JLabel> stacksLabels = new ArrayList<JLabel>();
    private JButton changeButton;
    private JTextField changeText;

    private JTextField raiseAmountText;
    private JTextField join;
    private JButton okName;
    private String playerName;
    private Player   player;
    private PokerGame game;
    private Client client;
    private boolean largeName = false;
    private boolean usedName = false;
    private boolean welcome = false;
    private boolean incorrectType = false;
    private boolean incorrectPlayers = false;
    private boolean incorrectBet = false;
    private boolean incorrectStack = false;
    private boolean startRqst=false;
    private boolean stop = false;
    private boolean displayPlayButton = false;
    private boolean displayActionButtons = false;
    private boolean change = false;

    public final static int INTERVAL = 50;
    private Timer timer;
    private int reponseStartRequest=-1;

    private JLabel tries;
    private JLabel behind;
    private JLabel time;
    private JLabel tied;
    private JLabel ahead;
    private boolean updatedStatistics=false;
    public ClientFrame(String title , Client client){
        super(title);
        this.client=client;
        playerText = new String[MAX_PLAYERS];
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout( new GridLayout( 4,1 ) );
        JPanel buttons = new JPanel();
        buttons.setLayout( new GridLayout( 1,5 ) );

        foldButton = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                timer.restart();
                String messageToSend="410 FOLD";
                client.sendMessage(messageToSend);
                displayActionButtons=true;
            }
        });
        foldButton.setFocusPainted( false );
        buttons.add( foldButton );

        checkButton = new JButton( new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                timer.restart();
                String messageToSend="411 CHECK";
                client.sendMessage(messageToSend);
                displayActionButtons=true;
            }
        });
        checkButton.setFocusPainted( false );
        buttons.add( checkButton );

        callButton = new JButton( new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                timer.restart();
                String messageToSend="412 CALL";
                client.sendMessage(messageToSend);
                displayActionButtons=true;
            }
        });
        callButton.setFocusPainted( false );
        buttons.add( callButton );

        raiseButton = new JButton( new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                timer.restart();
                String messageToSend="413 RAISE "+raiseAmountText.getText();
                client.sendMessage(messageToSend);
                raiseAmountText.setText("");
                displayActionButtons=true;
            }
        });
        callButton.setFocusPainted( false );
        buttons.add( raiseButton );

        raiseAmountText = new JTextField();
        buttons.add(raiseAmountText);

        cashPanel = new JPanel();
        cashPanel.setLayout( new GridLayout( 1,4 ) );
        cashPanel.setBorder( new EtchedBorder( EtchedBorder.RAISED ) );

        bankText = new JTextField();
        potText = new JTextField();
        betText = new JTextField();
        yourBetText = new JTextField();

        bankText.setEditable( false );
        potText.setEditable( false );
        betText.setEditable( false );
        yourBetText.setEditable(false);

        cashPanel.add( bankText );
        cashPanel.add( potText );
        cashPanel.add( betText );
        cashPanel.add(yourBetText);

        messagePanel = new JPanel();
        messagePanel.setLayout( new BorderLayout() );
        messageText = new JTextField();
        messageText.setEditable( false );
        messagePanel.add( messageText );

        JPanel changePanel = new JPanel();
        changePanel.setLayout( new GridLayout( 1,2 ) );

        changeButton = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                change = true;
                timer.restart();
                String messageToSend = "710 CHANGE ";
                if(changeText.getText().equals("")){
                    messageToSend = messageToSend +"0";
                }
                else{
                    String cardsNames[] = changeText.getText().split(" ");
                    messageToSend = messageToSend + cardsNames.length;
                    for(int i=0 ; i<cardsNames.length ; i++){
                        messageToSend = messageToSend + " " + cardsNames[i];
                    }
                }
                System.out.println(messageToSend);
                client.sendMessage(messageToSend);
                changeText.setText("");
                displayActionButtons=true;
            }
        });
        changeButton.setFocusPainted( false );
        changeButton.setEnabled(false);
        changePanel.add( changeButton );

        changeText = new JTextField();
        changePanel.add(changeText);


        bottomPanel.add( cashPanel );
        bottomPanel.add( messagePanel );
        bottomPanel.add( buttons );
        bottomPanel.add(changePanel);


        getContentPane().add( bottomPanel, BorderLayout.SOUTH );

        table = new JPanel( null);
        table.setBackground(new Color(0, 128, 0));

        join = new JTextField();
        join.setBounds(610,230,150,30);
        joinPanel.add(join);

        JLabel nameLabel = new JLabel("Entrer votre nom :");
        nameLabel.setBounds(635,190,150,30);
        joinPanel.add(nameLabel);

        nameMessage = new JLabel();
        nameMessage.setBounds(550,270,300,30);
        joinPanel.add(nameMessage);

        okName = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                playerName = join.getText();
                String messageToSend="100 HELLO PLAYER "+playerName;
                client.sendMessage(messageToSend);
                while (!client.isChange()){
                    System.out.print("");
                }
                if(largeName){
                    largeName=false;
                    nameMessage.setText("La taille du nom ne doit pas dépasser 30 caractères");
                    join.setText("");
                    setPanel(joinPanel);
                }
                else{
                    if(usedName){
                        usedName=false;
                        nameMessage.setText("Ce nom est déjà utilisé par un autre joueur");
                        join.setText("");
                        setPanel(joinPanel);
                    }
                    else{
                        if(welcome){
                            getList();
                        }
                    }
                }
            }
        });
        okName.setText("OK");
        okName.setBounds(650,310,70,30);
        joinPanel.add(okName);


        startGame = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                setPanel(joinPanel);
            }
        });
        startGame.setText("Commencer le jeu");
        startGame.setBounds(610,230,150,30);
        startGamePanel.add(startGame);


        JLabel typeLabel = new JLabel("Type : ");
        typeLabel.setBounds(100,50,150,30);
        JTextField typeText = new JTextField();
        typeText.setBounds(100,90,150,30);
        createPanel.add(typeLabel);
        createPanel.add(typeText);

        JLabel nbPlayerLabel = new JLabel("nombre maximal de joueur : ");
        JTextField nbPlayerText = new JTextField();
        nbPlayerLabel.setBounds(270,50,200,30);
        nbPlayerText.setBounds(270,90,200,30);
        createPanel.add(nbPlayerLabel);
        createPanel.add(nbPlayerText);

        JLabel minBetLabel = new JLabel("Enchère minimale : ");
        JTextField minBetText = new JTextField();
        minBetLabel.setBounds(490,50,150,30);
        minBetText.setBounds(490,90,150,30);
        createPanel.add(minBetLabel);
        createPanel.add(minBetText);

        JLabel stackLabel = new JLabel("Stack : ");
        JTextField stackText = new JTextField();
        stackLabel.setBounds(670,50,150,30);
        stackText.setBounds(670,90,150,30);
        createPanel.add(stackLabel);
        createPanel.add(stackText);

        JLabel erreurCreateRound = new JLabel();
        erreurCreateRound.setBounds(250,130,400,30);
        createPanel.add(erreurCreateRound);

        JButton createRound = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                String messageToSend="110 CREATE "+typeText.getText()+" PLAYER "+nbPlayerText.getText()+" MIN "+minBetText.getText()+" STACK "+stackText.getText();
                client.sendMessage(messageToSend);
                while (!client.isChange()){
                    System.out.print("");
                }
                if(incorrectType){
                    incorrectType=false;
                    erreurCreateRound.setText("Le type doit étre 1 pour Texas Hold’em ou 0 pour Poker fermé");
                    typeText.setText("");
                    setPanel(createPanel);
                }
                else if(incorrectPlayers){
                    incorrectPlayers=false;
                    erreurCreateRound.setText("Le nombre de joueurs est incorrect");
                    nbPlayerText.setText("");
                    setPanel(createPanel);
                }
                else if(incorrectBet){
                    incorrectBet=false;
                    erreurCreateRound.setText("L’enchère minimale est incorrect");
                    minBetText.setText("");
                    setPanel(createPanel);
                }
                else if(incorrectStack){
                    incorrectStack=false;
                    erreurCreateRound.setText("Le stack initial est incorrect");
                    stackText.setText("");
                    setPanel(createPanel);
                }
                else {
                    setPanel(roundPanel);
                    while (!(client.getGameState() instanceof WaitingState)) {
                        System.out.print("");
                    }
                    game = ((WaitingState) client.getGameState()).getCurrentGame();
                    player = game.getPlayer(playerName);
                    if(playerName.equals(game.getPlayers().get(0).getName())) {
                        startResquest = new JButton(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                String messageToSend = "150 REQUEST START";
                                client.sendMessage(messageToSend);
                                while (!(client.getGameState() instanceof WaitingState)) {
                                    System.out.println(" ");
                                }
                            }
                        });

                        startResquest.setText("Start the Game");
                        startResquest.setBackground(new Color(255, 255, 0));
                        startResquest.setBounds(80, 500, 200, 50);
                        roundPanel.add(startResquest);
                    }


                    timer = new Timer(INTERVAL,
                            new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    if(client.getGameState() instanceof PlayingTexasHoldemState || client.getGameState() instanceof Playing5CardPokerState){
                                        roundPanel.remove(startResquest);
                                    }
                                    for (int i = 0; i < game.getPlayers().size(); i++) {
                                        updatePlayerCards(game.getPlayers().get(i), i, game.getPlayers().get(i).getName().equals(player.getName()));
                                    }
                                    if(game instanceof TexasHoldem){
                                        updateTableCards();
                                    }

                                    if(player.getName().equals(game.getCurrentPlayer().getName()) && client.getGameState().isGameStarted() /*&& player.getHand().getCards().size()>0*/){
                                        if(!(game.getBidTurn()==2 && change)){
                                            timer.stop();
                                        }
                                    }
                                    updatePlayer();
                                    if(displayActionButtons){
                                        timer.restart();
                                        displayActionButtons=false;
                                    }
                                    setPanel(roundPanel);
                                }
                            });
                    timer.start();
                }
            }

        });
        createRound.setText("Créer la partie");
        createRound.setBounds(400,170,150,30);
        createPanel.add(createRound);

        JLabel dealer=new JLabel();
        dealer.setIcon(new ImageIcon(this.getClass().getResource("/images/button_present.png")));
        dealer.setBounds(30,170,50,50);
        roundPanel.add(dealer);

        for (int i=0;i<MAX_PLAYERS;i++) {
            String path;
            if(i==0){
                JLabel name = new JLabel();
                name.setBounds(75, 170, 90, 30);
                namesLabels.add(name);
                roundPanel.add(name);
                JLabel stack= new JLabel();
                stack.setBounds(75, 190, 90, 30);
                stacksLabels.add(stack);
                roundPanel.add(stack);
                JLabel[] cards = new JLabel[5];
                for (int j = 0; j < 5; j++) {
                    cards[j] = new JLabel();
                    /*path = "/images/card_44.png";
                    cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));*/
                    cards[j].setBounds(50 + (j * 15), 255, 70, 100);
                    cardsLabels.add(cards[j]);
                }
                for (int j = 4; j >= 0; j--) {
                    roundPanel.add(cards[j]);
                }
            }
            else{
                if(i<5){
                    JLabel name = new JLabel();
                    name.setBounds(100+(i*200), 0, 90, 30);
                    namesLabels.add(name);
                    roundPanel.add(name);
                    JLabel stack= new JLabel();
                    stack.setBounds(100+(i*200), 20, 90, 30);
                    stacksLabels.add(stack);
                    roundPanel.add(stack);
                    JLabel[] cards = new JLabel[5];
                    for (int j = 0; j < 5; j++) {
                        cards[j] = new JLabel();
                        cards[j].setBounds(80 + (j * 15) + (i*200), 55, 70, 100);
                        cardsLabels.add(cards[j]);
                    }
                    for (int j = 4; j >= 0; j--) {
                        roundPanel.add(cards[j]);
                    }
                }
                else{
                    if(i==5){
                        JLabel name = new JLabel();
                        name.setBounds(1120, 170, 90, 30);
                        roundPanel.add(name);
                        namesLabels.add(name);
                        JLabel stack= new JLabel();
                        stack.setBounds(1120, 190, 90, 30);
                        stacksLabels.add(stack);
                        roundPanel.add(stack);
                        JLabel[] cards = new JLabel[5];
                        for (int j = 0; j < 5; j++) {
                            cards[j] = new JLabel();
                            /*path = "/images/card_back.png";
                            cards[j].setIcon(new ImageIcon(this.getClass().getResource(path)));*/
                            cards[j].setBounds(1100 + (j * 15), 255, 70, 100);
                            cardsLabels.add(cards[j]);
                        }
                        for (int j = 4; j >= 0; j--) {
                            roundPanel.add(cards[j]);
                        }
                    }
                    else{
                        JLabel name = new JLabel();
                        name.setBounds(905 -((i-6)*200), 395, 90, 30);
                        namesLabels.add(name);
                        roundPanel.add(name);
                        JLabel stack= new JLabel();
                        stack.setBounds(905 -((i-6)*200), 415, 90, 30);
                        stacksLabels.add(stack);
                        roundPanel.add(stack);
                        JLabel[] cards = new JLabel[5];
                        for (int j = 0; j < 5; j++) {
                            cards[j] = new JLabel();
                            cards[j].setBounds(885 +(j * 15) -((i-6)*200), 450, 70, 100);
                            cardsLabels.add(cards[j]);
                        }
                        for (int j = 4; j >= 0; j--) {
                            roundPanel.add(cards[j]);
                        }
                    }
                }
            }
        }
        JLabel[] cards = new JLabel[5];
        for (int j = 0; j < 5; j++) {
            cards[j] = new JLabel();
            cards[j].setBounds(310 + (j * 150) , 255, 75, 100);
            cardsOnTable.add(cards[j]);
        }
        for (int j = 4; j >= 0; j--) {
            roundPanel.add(cards[j]);
        }
        tries=new JLabel();
        tries.setBounds(1100,10,250,25);
        tries.setForeground(new Color(255, 255, 0));
        roundPanel.add(tries);

        time=new JLabel();
        time.setBounds(1100,35,250,25);
        time.setForeground(new Color(255, 255, 0));
        roundPanel.add(time);

        ahead=new JLabel();
        ahead.setBounds(1100,60,250,25);
        ahead.setForeground(new Color(255, 255, 0));
        roundPanel.add(ahead);

        tied=new JLabel();
        tied.setBounds(1100,85,250,25);
        tied.setForeground(new Color(255, 255, 0));
        roundPanel.add(tied);

        behind=new JLabel();
        behind.setBounds(1100,110,250,25);
        behind.setForeground(new Color(255, 255, 0));
        roundPanel.add(behind);

        getContentPane().add( table, BorderLayout.CENTER );
        setPanel(startGamePanel);

        disableButtons();
        setResizable( false );
        setBounds( 35,20,1300,700);
        setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        setVisible( true );
    }
    private void setPanel(ArrayList<JComponent> panel){
        table.removeAll();
        table.setLayout(null);
        for(int i=0 ; i<panel.size(); i++) {
            table.add(panel.get(i));
        }
        repaint();
        validate();
    }



    private void getList(){
        JTextField id = new JTextField("Id");
        id.setEditable(false);
        id.setBounds(0,0,150,30);
        getListPanel.add(id);

        JTextField type = new JTextField("Type");
        type.setEditable(false);
        type.setBounds(150,0,150,30);
        getListPanel.add(type);

        JTextField nbPlayer = new JTextField("Nombre de joueurs");
        nbPlayer.setEditable(false);
        nbPlayer.setBounds(2*150,0,150,30);
        getListPanel.add(nbPlayer);

        JTextField minBet = new JTextField("Min bet");
        minBet.setEditable(false);
        minBet.setBounds(3*150,0,150,30);
        getListPanel.add(minBet);

        JTextField stack = new JTextField("stack");
        stack.setEditable(false);
        stack.setBounds(4*150,0,150,30);
        getListPanel.add(stack);

        String messageToSend="120 GETLIST";
        client.sendMessage(messageToSend);

        setPanel(getListPanel);
        while(!(client.getGameState() instanceof MenuState)){
        }
        int nbRooms = ((MenuState)client.getGameState()).getN();

        while(nbRooms!=0 &&(((MenuState)client.getGameState()).getGamesList()==null || ((MenuState)client.getGameState()).getGamesList(nbRooms-1)==null)){
            System.out.print("");
        }

        for(int i=0 ; i<nbRooms ; i++){
            JTextField id1 = new JTextField();
            id1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getId());
            id1.setBounds(0,30*(i+1),150,30);
            id1.setEditable(false);
            id1.setBackground(Color.WHITE);
            getListPanel.add(id1);

            JTextField type1 = new JTextField();
            type1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getType());
            type1.setBounds(150,30*(i+1),150,30);
            type1.setEditable(false);
            type1.setBackground(Color.WHITE);
            getListPanel.add(type1);

            JTextField nbPlayer1 = new JTextField();
            nbPlayer1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getPlayers().size());
            nbPlayer1.setBounds(2*150,30*(i+1),150,30);
            nbPlayer1.setEditable(false);
            nbPlayer1.setBackground(Color.WHITE);
            getListPanel.add(nbPlayer1);

            JTextField minBet1 = new JTextField();
            minBet1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getMinBid());
            minBet1.setBounds(3*150,30*(i+1),150,30);
            minBet1.setEditable(false);
            minBet1.setBackground(Color.WHITE);
            getListPanel.add(minBet1);

            JTextField stack1 = new JTextField();
            stack1.setText(""+((MenuState)client.getGameState()).getGamesList(i).getInitStack());
            stack1.setBounds(4*150,30*(i+1),150,30);
            stack1.setEditable(false);
            stack1.setBackground(Color.WHITE);
            getListPanel.add(stack1);

            int index = i;
            JButton joinRoom = new JButton(new AbstractAction() {
                public void actionPerformed(ActionEvent a) {
                    String messageToSend="130 JOIN "+index+1;
                    client.sendMessage(messageToSend);
                    setPanel(roundPanel);
                    //updateSlider();
                    while(!(client.getGameState() instanceof WaitingState)){
                    }
                    game = ((WaitingState)client.getGameState()).getCurrentGame();
                    player = game.getPlayer(playerName);

                    timer = new Timer(INTERVAL,
                            new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    if (client.getGameState() instanceof WaitingState && ((WaitingState) client.getGameState()).getStartRequest() && !stop) {
                                        createDialogue();
                                    }
                                    for(int i =0 ; i<game.getPlayers().size() ; i++){
                                        updatePlayerCards(game.getPlayers().get(i) , i , game.getPlayers().get(i).getName().equals(player.getName()));
                                    }

                                    if(player.getName().equals(game.getCurrentPlayer().getName()) && client.getGameState().isGameStarted()){
                                        if(!(game.getBidTurn()==2 && change)){
                                            timer.stop();
                                        }
                                    }
                                    updatePlayer();
                                    if(displayActionButtons){
                                        timer.restart();
                                        displayActionButtons=false;
                                    }
                                    setPanel(roundPanel);
                                }
                            });
                    timer.start();
                }
            });
            joinRoom.setText("Rejoindre");
            joinRoom.setBounds(5*150+30,30*(i+1),90,30);
            getListPanel.add(joinRoom);
        }

        JButton create = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                setPanel(createPanel);
            }
        });
        create.setText("Créer une partie");
        create.setBounds(900,50,150,30);
        getListPanel.add(create);

        JButton refresh = new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent a) {
                getList();
            }
        });
        refresh.setText("actualiser");
        refresh.setBounds(900,100,150,30);
        getListPanel.add(refresh);

        setPanel(getListPanel);
    }
    public void disableButtons() {
        foldButton.setEnabled( false );
        foldButton.setText("");
        checkButton.setEnabled( false );
        checkButton.setText("");
        callButton.setEnabled( false );
        callButton.setText("");
        raiseButton.setEnabled( false );
        raiseButton.setText("");
        changeButton.setEnabled( false );
        changeButton.setText("");
    }


    public void updatePlayerCards(Player p , int i , boolean showCard){

        if(showCard){
            namesLabels.get(i).setForeground(Color.WHITE);
            stacksLabels.get(i).setForeground(Color.WHITE);
        }
        else{
            namesLabels.get(i).setForeground(Color.BLACK);
            stacksLabels.get(i).setForeground(Color.BLACK);
        }
        namesLabels.get(i).setText(p.getName());
        stacksLabels.get(i).setText(""+p.getStack());
        int nbCards = 0;
        if(client.getGameState() instanceof PlayingTexasHoldemState && ((PlayingTexasHoldemState)client.getGameState()).isGameStarted() ){
            nbCards = 2;
        }
        else{
            if(client.getGameState() instanceof Playing5CardPokerState && game.getBidTurn()>=1){
                nbCards = 5;
            }
        }
        for(int j=0 ; j</*p.getCards().length*/nbCards; j++){
            if(showCard){
                cardsLabels.get(5*i+j).setIcon(ResourceManager.getCardImage(p.getCards()[j]));
            }
            else{
                if(client.getGameState().isEndgame()){
                    cardsLabels.get(5*i+j).setIcon(ResourceManager.getCardImage(p.getCards()[j]));
                }
                else {
                    cardsLabels.get(5 * i + j).setIcon(ResourceManager.getIcon("/images/card_back.png"));
                }
            }
        }
    }
    public void updateStatiscis(){
        if(client.getGameState() instanceof  PlayingTexasHoldemState || client.getGameState() instanceof  Playing5CardPokerState){
            String simulation="";
            if(game instanceof TexasHoldem){
                simulation=(TexasHoldemSimulator.simulate(game.getPlayer(playerName),(TexasHoldem) game)).toString();
            }else{
                simulation=(FiveCardSimulator.simulate(game.getPlayer(playerName),(PokerFerme)game)).toString();
            }
            if(simulation!=""){
                String simulNecessary=simulation.substring(5,simulation.length()-1);
                String results[]=simulNecessary.split(",");
                for(int i=0;i<results.length;i++){
                    String resul[]=results [i].split("=");
                    System.out.println( resul[0]+" "+resul[1]);
                    switch (resul[0]){
                        case "tries":tries.setText(" Tries :"+resul[1]);break;
                        case " time":time.setText(" Time :"+resul[1]);break;
                        case " ahead":ahead.setText(" Ahead :"+resul[1]);break;
                        case " tied":tied.setText(" Tied :"+resul[1]);break;
                        case " behind":behind.setText(" Behind :"+resul[1]);break;
                        default:;
                    }
                }
            }
        }
    }
    public void updateTableCards(){
        for (int j = 0; j < 5; j++) {
            String path = "/images/card_placeholder.png";
            cardsOnTable.get(j).setIcon(new ImageIcon(this.getClass().getResource(path)));
        }
        for(int j=0 ; j<((TexasHoldem)game).getTable().nbCards() ; j++){
            cardsOnTable.get(j).setIcon(ResourceManager.getCardImage(((TexasHoldem)game).getTable().getCard(j)));
        }
    }
    public void updatePlayer(){
        if(game instanceof TexasHoldem){
            updateTableCards();
        }
        if(player.getName().equals((game.getCurrentPlayer().getName())) && !updatedStatistics){
            updateStatiscis();
            updatedStatistics=true;
        }else{
            updatedStatistics=false;
        }
        bankText.setText("Stack : "+player.getStack());
        potText.setText("Pot : "+game.getPot());
        betText.setText("Bet : "+game.getBidAmount());
        yourBetText.setText("Your bet : "+player.getBidPerRound());
        if(client.getGameState().isEndgame()){
            messageText.setForeground(Color.GREEN);
            String message = "Les gagnants sont : ";
            for(int i=0 ; i<game.getWinners().size(); i++){
                message+=""+game.getWinners().get(i).getName()+", ";
            }
            messageText.setText(message);
        }
        else{
            if(player.getName().equals((game.getCurrentPlayer().getName()))){
                messageText.setForeground(Color.GREEN);
                messageText.setText("C'est votre tour");
            }
            else{
                messageText.setForeground(Color.BLACK);
                messageText.setText("C'est le tour de "+game.getCurrentPlayer().getName());
            }
        }

        disableButtons();
        if(client.getGameState().isGameStarted()) {
            if(player.getName().equals((game.getCurrentPlayer().getName()))){
                raiseButton.setEnabled(true);
                raiseButton.setText("Raise");
            }
            if (game.canCall(player)) {
                callButton.setEnabled(true);
                callButton.setText("Call");
            }
            if (game.canCheck(player)) {
                checkButton.setEnabled(true);
                checkButton.setText("Check");
            }
            if (game.canFold(player)) {
                foldButton.setEnabled(true);
                foldButton.setText("Fold");
            }
            if(player.getName().equals((game.getCurrentPlayer().getName())) && game.getType()==0 &&game.getBidTurn()==2){
                changeButton.setEnabled(true);
                changeButton.setText("Change Cards");
            }
        }


    }

    public void setUsedName(boolean usedName) {
        this.usedName = usedName;
    }
    public void setLargeName(boolean largeName) {
        this.largeName = largeName;
    }

    public void setWelcome(boolean welcome) {
        this.welcome = welcome;
    }

    public void setIncorrectType(boolean incorrectType) {
        this.incorrectType = incorrectType;
    }



    public void setIncorrectBet(boolean incorrectBet) {
        this.incorrectBet = incorrectBet;
    }

    public void setIncorrectStack(boolean incorrectStack) {
        this.incorrectStack = incorrectStack;
    }

    public void setIncorrectPlayers(boolean incorrectPlayers) {
        this.incorrectPlayers = incorrectPlayers;
    }

    void createDialogue(){
        do{
            reponseStartRequest=JOptionPane.showConfirmDialog(this,
                    "Êtes-vous prêt à vous lancer dans le Jeu ? \n            \"Réponse OBLIGATOIRE\" ",
                    "Lancement de la partie "+playerName,JOptionPane.YES_NO_OPTION);
            if(reponseStartRequest==JOptionPane.YES_OPTION){
                stop = true;
                String messageToSend="152 START YES";
                client.sendMessage(messageToSend);
            }else{
                stop = true;
                String messageToSend="152 START NO";
                client.sendMessage(messageToSend);
            }

        }while( reponseStartRequest!=JOptionPane.YES_OPTION && reponseStartRequest!=JOptionPane.NO_OPTION );
    }
}