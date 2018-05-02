package edu.nyu.cs9053.homework11.network;

import edu.nyu.cs9053.homework11.game.Difficulty;
import edu.nyu.cs9053.homework11.game.GameProvider;
import edu.nyu.cs9053.homework11.game.screen.InputMove;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * User: blangel
 *
 * A blocking IO implementation of a client which requests moves from a remote server implementing the
 * {@linkplain edu.nyu.cs9053.homework11.network.NetworkGameProvider}
 */
public class GameClient implements GameProvider {

    private final InputStream serverInput;

    private final OutputStream serverOutput;

    private final Difficulty difficulty;

    public static final String NEXT_FOES = "NEXT_FOES";

    public static final String NEXT_MOVE = "NEXT_MOVE";

    public static GameClient construct(Difficulty difficulty) {
        try {
            Socket client = new Socket(GameServer.SERVER_HOST, GameServer.SERVER_PORT);
            return new GameClient(difficulty, client.getInputStream(), client.getOutputStream());
        } catch (IOException e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
            throw new RuntimeException("Failed to connect to server");
        }

    }

    public GameClient(Difficulty difficulty, InputStream serverInput, OutputStream serverOutput) {
        this.difficulty = difficulty;
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public int getRandomNumberOfNextFoes() {
        PrintWriter out = new PrintWriter(serverOutput, true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(serverInput, StandardCharsets.UTF_8));
        String randomeNumberOfNextFoes;
        try {
            out.println(processRequest(NEXT_FOES, difficulty.getLevel()));
            randomeNumberOfNextFoes = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("FAIL TO GET RANDOM NEXT FOES: " + e.getMessage());
        }
        return Integer.valueOf(randomeNumberOfNextFoes);
    }

    @Override
    public InputMove getRandomNextMove() {
        PrintWriter out = new PrintWriter(serverOutput, true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(serverInput, StandardCharsets.UTF_8));
        String inputMove;
        try {
            out.println(processRequest(NEXT_MOVE, difficulty.getLevel()));
            inputMove = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("FAIL TO GET RANDOM NEXT FOES: " + e.getMessage());
        }
        switch (inputMove) {
            case "Up":
                return InputMove.Up;
            case "Down":
                return InputMove.Down;
            case "Left":
                return InputMove.Left;
            case "Right":
                return InputMove.Right;
            default:
                throw new AssertionError("Parse inputMove error!");
        }
    }

    private String processRequest(String operation, int difficulty) {
        return String.format("{operation:%s, difficulty:%d}%n",operation, difficulty);
    }

}