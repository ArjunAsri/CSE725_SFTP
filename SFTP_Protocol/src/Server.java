import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

/*Input Output stream objects used
 *is -InputStream(connectionSocket.getInputStream())
 *fos -  FileOutputStream - This is needed to save the file
 * provided
 * inFromCliet BufferedReader
 * outToClient DataOutputStream
 * */
public class Server {

	public static void main(String args[]) throws IOException{
		String PositiveGreeting = "+MIT-XX SFTP Service\n";
		String NegativeGreeting = "-MIT-XX Out to Lunch\n";
		String clientSentence = null, outputFromServer = null; 
		String invalidResponse,capitalizedSentence = null; 
		boolean flagInvalidResponse = true, doneCommandIssued = false,fileTransferSuccessful = false, fileAppendSuccessful = false;
		boolean connectionSocketConnected = true, sendFile = false, fileSentToClient = false, FileStuffjustExecuted = false;
		int commandCode; //code for the command sent by the user to call the function
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(10007); 
		 Socket connectionSocket = welcomeSocket.accept(); 
		 //after connection accepted we reset the flagInvalidResponse
		 flagInvalidResponse = false;
		 ArrayList<Socket> clients = new ArrayList<Socket>(); //multiple clients
		    BufferedReader inFromClient = 
			new BufferedReader(new
			    InputStreamReader(connectionSocket.getInputStream())); 
		    
		    DataOutputStream  outToClient = 
			new DataOutputStream(connectionSocket.getOutputStream()); 
		    int portNumber = connectionSocket.getPort();
		    outToClient.writeBytes(PositiveGreeting);
		    USER user = new USER();
		    InputStream is = (connectionSocket.getInputStream());
			boolean negativeUserInput = false;
		    while(true){
				 //from the USER class
				while(connectionSocketConnected){
					
								if(sendFile){
									FileStuffjustExecuted = true;
									byte ByteArray[] = new byte[user.fileStorSize];
									
									//boolean fileTransfered = user.receiveFile(connectionSocket);
									fileTransferSuccessful = false;
									if(!user.appendToFile){
										try{
											FileOutputStream fos = new FileOutputStream(user.CurrentDirectory+"\\"+user.fileStorName);

											is.read(ByteArray, 0, user.fileStorSize);
											fos.write(ByteArray, 0, user.fileStorSize);
											fos.close();
											sendFile = false;
											fileTransferSuccessful = true;
											//String outputString = "+Saved <"+user.fileStorName+">\n\0"; 
											
										}catch(Exception e){ //catch any exception file size may not be specified correctly
											fileTransferSuccessful = false;
											outToClient.writeBytes("-Couldn't save because ....\n\0");
										}
									}else{
										try {
											File fileToWriteTo = new File(user.CurrentDirectory+"\\"+user.fileStorName);
											is.read(ByteArray, 0,user.fileStorSize);
											//FileWriter output = new FileWriter(fileToWriteTo, true);
											//BufferedWriter bw = new BufferedWriter(output);
											//PrintWriter printWriter = new PrintWriter(bw);
											//String str = new String(ByteArray, "UTF-8");
											FileOutputStream fos = new FileOutputStream(user.CurrentDirectory+"\\"+user.fileStorName, true);

											fos.write(ByteArray, 0, user.fileStorSize);
											fos.close();
											//buffWriter.append("Hello");
											//buffWriter.close();
											//printWriter.println(str);
											//printWriter.close();
											//output.append("Hello\n");
										    //output.close();
											fileAppendSuccessful = true;
										    user.appendToFile = false;
										}catch (Exception e) {
										    //exception handling left as an exercise for the reader
										}
										
									}
									sendFile = false;
								}
									
								//For Retr 
								if(user.sendFileToClient){
									byte ByteArray[] = new byte[user.fileStorSize];
									try{
							        FileInputStream  fis 	= new FileInputStream(user.CurrentDirectory+"\\"+user.fileRetrName);
							        OutputStream os = connectionSocket.getOutputStream();        //-----------------------//
							       
					        		fis.read(ByteArray, 0, ByteArray.length);
					        		os.write(ByteArray, 0, ByteArray.length);
									user.sendFileToClient = false;
									fileSentToClient = true;
									}catch(Exception e){
										
									}
								}
								//while (!(inFromClient.ready()));
								
								//if non of the file commands, then read the message
								//try{
								//while((clientSentence = inFromClient.readLine()) == null);
								clientSentence = inFromClient.readLine(); 
								//	//negativeUserInput = false;
								//}catch (IOException e) {
									//capitalizedSentence = " ";
							//	}

							    //ENTER KEY IS NEEDED OTEHRWISE CONNECTION WILL NOT WORK, SERVER WILL STILL BE WORKING
							    capitalizedSentence = clientSentence.toUpperCase() + '\n';//ENTER KEY IS NEEDED OTHERWISE CONNECITON DOES NOT WORK WHEN SENDING DATA
							    
							    
							    //outToClient.writeBytes(capitalizedSentence);
							   if(clientSentence.length()>= 4){
								   if(clientSentence.length()==4){
									   outputFromServer = clientSentence.substring(0, 4)+ " \n"; //this has added space
								   }else{
									   outputFromServer = clientSentence.substring(0, 5)+ '\n';  
								   }
							    	outputFromServer = outputFromServer.toUpperCase(); 
							    	commandCode = checkCommand(outputFromServer);
							    	
							    	if((commandCode > 0) && (commandCode < 16)){
								    	switch(commandCode){
										case 1: 
											outputFromServer = user.USERCommand(clientSentence + '\n');
											break;
										case 2: 
											outputFromServer = user.ACCTCommand(clientSentence + '\n');
											break;
										case 3: 
											outputFromServer = user.PASSCommand(clientSentence + '\n');
											break;
										case 4: //The
											if(user.SystemLogged){
												outputFromServer = user.TypeCommand(clientSentence.toUpperCase() + '\n');
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 5: 
											ArrayList<String> FileArray = new ArrayList<String>();
											FileArray = user.LISTCommand(clientSentence.toUpperCase() + '\n');
											//check if there is any returned data, any files found in the specified folder
											if(user.SystemLogged){
												if(FileArray.size()>0){
													outputFromServer = FileArray.get(0) +'\n';
													for(int i = 1;i <FileArray.size();i++ ){
														outputFromServer = outputFromServer + (FileArray.get(i))+'\n';
													}
												}else{
													outputFromServer=("Nothing in folder \n");
												}
									    	}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 6:
											if(user.SystemLogged){
												outputFromServer = user.CDIRCommand(clientSentence + '\n');
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 7: 
											if(user.SystemLogged){
											outputFromServer = user.KILLCommand(clientSentence + '\n'); //the last bit  '\n' is length()-1 in the user code when using substring
											}else{
												outputFromServer = "Login First\n";
											}
											break;														//also good habit including '\n' in every string because in case we need to print it then the server seems to crash 
										case 8:
											if(user.SystemLogged){
												user.FileExists = false;
												outputFromServer = user.NAMECommand(8,clientSentence+'\n');
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 9:
											doneCommandIssued = true;
											outputFromServer = user.DONECommand(); //done command issued
											break;
										case 10:
											if(user.SystemLogged){
												outputFromServer = user.RETRCommand(clientSentence.toUpperCase()+"\n",10);
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 11:
											if(user.SystemLogged){
												outputFromServer = user.STORCommand(clientSentence.toUpperCase() +"\n",clientSentence +"\n",connectionSocket);
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 12:  //for renaming the file
											if(user.SystemLogged){
												outputFromServer = user.NAMECommand(12,clientSentence+'\n');
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 13: 
											if(user.SystemLogged){
												if(user.receiveFileAttributes){
													outputFromServer = user.STORCommand(clientSentence.toUpperCase() + "\n", clientSentence + "\n",connectionSocket);
												}else{
													outputFromServer = "-Use STOR command\n";
												}
											}else{
												outputFromServer = "Login First\n";
												}
											break;
										case 14:
											if(user.SystemLogged){
												outputFromServer = user.RETRCommand(clientSentence.toUpperCase()+"\n",14);
											}else{
												outputFromServer = "Login First\n";
											}
											break;
										case 15:
											if(user.SystemLogged){
												outputFromServer = user.RETRCommand(clientSentence.toUpperCase()+"\n",15);
											}else{
												outputFromServer = "Login First\n";
											}
											break;

										}

								    	invalidResponse = outputFromServer.substring(0,1);
								    	

								    	String doneCommand = outputFromServer.substring(0, 11);
								    	char negativeResponse = outputFromServer.charAt(0);
								    	//(negativeResponse == '-')||
								    		if((doneCommand.equals("+Connection"))||(negativeUserInput)){
								    			outToClient.writeBytes(outputFromServer);
									    		outToClient.write('\0');
									    		connectionSocket.close(); //the connection was successfully closed
									    		connectionSocketConnected = false;
									    		//wait for
									    		//System.out.println("Disconnecting Client\n");
								    			
								    		}else{//The connection will remain in place
								    			if(outputFromServer.equals("+ok, waiting for files\n")){
								    				outToClient.writeBytes(outputFromServer);
									    			outToClient.write('\0');
									    			sendFile =true;
									    			//System.out.println(sendFile);
								    			}else{
								    				fileSentToClient = false;fileAppendSuccessful = false;
								    				fileTransferSuccessful = false; 
									    			outToClient.writeBytes(outputFromServer);
									    			outToClient.write('\0');
								    			}
								    	//		//inFromClient.close(); //close the socket blocking input and output Stream
									    //	}else{
									    		
									    	}
							    	
							    	}else{

							    	
								    	outToClient.writeBytes( NegativeGreeting);
								    	outToClient.write('\0');
							    	
							    	}
							   
					     //if the clientSentence is not valid 
						}else{
							if(FileStuffjustExecuted){
							    	if(fileTransferSuccessful){
							    		outToClient.writeBytes("+Saved <"+user.fileStorName+">\n\0");
							    		outToClient.write('\0');
							    		fileTransferSuccessful = false; //reset the flag
							    	}else if(fileAppendSuccessful){
							    		outToClient.writeBytes("+File Appended <"+user.fileStorName+">\n\0");
							    		outToClient.write('\0');
							    		fileAppendSuccessful = false; //reset the flag
							    	}else if(fileSentToClient){
							    		outToClient.writeBytes("+File Sent <"+user.fileRetrName+">\n\0");
							    		outToClient.write('\0');
							    		fileSentToClient = false;
							    	}
							    	FileStuffjustExecuted = false;
							   }else{
							    	outToClient.writeBytes(NegativeGreeting);
							    	outToClient.write('\0');
							    	}
							   	}
								
							   //part of while(socketConnected) loop, while loop ends here
				}
				
				 connectionSocket = welcomeSocket.accept(); 
				 //after connection accepted we reset the flagInvalidResponse
				 flagInvalidResponse = false;
			
				 inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));  
				 outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
				//IF connectionSocketConnected is false then we chek the if condition
				//if(connectionSocket == welcomeSocket.accept()){
				connectionSocketConnected = true;
				//}
		}
	}
	
	
	
	public static int checkCommand(String inputFromClient){
		int commandCode = 0;

		
		switch(inputFromClient){
		case  "USER \n": //user command is for logging in
				commandCode = 1;
				break;
		case  "ACCT \n":// Account we want to use on the remote system
				commandCode = 2;
				break;
		case  "PASS \n": //Password stored on the remote system
				commandCode = 3;
				break;
		case  "TYPE \n": //The TYPE command can only be specified if we receive '!' from the server
				commandCode = 4;
				break;
		case  "LIST \n": //returns the current directory-path
				commandCode = 5;
				break;
		case  "CDIR \n": //change the current directory to the argument that is passed
				commandCode = 6;
				break;
		case  "KILL \n": //This will delete the file from the remote system
				commandCode = 7;
				break;			
		case  "NAME \n": //renames the old file specification to the new file specification
				commandCode = 8;
				break;	
		case  "DONE \n": //Tells the remote system you are done and then both the server and the client close the connection
				commandCode = 9;
				break;	
		case  "RETR \n": //Requests the remote system sends the specified file
				commandCode = 10;
				break;	
		case  "STOR \n": //Tells the remote system to receive the following file and store it on the system
				commandCode = 11;
				break;	
		case  "TOBE \n":
				commandCode = 12;
				break;
		case  "SIZE \n":
				commandCode = 13;
				break;
		case  "SEND \n": //For RETR command
			commandCode = 14;
			break;
		case  "STOP \n"://For RETR command
			commandCode = 15;
			break;
				
		}
		
		return commandCode;
	}
	
}
	