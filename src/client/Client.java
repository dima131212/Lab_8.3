package client;

import java.io.*;
import java.net.ConnectException;
import java.util.*;

import client.dataInput.DataInput;
import client.dataStorage.CollectionView;
import client.dataStorage.CurrentClient;
import client.dataStorage.DataForMovie;
import client.dataValidation.CheckData;
import client.dataValidation.CheckInput;
import client.dataValidation.CommandParser;
import client.executeScript.ExecuteScript;
import client.executeScript.FileStack;


public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 2348;
    
    static DataInput dataInput = new DataInput();
    static CheckInput checkInput = new CheckInput();
    public static CurrentClient currentClient;
    
    @SuppressWarnings("unchecked")
	public static void main(String[] args) {



        CommandParser commandParser = new CommandParser();
        ExecuteScript executeScript = new ExecuteScript();

        try {
        	 ClientConnection connection = new ClientConnection();
        	 connection.connect(SERVER_HOST, SERVER_PORT);
        	 ClientRequestSender sender = new ClientRequestSender(connection.getOut());
             ClientResponseReceiver receiver = new ClientResponseReceiver(connection.getIn());
             String input;
             

             while (true) {
            	 System.out.println("Зарегистрируйтесь или авторизуйтесь в системе введите \"join\" или \"register\" ");
            	 input = dataInput.input();
            	 if (input.equalsIgnoreCase("join") || input.equalsIgnoreCase("register")) {
            		    sender.send(input); 
            		    receiver.getResponce(); 

            		    System.out.print("Логин: ");
            		    String login = dataInput.input();

            		    System.out.print("Пароль: ");
            		    String password = dataInput.input();

            		    sender.send(new String[]{login, password}); 
            		    
            		    String responce = (String) receiver.getData();
            		    
            		    if(responce.equals("OK")) {
            		    	System.out.println("Welcome");
            		    	currentClient = new CurrentClient(login, password);
            		    	break;
            		    }
            		    else {
            		    	System.out.println(responce);
            		    }
            		    
            	 }
            	 else {
            		 System.out.println("Введена некорректная команда");
            	 }
            	 
             }
            
             new DataForMovie(receiver);
             sender.send(new Object[]{"load_next_page", new Object[]{1L}, currentClient.getUserName(), currentClient.getUserPassword()});
             //new CollectionView((HashMap<Long, String>) receiver.getResponce());
             
             ServerPoller serverPoller = new ServerPoller(sender, currentClient.getUserName(), currentClient.getUserPassword());
             serverPoller.startPolling();
             Thread listenerThread = new Thread(new ClientListener(receiver));
             listenerThread.start();

            while (true) {
                System.out.println("> ");
                
                input = dataInput.input();

                if (input.trim().equalsIgnoreCase("exit")) {
                    System.out.println("Завершение работы клиента...");
                    
                    serverPoller.stopPolling();
                    listenerThread.interrupt();
                    break;
                }

                if (commandParser.parseCommandName(input)[0].equals("execute_script")) {
                    String scriptFileName = commandParser.parseCommandName(input)[1];
                    executeScript.executeScript(scriptFileName, connection.getOut(), connection.getIn(), DataForMovie.additionalInput);
                    FileStack.fileStack.clear();
                    continue;
                }

                String[] commandParts = commandParser.parseCommandName(input);
                String commandName = commandParts[0];

                Object[] arg;
                boolean needsAdditionalInput = DataForMovie.additionalInput.getOrDefault(commandName, false);
                boolean hasArgument = commandParts.length > 1;

                if (needsAdditionalInput && hasArgument) {
                	Long id =0L;
                	if(CheckData.isLong(commandParts[1])) {
                		id = Long.parseLong(commandParts[1]);
                	}
                	else {
                		System.out.println("Ошибка: параметр должен быть числом");
                		continue;
                	}
                	
                	Map<String, Object> movieData = new LinkedHashMap<>(checkInput.checkInput());
                    
                	arg = new Object[]{id, movieData};
                } else if (needsAdditionalInput) {
                    Map<String, Object> movieData = new LinkedHashMap<>(checkInput.checkInput());
                    arg = new Object[]{movieData};
                } else if (hasArgument) {
                	if(CheckData.isInteger(commandParts[1]) && commandParts.length <= 2) {
                		arg = new Object[]{Long.parseLong(commandParts[1])};
                	}
                	else if (hasArgument && commandParts.length > 2) {
                		String sortParam = "";
                		for(int i = 2; i <=commandParts.length-1; i++) {
                			sortParam = sortParam +  " " +commandParts[i];
                		}
                		arg = new Object[]{Long.parseLong(commandParts[1]), sortParam};
                	}
                	else {
                		System.out.println("Ошибка: параметр должен быть числом");
                		continue;
                	}
                } else {
                    arg = new Object[]{};
                }
                
                String login = currentClient.getUserName();
                String password = currentClient.getUserPassword();
                sender.send(new Object[]{commandName, arg, login, password});


            }
        } 
        catch (ConnectException e) {
            System.out.println("Сервер занят, подождите немного.");
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}

