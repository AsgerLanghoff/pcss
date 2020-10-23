import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lobby {

    //    private List<String>activeGameName = new ArrayList<String>();
    private List<SubLobby> subLobbies = new ArrayList<SubLobby>();

    private String playerID;
    private String[] OtherPlayers = {};
    private boolean isHost;
    private String currentLobby;
    private LobbySender sender;

    Scanner scanner = new Scanner(System.in);


    Lobby() throws IOException {
        System.out.println("Welcome to TANK the game!");



        System.out.println("What is your name?");
        String id = scanner.nextLine();
        this.playerID = id;
        sender = new LobbySender(id);
        sender.setPlayerID(id);
        sender.sendPlayerID();
        options();
    }

    public String PlayerID(){
        return playerID;
    }

    void createLobby(String code) throws IOException {
        SubLobby subLobby = new SubLobby(code, this.playerID);
        this.subLobbies.add(subLobby);
        sender.sendSubLobby(subLobby);
    }


    void updateLobbies(List<SubLobby> s) throws IOException{
        System.out.println("1");
        System.out.println(s.size());
        for(int i = 0; i < s.size(); i++) {
            System.out.println("2");

            if(subLobbies.size() == 0){
                subLobbies.add(s.get(i));
                System.out.println("New lobby found!: "+s.get(i).getLobbyName());
                break;
            }

            for (int j = 0; j<subLobbies.size(); j++){
                System.out.println("3");
                if(s.get(i).getLobbyName().equals(subLobbies.get(j).getLobbyName())){
                    System.out.println("4");
                    break; //Run next  i (atlså ikke add!!)
                } else if (j == subLobbies.size()-1){
                    subLobbies.add(s.get(i));
                    System.out.println("New lobby found!: "+s.get(i).getLobbyName());
                }
            }

        }
    }
    void joinLobby(String lobbyName) throws IOException {
        updateLobbies(sender.requestLobbyList());

        ////
        ////CREATE THE SUBLOBIES FIRST!!!!
        ////

        for (int i = 0; i < subLobbies.size(); i++) {
            if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                subLobbies.get(i).addToPlayers(this.playerID);
            }
        }
    }

    void checkDuplicate() {
    }


    void startGame(String lobby) {
        //Game game = new Game(playerID);
        String[] arguments = new String[]{playerID};
        Game.main(arguments);
    }

    void removeFromLobby(String lobbyName) {
        this.isHost = false;
        this.currentLobby = null;

        for (int i = 0; i < subLobbies.size(); i++) {
            if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                int j = subLobbies.get(i).getPlayers().indexOf(playerID);
                subLobbies.get(i).getPlayers().remove(j);
                if(subLobbies.get(i).getPlayers().size() == 0) {
                    subLobbies.remove(i);
                }
            }
        }


        options();
    }

    void playerOptions(String lobbyName) {
        System.out.println("Write \"list\" to see player list.");
        System.out.println("Write \"exit\" to leave lobby");
        try {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                removeFromLobby(lobbyName);
            } else if (input.equals("list")) {

                for (int i = 0; i < subLobbies.size(); i++) {
                    if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                        subLobbies.get(i).printPlayers();
                    }
                }
                playerOptions(lobbyName);
            } else {
                System.out.println("Unknown input, try again");
                playerOptions(lobbyName);
            }

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Unknown input, try again");
            hostOptions(lobbyName);
        }

    }


    void hostOptions(String lobbyName) {
        System.out.println("Write \"start\" to start the game, or write \"list\" to see player list.");
        System.out.println("Write \"exit\" to leave lobby");

        try {
            String input = scanner.nextLine();
            if (input.equals("start")) {
                System.out.println("GET REAAAADYYY!!!!");
                startGame(lobbyName); //Start ny instance af et game!
            } else if (input.equals("list")) {

                for (int i = 0; i < subLobbies.size(); i++) {
                    if (subLobbies.get(i).getLobbyName().equals(lobbyName)) {
                        subLobbies.get(i).printPlayers();
                    }
                }


                hostOptions(lobbyName);
            } else if (input.equals("exit")) {
                removeFromLobby(lobbyName);
            } else {
                System.out.println("Unknown input, try again");
                hostOptions(lobbyName);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Unknown input, try again");
            hostOptions(lobbyName);
        }

    }

    void options() {
        System.out.println("Write \"create\" if you want to create a new game, or write \"join\" to see a list of available games");
        String option = scanner.nextLine();
        try {
            if (option.equals("create")) {
                System.out.println("Creating Lobby. What should the lobby be named?");
                String lobbyName = scanner.nextLine();
                createLobby(lobbyName);
                this.currentLobby = lobbyName;
                isHost = true;
            } else if (option.equals("join")) {
                updateLobbies(sender.requestLobbyList());

                if (subLobbies.size() == 0) {
                    System.out.println("No available games at the moment :(");
                    options();
                }

                System.out.println("Which lobby would you like to join? Here is a list of active games");
                for (int i = 0; i < subLobbies.size(); i++) {
                    System.out.println(subLobbies.get(i).getLobbyName() + " (" + subLobbies.get(i).getPlayers().size() + "/4)");
                }
                String lobbyName = scanner.nextLine();
                this.currentLobby = lobbyName;
                isHost = false;

                joinLobby(lobbyName); //Try Catch her måske, kommer and på hvad funktionen skal
            } else {
                System.out.println("please enter a valid option");
                options();
            }
        } catch (
                Exception e) {
            System.out.println(e);
            System.out.println("please enter a valid option");
            options();
        }

        if (this.isHost) {
            hostOptions(this.currentLobby);
        } else {
            playerOptions(this.currentLobby);
        }
    }


    public static void main(String[] args) throws IOException {
        Lobby l = new Lobby();
    }
}