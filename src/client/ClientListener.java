package client;

public class ClientListener implements Runnable {

    private final ClientResponseReceiver responceReceiver;
    
    public ClientListener(ClientResponseReceiver responceReceiver) {
        this.responceReceiver = responceReceiver;
    }
    


    @Override
    public void run() {
        try {
            while (true) {
            	responceReceiver.getResponce();
            }
        } catch (Exception e) {
            System.err.println("Ошибка в ClientListener: " + e.getMessage());
        }
    }
}
