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
import java.util.concurrent.ExecutorService; //쓰레드를 효율적으로 관리하기 위한 라이브러리
import java.util.concurrent.Executors;

import View.ChatController;

public class Main extends Application {
	//ExecutorService를 통해 쓰레드 숫자에 제한을 둘 수 있음
	public static ExecutorService threadPool; 
	public static Vector<Client> clients = new Vector<Client>();
	private ServerSocket serverSocket;
	private String myIP;
	//서버를 구동시켜 클라이언트의 연결을 기다리는 메소드
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
		
		//클라이언트가 접속할때까지 계속 기다리는 쓰레드
		Runnable thread  = new Runnable() {
			public void run() {
				while(true) {
					try {
						Socket socket = serverSocket.accept();
						clients.add(new Client(socket));
						System.out.println("[클라이언트 접속]"+socket.getRemoteSocketAddress()+
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
	//서버의 작동을 중지시키는 메소드
	public void stopServer() {
		try {
			//작동중인 모든 소켓 닫기
			Iterator<Client> iterator = clients.iterator();
			while(iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			//서버 소켓 객체 닫기
			if(serverSocket !=null &&!serverSocket.isClosed())
				serverSocket.close();
			//쓰레드 풀 종료하기
			if(threadPool != null && !threadPool.isShutdown())	
				threadPool.shutdown();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//IP 가져오기
	public String myIP() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			myIP=ip.getHostAddress();
		} catch (Exception e) {
			System.out.println(e);
		}
		return myIP;
	}
	
	
	//javafx실행
	private Stage primaryStage;
	private BorderPane rootLayout;
    @Override
    public void start(Stage primaryStage) {
    	
		this.primaryStage=primaryStage;
		initChat();
	}
    public void initChat() {
        try {
            // fxml 파일에서 상위 레이아웃을 가져온다.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/Chat.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            Scene scene = new Scene(rootLayout, 400,600);
            primaryStage.setTitle("[채팅 서버]");
            primaryStage.setOnCloseRequest(event->stopServer());
            primaryStage.setScene(scene);
            
            //ChatController를 연결
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
