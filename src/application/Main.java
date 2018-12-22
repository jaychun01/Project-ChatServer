package application;
	
import java.awt.TextArea;
import java.io.IOException;
import java.net.InetAddress; 
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService; //�����带 ȿ�������� �����ϱ� ���� ���̺귯��
import java.util.concurrent.Executors;

import View.ChatController;

public class Main extends Application {
	//ExecutorService�� ���� ������ ���ڿ� ������ �� �� ����
	public static ExecutorService threadPool; 
	public static Vector<Client> clients = new Vector<Client>();
	private ServerSocket serverSocket;
	private String myIP;
	//������ �������� Ŭ���̾�Ʈ�� ������ ��ٸ��� �޼ҵ�
	public void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));
		}catch(Exception e) {
			e.printStackTrace();
			if(!serverSocket.isClosed())
				stopServer();
			return;
		}
		
		//Ŭ���̾�Ʈ�� �����Ҷ����� ��� ��ٸ��� ������
		Runnable thread  = new Runnable() {
			public void run() {
				while(true) {
					try {
						Socket socket = serverSocket.accept();
						clients.add(new Client(socket));
						System.out.println("[Ŭ���̾�Ʈ ����]"+socket.getRemoteSocketAddress()+
								": "+Thread.currentThread().getName());
					}catch(Exception e) {
						if(!serverSocket.isClosed())
							stopServer();
						break;
					}
				}
			}
		};
		threadPool= Executors.newCachedThreadPool();
		threadPool.submit(thread);
	}
	//������ �۵��� ������Ű�� �޼ҵ�
	public void stopServer() {
		try {
			//�۵����� ��� ���� �ݱ�
			Iterator<Client> iterator = clients.iterator();
			while(iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			//���� ���� ��ü �ݱ�
			if(serverSocket !=null &&!serverSocket.isClosed())
				serverSocket.close();
			//������ Ǯ �����ϱ�
			if(threadPool != null && !threadPool.isShutdown())	
				threadPool.shutdown();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//IP ��������
	public String myIP() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			myIP=ip.getHostAddress();
		} catch (Exception e) {
			System.out.println(e);
		}
		return myIP;
	}
	
	
	//javafx����
	private Stage primaryStage;
	private BorderPane rootLayout;
    @Override
    public void start(Stage primaryStage) {
    	
		this.primaryStage=primaryStage;
		initChat();
	}
    public void initChat() {
        try {
            // fxml ���Ͽ��� ���� ���̾ƿ��� �����´�.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/Chat.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            Scene scene = new Scene(rootLayout, 400,600);
            primaryStage.setTitle("[ä�� ����]");
            primaryStage.setOnCloseRequest(event->stopServer());
            primaryStage.setScene(scene);
            
            //ChatController�� ����
            ChatController controller =loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	
	public static void main(String[] args) {
		launch(args);
	}
}
