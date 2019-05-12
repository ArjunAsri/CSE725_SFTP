import java.io.IOException;

public class Thread {

	static Thread thread1 = new Thread(){
		public void run() throws IOException{
			Server server_1 = new Server();
			server_1.main(null);
		}
	};
	static Thread thread2 = new Thread(){
		public void run() throws IOException{
		//	client client_1 = new client();
			//client_1.main(null);
		}
	};
	


}
