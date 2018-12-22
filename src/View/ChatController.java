package View;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import application.Main;
public class ChatController {
	@FXML
	private Button button;
	@FXML
	private TextArea textArea;
	
	private Main main;
	
	@FXML
	private void buttonClicked(){
		
		String IP=main.myIP();
		int port = 9876;
		button.setText("시작하기");
		button.setOnAction(event ->{
			if(button.getText().equals("시작하기")) {
				main.startServer(IP, port);
				String myIP= main.myIP();
				Platform.runLater(()->{
					String message = String.format("[서버시작]\n", IP,port);
					textArea.appendText(message);
					textArea.appendText(myIP+"\n");
					button.setText("종료하기");
				});
			}else {
				main.stopServer();
				Platform.runLater(()->{
					String message = String.format("[서버종료]\n", IP,port);
					textArea.appendText(message);
					button.setText("시작하기");
				});
			}
		});
	}
	
	public void setMainApp(Main main) {
        this.main = main;
    }
}
