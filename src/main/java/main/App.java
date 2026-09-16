package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.AppSystem;
import core.Command;
import core.User;
import fileio.CommandInput;
import fileio.InputLoader;
import fileio.UserInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * main.App represents the main application logic that processes input commands,
 * generates outputs, and writes them to a file
 */
public final class App {
    private App() {
    }

    private static final String INPUT_USERS_FIELD = "input/database/users.json";

    private static final ObjectWriter WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();

    /**
     * Runs the application: reads commands from an input file,
     * processes them, generates results, and writes them to an output file
     *
     * @param inputPath path to the input file containing commands
     * @param outputPath path to the file where results should be written
     */
    public static void run(final String inputPath, final String outputPath) {
        // feel free to change this if needed
        // however keep 'outputs' variable name to be used for writing
        List<ObjectNode> outputs = new ArrayList<>();

        /*
            TODO 1 :
            Load initial user data and commands. we strongly recommend using jackson library.
            you can use the reading from hw1 as a reference.
            however you can use some of the more advanced features of
            jackson library, available here: https://www.baeldung.com/jackson-annotations
        */


        List<UserInput> userInputs = null;
        try {
            userInputs = InputLoader.UserLoader(INPUT_USERS_FIELD);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AppSystem system = new AppSystem();

        List<User> users = InputLoader.getListUsers(userInputs, system);
        // TODO 2: process commands.

        system.setUsers(users);

        List<CommandInput> commandInputs = null;
        try {
            commandInputs = InputLoader.CommandLoader(inputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Command> commands =
                InputLoader.getListCommands(commandInputs, users, system);

        system.setCommands(commands);

        AppSystem.setTicketId(0);

        outputs = system.iterate();

        // TODO 3: create objectnodes for output, add them to outputs list.

        // DO NOT CHANGE THIS SECTION IN ANY WAY
        try {
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            WRITER.withDefaultPrettyPrinter().writeValue(outputFile, outputs);
        } catch (IOException e) {
            System.out.println("error writing to output file: " + e.getMessage());
        }
    }
}
