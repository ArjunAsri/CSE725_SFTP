/*File Description: Client TCP connection for SFTP protocol*/
import java.io.*; 
import java.net.*; 
import java.util.Scanner;

/*Input and Output objects used in the class
 * fos - FileOutputStream
 * fis = FileInputStream
 * os = outputStream
 *------------ Provided ------------
 * inFromUser - BufferedReader
 * inFromServer - BufferedReader
 * outToServer - DataOutputStream*/

class TCPClient { 
    
    public static void main(String argv[]) throws Exception 
    { 
    	File filePath = new File("C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\TestFolder2\\TestDocument.txt");
        FileInputStream  fis 	= new FileInputStream(filePath);
        String sentence; 
        String modifiedSentence = null, previousResponse = null; 
        String serverDisconnected = "+Connection closing both client and server side";
        BufferedReader inFromUser = 
	    new BufferedReader(new InputStreamReader(System.in)); 
        /*Take input from server*/
        Scanner reader = new Scanner(System.in);
        int fileSizeReceivedFromClient = 0;
        
        Socket clientSocket = new Socket("127.0.0.1", 10007);  //opening a new socket
        //-----------------------//---------------------------------------------------------------------------
        
        /*for sending data we use file input stream*/
        byte ByteArray[] = new byte[(int)filePath.length()];
        
        //For file Transfer to the server
        OutputStream os = clientSocket.getOutputStream();        //-----------------------//
        
        DataOutputStream outToServer = 
	    new DataOutputStream(clientSocket.getOutputStream()); 
	
		BufferedReader inFromServer = 
		    new BufferedReader(new
			InputStreamReader(clientSocket.getInputStream())); 
		modifiedSentence = inFromServer.readLine(); //get the first Input From the server
		System.out.println("FROM SERVER: " + modifiedSentence); 
		while(true){
			//if(clientSocket.isClosed()){
			//	clientSocket.connect(new InetSocketAddress("127.0.0.1", 10007), 30);
			//}
			if(!clientSocket.isClosed()){
				
				boolean fileTransferActivated = false;
				//read sentence from the user
		        sentence = inFromUser.readLine(); 

		        //(subStringSendData.equals("SIZE"))
		        if(sentence.length()>=4){
			        String subStringSendData = sentence.substring(0, 4);
			        subStringSendData = subStringSendData.toUpperCase();
			        if((previousResponse =="+ok, waiting for files\n")){
			        	//fileTransferActivated = true;
			        	System.out.println("Size command activated\n");
			        	// fileInput.read(ByteArray, 0, ByteArray.length);
					     //outputStreamSendFile.write(ByteArray, 0, ByteArray.length);
			        }
		        }
		        //write Bytes to the server, user request
		        outToServer.writeBytes(sentence + '\n'); 

		        //File Receiver is for RETR command
		        
		        //reset count at the start every time
		        //last Character is used to complete the stream, the last character has to be specified
		        char lastCharacter = 'a';
		        int count = 0;
		        
		        while((modifiedSentence = inFromServer.readLine()) != null){
		        	//we append the character removed from the buffer by reading inFromServer.read()
		        	//the lastCharacter is appended to the string
		        	if(modifiedSentence.equals(serverDisconnected)){ 
		        		clientSocket.close();
		        		//System.out.println("Server close detected\n");
		        		System.out.println("FROM SERVER: " + modifiedSentence); //append last character to the string
		        		
		        		break;
		        	}
		        	//append the character
		        	if(count>0){
		        		modifiedSentence = lastCharacter + modifiedSentence;
		        		
		        	}
		        	//server output
		        	System.out.println("FROM SERVER: " + modifiedSentence); 
		        	if((modifiedSentence.equals("+ok, waiting for files"))){
			        	//fileTransferActivated = true;
			        	System.out.println("File is being sent now\n");
			        		fis.read(ByteArray, 0, ByteArray.length);
			        		os.write(ByteArray, 0, ByteArray.length);
			        		//outToServer.close();
				        	// outputStreamSendFile.write(ByteArray, 0, ByteArray.length);
				        	// outputStreamSendFile.flush();
				        	 //outputStreamSendFile.close();
			        	//fileInput.read(ByteArray, 0, ByteArray.length);
			        	 //outputStreamSendFile.write(ByteArray, 0, ByteArray.length);
			        	 System.out.println("Done Sending File\n");
			        }else if(modifiedSentence.equals("+ok sending data")){
			        	System.out.println("Receiving File from server\n");		
			        	 byte receiveArray[] = new byte[fileSizeReceivedFromClient];
			        	FileOutputStream fos = new FileOutputStream(filePath);
					    InputStream is = (clientSocket.getInputStream());

						is.read(receiveArray, 0, fileSizeReceivedFromClient);
						fos.write(receiveArray, 0, fileSizeReceivedFromClient);
						fos.close();
			        }else{
			        	
			        	//receiving data from the server, finding out the data size from the string
				        	int decimalIndex = modifiedSentence.indexOf(',');
				        	if(decimalIndex!=-1){
					        	String substring = modifiedSentence.substring(0,decimalIndex);
					        	try{
					        		fileSizeReceivedFromClient = Integer.parseInt(substring);
					        	}catch(NumberFormatException e){
					        		//System.out.println(fileSizeReceivedFromClient);
					        		
					        	}
				        	}
			        }
		        	if((lastCharacter = (char) inFromServer.read())=='\0'){
		        		break;
		        	}
		        	count++;
		        	
		        	//output.append('\n');
		        	 
		        	 
		        }; 
		        
		       
		
		        //modifiedSentence = inFromServer.readLine();
		        //System.out.println("FROM SERVER: " + modifiedSentence);
		        //System.out.println("Testing\n");
	        
	        //If received from the server done command then close the socket
	        //modifiedSentence = modifiedSentence.substring(0, 19);
	        //if(modifiedSentence == "+Connection closing"){
	        
	        //}
			}else{
				
				System.out.println("Not Connected to the server, type \"Connect\" to reconnect\n ");
				String userInput = reader.nextLine();
				//System.out.println(userInput.length());
				String connect = "CONNECT"; String upperCaseInput = userInput.toUpperCase();
				if(upperCaseInput.equals(connect)){
					System.out.println("Opening new Server socket\n ");
					  clientSocket = new Socket("127.0.0.1", 10007); //reconnect to the server
					  inFromServer = 
							    new BufferedReader(new
								InputStreamReader(clientSocket.getInputStream())); 
					  outToServer = 
							    new DataOutputStream(clientSocket.getOutputStream());
				}
			}
        }
	
    } 
} 
