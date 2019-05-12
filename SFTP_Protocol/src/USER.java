
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import java.nio.file.*;

public class USER {

	int flagTypeCommand = 0;
	String userName, userPassword, storedUserName = null, storedUserPassword = null, fileStorName = null, fileRetrName = null;
	boolean UserIDneeded = true, PasswordNeeded = false,userIDcorrect = false, PasswordCorrect = true;
	boolean AccountOkNotNeeded = false, AccountValid = true;
	boolean passwordOnRemoteSystem = false, appendToFile = false, sendFileToClient= false, bytesReadyToBeSent = false;
	boolean fileClearedForStore = false, cdirLock =true, cdirPass = false;
	boolean receiveFileAttributes = false, PasswordCommmand = false, SystemLogged = false;
	boolean FileExists = false; String CurrentDirectory = System.getProperty("user.dir");
	String userDetailsDirectory =  System.getProperty("user.dir");
	
	int AsciiMode =0, BinaryMode = 1, ContinousMode =2, fileTypeMode = 1, fileStorSize = 0, fileVersion = 0;
	String previousFileName;
	ArrayList<String> userDATA = new ArrayList<String>();
	
	
	/*Helper functions for checking user ID and Password*/
	public boolean checkUserID(String userName){
		boolean found = false;
		try{
			FileReader fr = new FileReader("C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\UserDetails\\Users&Passwords.txt");
			BufferedReader br = new BufferedReader(fr);
			String storedUserData = null;
			while((storedUserData=br.readLine())!=null){
				int nextIndex = 0, startIndex = 0;
				String subStringToCheck;
				while(nextIndex != -1){ //while we have not read the entire line i.e. not checked for every ','
					nextIndex = storedUserData.indexOf(',', startIndex);
					if(nextIndex == -1){//if no more to read in the line then move to the next line
						break;
					}
					subStringToCheck = storedUserData.substring(startIndex, nextIndex);
					subStringToCheck = subStringToCheck.trim();
					//System.out.println(subStringToCheck);
					userName = userName.trim();
					startIndex = nextIndex+1;//we start after the index of  ','
					//System.out.println(subStringToCheck);
					if(subStringToCheck.equals(userName)){ //if username found then store all the relevant data and break the loop	
						char ID = storedUserData.charAt(0);
						String str = ID + ""; //convert char to string
						userDATA.add(str); //add ID
						char passwordNeeded = storedUserData.charAt(2);
						str = passwordNeeded + ""; //convert char to string
						userDATA.add(str); //add y/n for password needed or not
						storedUserName = userName;
						userDATA.add(userName);
						if(userDATA.get(1).equals("y")){
							//get the user password and ACCTname if password exists
							str = storedUserData.substring(12,20); //password
							userDATA.add(str);
							str = storedUserData.substring(21,storedUserData.length());
							userDATA.add(str); //add the ACCT Name
						}else{
							//only get the ACCT name
							str = storedUserData.substring(12,storedUserData.length()); //password
							userDATA.add(str);
						}
						
						//System.out.println(userDATA.get(0)); //ID
						//System.out.println(userDATA.get(1)); //y/n
						//System.out.println(userDATA.get(2)); //userName
						//System.out.println(userDATA.get(3)); //ACCT Name
						//System.out.println(userDATA.get(4)); //password
						found = true;
						break;
					}
				}
				//Iterate to check the next line in the loop for the required username
			}
		}catch(IOException e){
			System.out.println("error reading data\n");
		}
		if(found){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean checkUserPassword(String userPassword){
		if(storedUserPassword==userPassword){
			return true;
		}else{
			return false;
		}
	}
	


	/*USER user -id Your UserId on the remote system
	* Outputs - !<user-id> logged in ->This means that you dont need an account or password or or you specified a user-id not needing them*/
	/*User ID user command*/
	public String USERCommand(String Data){
		//reset array for new user
		userDATA.clear();
		int startIndex = 5;
		//check if the username is part of the string or not to avoid errors
		if(Data.length()>6){
			userName = Data.substring(startIndex,Data.length()-1);
			userIDcorrect = checkUserID(userName);
		/*check if UserID needed*/
			if(userIDcorrect){
						if(userDATA.get(1).equals("y")){//userIDneeded
						/*check userIDcorrect*/
							SystemLogged = false;
								PasswordCommmand = true;
								return ("+<"+userName+"> valid, send account and password\n");	
						}else{
							SystemLogged = true;
							PasswordCommmand = false;
							return ("!<"+userName+"> logged in\n");
						}	
			}else{
				SystemLogged = false;
				PasswordCommmand = false;
					return ("-Invalid, try again\n");
					
			}
		
		}else{
			SystemLogged = false;
			PasswordCommmand = false;
			return ("-Type username with the USER command\n");
			
		}
	}
		
	

	public String ACCTCommand(String Data){
		userDATA.clear();
		int startIndex = 5;
		//check if the username is part of the string or not to avoid errors
		if(Data.length()>6){
			userName = Data.substring(startIndex,Data.length()-1);
			//System.out.println(userName);
			userIDcorrect = checkUserID(userName);
		/*check if UserID needed*/
			if(userIDcorrect){
						if(userDATA.get(1).equals("y")){//userIDneeded
						/*check userIDcorrect*/
								PasswordCommmand = true;
								SystemLogged = false;
								return ("+Account valid, send password\n");	
						}else{
							SystemLogged = true;
							PasswordCommmand = false;
							return ("! Account valid, logged-in\n");
						}	
			}else{
				PasswordCommmand = false;
				SystemLogged = false;
					return ("-Invalid, try again\n");
					
			}
		
		}else{
			SystemLogged = false;
			PasswordCommmand = false;
			return ("-invalid account, try again\n");
			
		}
	}
	
	
	public String PASSCommand(String Data){		
		//check if the password was provided or not 
		
		if((Data.length()>6)&&(PasswordCommmand)){
			String str = Data.substring(5,Data.length());
			str = str.trim();
			passwordOnRemoteSystem = checkUserPassword(Data);
			//System.out.println(str);
			if(userDATA.get(3).equals(str)){
				SystemLogged = true;
				////////////////////////////
				if(cdirPass){
					cdirLock = false;
					cdirPass = false;
				}else{
					cdirLock = true;
				}
				////////////////
				return("! Logged in\n");
				
			}else{
				cdirLock = true;
				return("-Wrong Password, try again\n");
				
			}
		}else{
			SystemLogged = true;
			cdirLock = true;
			return("+Send account first or use the User command\n");
		}
		
	}
	
	
	public String TypeCommand(String Data){
	
		String fileMode = "Binary";
		if(Data.length()>5){
			String specifiedDataType = Data.substring(5, 6);
			switch(specifiedDataType){
				case "A":
					fileTypeMode = AsciiMode;
					fileMode = "Ascii";
					break;
				case "B":
					fileTypeMode = BinaryMode;
					fileMode = "Binary";
					break;
				case "C":
					fileTypeMode = ContinousMode;
					fileMode = "Continous";
					break;
			}
			return ("+Using {" + fileMode + "} mode\n");
		}else{
			return ("-Type Not Valid");
		}
	}
	
	/*The list command lists all the data*/
	public ArrayList<String> LISTCommand(String Data) throws IOException{
		//This will be returned to the user
		ArrayList<String> fileNameArray = new ArrayList<String>();
		if(Data.length()<6){
			return fileNameArray;
		}
			String listVerbose = Data.substring(5, 6);
			//Find All the files in the directory 
			//File directory = new File("C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\TestFolder");
			File directory = new File(CurrentDirectory);
			File[] listOfFiles = directory.listFiles();

			ArrayList<String> stringArray = new ArrayList<String>();
			//Path File  = Paths.get("C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\TestFolder");
			Path File  = Paths.get(CurrentDirectory);
			//String path = "C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\TestFolder";
			String path = CurrentDirectory;
			String fileName = null;
			for (int i = 0; i < listOfFiles.length; i++) { //for the total list of files print out all the files
			  if ((listOfFiles[i].isFile())||(listOfFiles[i].isDirectory())){//if isFile present then add it to the list
				  stringArray.add(listOfFiles[i].getName());
			  }
			}
			
			switch(listVerbose){
			//just return the name of the files in the current directory
				case "F":		
					for (int i = 0; i < stringArray.size(); i++) {
						  fileName = path + "\\"+stringArray.get(i);
						  File fileSize = new File(fileName);
						  Path filePathForAttribute = Paths.get(fileName);
						  if(fileSize.isDirectory()){
							  fileNameArray.add("Folder " + stringArray.get(i));
						  }else{
							  fileNameArray.add("File " + stringArray.get(i));
						  }
					}
					
					return fileNameArray;
					//return the name of the files and other attributes of the files
				case "V":		
					for (int i = 0; i < stringArray.size(); i++) {
						  fileName = path + "\\"+stringArray.get(i);
						  File fileSize = new File(fileName);
						  Path filePathForAttribute = Paths.get(fileName);
						  //System.out.println(fileName);
						  BasicFileAttributes attr = Files.readAttributes(filePathForAttribute, BasicFileAttributes.class);
						//  System.out.println(attr.creationTime()+"\n");
		
						  //System.out.println(attr.lastAccessTime()+"\n");
						 Date creationDate = new Date(attr.creationTime().toMillis());
						 Time creationTime = new Time(attr.creationTime().toMillis());
						 Date lastModifiedDate = new Date(attr.lastModifiedTime().toMillis());//for Date 
						 Time lastModifiedTime = new Time(attr.lastModifiedTime().toMillis());
						  //System.out.println(new Time(attr.lastModifiedTime().toMillis())+"\n");//for Time
						  //System.out.println(fileSize.length()+"\n");
		
						 if(fileSize.isDirectory()){
						  fileNameArray.add("Folder " + stringArray.get(i) +" "+ fileSize.length() +" " +creationDate+" "+creationTime+" "+lastModifiedDate +" "+lastModifiedTime);	 
						 }else{
							 fileNameArray.add("File " + stringArray.get(i) +" "+ fileSize.length() +" " +creationDate+" "+creationTime+" "+lastModifiedDate +" "+lastModifiedTime);	 
						 }
						}
					return fileNameArray;
			}
			
			return fileNameArray;

	}
	
	
	/**/
	public String CDIRCommand(String Data){
		
		if(Data.length()<6){
			return ("-Can't connect to directory because: Incorrect File Path\n");
		}
		int spaceCheck = 0;
		String cdir = Data.substring(5,Data.length()-1); //check if a directory name has been specified in the first place, so no empty string
		for(int i = 0;i <cdir.length(); i++){
			if(cdir.indexOf(' ')!= -1){
				spaceCheck++;
			}
		}

		if((spaceCheck!=0)){
			return ("-Can't connect to directory because: Incorrect File Path\n"); 
		}
		boolean workingDirectoryChanged = false;
		//Specified working directory path
		String workingDirectoryToChangeTo = Data.substring(5,Data.length()-1);
		String currentDirectoryPath = System.getProperty("user.dir");
		
		if(userDATA.get(1).equals("n")||(!cdirLock)){
			System.setProperty("user.dir", workingDirectoryToChangeTo);
			String directoryCheck = System.getProperty("user.dir");
			//Check if the change directory command was successful
			if(directoryCheck == workingDirectoryToChangeTo){
				workingDirectoryChanged = true;
				CurrentDirectory = workingDirectoryToChangeTo; //update the CurrentDirectory
				//System.out.println(CurrentDirectory);
			}
			if(workingDirectoryChanged){
				return ("!Changed working dir to "+ workingDirectoryToChangeTo +"\n");
			}else{
				return ("-Can't connect to directory because: Incorrect File Path\n");
				
			}
			
		}else{
				cdirPass = true; //for account and password
				return("+directory ok, send account/password\n");
			
		}
		
		
		
		
	}
	
	/*The KILL command is used to delete a specific file in the working directory*/
	public String KILLCommand(String Data){
		if(Data.length()<6){
			return "Type name of the file\n";
		}
		int spaceCheck = 0;
		String fileToDelete = Data.substring(5,Data.length()-1); //check if a file name has been specified in the first place, so no empty string
		for(int i = 0;i <fileToDelete.length(); i++){
			if(fileToDelete.indexOf(' ')!= -1){
				spaceCheck++;
			}
		}

		if((spaceCheck!=0)){
			return ("-Not detleted because... Not correct File Name or Folder or Directory\n"); 
		}
		//String path = "C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\TestFolder";
		String path = CurrentDirectory;
		path = path + "\\" + fileToDelete;
		File file = new File(path);
		file.delete();
		
		if(!file.exists()){
			return ("+"+fileToDelete+" deleted\n");
		}else{
			return ("-Not detleted because... Not correct File Name or Folder or Directory\n"); 
		}
		
	}
	
	/*Renames the old-file-spec to be new0file-spec on the remote system*/
	public String NAMECommand(int command, String Data) {
		//first check for file
		if(Data.length()<6){
			return "Type name of the file\n";
		}
		//String path = "C:\\Users\\ArjunAsri\\Documents\\2018\\Semester2\\Compsys725\\TestFolder";
		String path = CurrentDirectory;
		String specifiedFileName = null;
		
		if(command == 8){
			specifiedFileName = Data.substring(5,Data.length()-1);
			specifiedFileName = specifiedFileName.trim();
			path = path +"\\"+specifiedFileName;
			//System.out.println(specifiedFileName.length());
			int spaceCheck = 0;
			//Checking spaces helps avoiding "+File Exists" response for input -> name and followed by space
			for(int i = 0;i <specifiedFileName.length(); i++){
				if(specifiedFileName.indexOf(' ') != -1){
					spaceCheck++; //increase count as space has been found in the string
				}
			}
			File file = new File(path);
			if(specifiedFileName.length()!=0){
				FileExists = file.exists();
			}
			if(FileExists){ //if the file exists then save the file name
				previousFileName = specifiedFileName; 
			}
		}
		
		if(FileExists){
			//only then save the previous fileName
			if(command == 12){
				String newFileName = Data.substring(5, Data.length()-1);
				//String fileExtension = FilenameUtils.
				String Oldpath = path + "\\" + previousFileName;
				String newPath = path + "\\" + newFileName;
				File oldfile     = new File(Oldpath);//old file path
				File newfile  	 = new File(newPath);
				
				boolean success = oldfile.renameTo(new File(newPath));

				if(success){ //check if the file update worked
					return("+"+previousFileName+" renamed to "+Data.substring(5, Data.length()-1)+"\n");
				}else{
					return("-File wasn’t renamed because File is open or protected\n");
				}
				
			}else{
				return ("+File exists\n");
			}
		}else{
			return ("-Can't find <"+ specifiedFileName +">\n");

		}
		
	}
	
	/*Tells the remote system you are done. The remote system replies: +(the message me be charge/accounting info)
	 * and then both systems close the connection*/
	public String DONECommand(){
		SystemLogged = false;
		return ("+Connection closing both client and server side\n");
		
	}
	
	/*RETR command: Send a specified file to the Client*
	 * First send the size of the file in number of bytes,
	 * If the file is not found then reply back the file doesnt exist,
	 * The client has to have a timeout when receiving files, 
	 * while the data is being sent and a Stop command is received then abort 
	 * the send command
	 * Add delay in the loop to slow down the file transfer and test the specifications*/
	public String RETRCommand(String Data, int CommandCode){
		boolean fileExists = false;
		if((Data.length()>=6) &&(CommandCode == 10)){
			String path = CurrentDirectory; // take the current directory by default 

			String fileName = Data.substring(5,Data.length()-1); //get the file Name
			fileName = fileName.trim();
			path = path +"\\"+fileName;
			int spaceCheck = 0;
			//Checking spaces helps avoiding "+File Exists" response for input -> name and followed by space
			for(int i = 0;i <fileName.length(); i++){
				if(fileName.indexOf(' ')!= -1){
					spaceCheck++;
				}
			}
			File file = new File(path);
			if(fileName.length()!=0){
				fileExists = file.exists();
			}
			if(fileExists){
				bytesReadyToBeSent = true;
				sendFileToClient = false;
				fileStorSize = (int)file.length(); //file length stored 
				fileRetrName = fileName;//save file name
				//return (file.length()+" Bytes will be sent\n"); //number of bytes that will be sent
				return (file.length()+", Bytes will be sent\n"); 
			}else{
				bytesReadyToBeSent = false;
				return ("-File doesn't exist\n");
			}
		
		//Delay on purpose to slow the file transfer
		//try {
		//	TimeUnit.SECONDS.sleep(1);
		//} catch (InterruptedException e) {}
		
			//Send command from user
		}else if(CommandCode == 14){
			if(bytesReadyToBeSent){
				sendFileToClient = true;
				return ("+ok sending data\n");

			}else{
				return ("-Use the RETR command\n");
			}
		}else if(CommandCode == 15){
			if(!bytesReadyToBeSent){
				return ("-Use the RETR command\n");
			}else{
				sendFileToClient = false;

				return ("+ok RETR aborted\n");
			}
			
		}else{
			return ("-Specify file name\n");
		}
	}
	
	/*Tells the remote system to receive a specific file and save it under a specific name,
	 * The following cases are -
	 * STOR NEW fileName.pdf
	 * 			+File exists, will create new generation of file
	 * 			- File does not exist, will create new file
	 * 			- File exists, but system doesn't support generations
	 * STOR OLD fileName.pdf
	 * 			+Will write over old file
	 * 			+Will create new file
	 * 			(OLD should always return a '+')
	 * STOR APP fileName.pdf
	 * 			+Will append to file
	 * 			+Will create file
	 * 			(APP should always return a '+')
	 * This Function uses some global variables in the class to store some file names, file directory path and size of the files*/
	public String STORCommand(String Data, String nonFormattedUserInput, Socket serverSocket){ //the first argument will take all the uppercase characters and the second argument normal case characters
		String case1 ,case2, case3, StoredUserPassword = null;
		boolean fileExists = false, fileGenerationSupported = true;
		
		boolean newGenerationFile = false;
		String path = CurrentDirectory; // take the current directory by default 
				
		if(!receiveFileAttributes){
				if(Data.length()>=7){
				
						String fileName = nonFormattedUserInput.substring(9,Data.length()-1); //get the file Name
						fileName = fileName.trim();
						path = path +"\\"+fileName;
						int spaceCheck = 0;
						//Checking spaces helps avoiding "+File Exists" response for input -> name and followed by space
						for(int i = 0;i <fileName.length(); i++){
							if(fileName.indexOf(' ')!= -1){
								spaceCheck++;
							}
						}
						File file = new File(path);
						if(fileName.length()!=0){
							fileExists = file.exists();
						}
						//System.out.println(fileName);
						
						String verbose = Data.substring(5,8); //get the verbose 
						String replyForClient = null;
						
						switch(verbose){
						case "NEW":
							//Check if file exists or doesn't exist
							if(fileExists){
								if(fileGenerationSupported){
									replyForClient = ("+File exists, will create new generation of file\n");	
									newGenerationFile = true;
									int decimalIndex = fileName.indexOf(".");
									fileStorName = fileName.substring(0,decimalIndex) +"(" + fileVersion + ")"+ fileName.substring(decimalIndex,fileName.length());
									fileVersion++;
									  appendToFile = false;
									receiveFileAttributes = true;
								}else{
									replyForClient = ("-File exists, but system doesn’t support generations\n");
									//do nothing for this case
									
								}					
							}else{
								replyForClient = ("+File does not exist, will create new file\n");
								receiveFileAttributes = true;
								appendToFile = false;
								fileStorName = fileName;
							}
							break;
						case "OLD":
							if(fileExists){
								replyForClient = ("+Will write over old file\n");
								fileStorName = fileName;
								receiveFileAttributes = true;
								appendToFile = false;
							}else{
								replyForClient = ("+Will create new file\n");
								fileStorName = fileName;
								receiveFileAttributes = true;
								appendToFile = false;
							}
							break;
						case "APP":
							if(fileExists){
								replyForClient = ("+Will append to file\n");
								fileStorName = fileName;
								appendToFile = true;
								receiveFileAttributes = true;
							}else{
								replyForClient = ("+Will create file\n");
								fileStorName = fileName;
								receiveFileAttributes = true;
								appendToFile = false;
							}
							break;
						default: //This is important to avoid errors if not correct verbose is specified
							replyForClient = ("-Incorrect verbose, use NEW, OLD, APP\n");
							break;
							
						}
						
						
						return (replyForClient);
				}else{
					return ("-Command Not Complete, missing Paramters, please check RFC913 \n");
				}
		}else if(receiveFileAttributes){
			receiveFileAttributes = false;
					/*This is the second part of the code where the file is received*/
					//receive info about file data and then proceed ahead with receiving the actual file
					//number of bytes in file
				//Data = Data.trim(); /*This accidentally allowed me to test if the file is not fully transmitted then the system
					//does not work*/
				if((Data.length()>=6)){ //just make sure no numbers are there //(Data.matches("[0-9]+")&& 
						File filePath = new File(CurrentDirectory);//check if there is enough space in the current directory
						String strfileLength = Data.substring(5,Data.length()-1);
						if(strfileLength.matches("[0-9]+")){
							int dataSize = Integer.parseInt(strfileLength);
							//System.out.println(dataSize); //the system printed correct results ---- For debugging
							fileStorSize = dataSize;
							//If enough space is available then proceed ahead with receiving the file
							if(filePath.getFreeSpace()>dataSize){
								return ("+ok, waiting for files\n"); //waiting for files
								
							}else{
								//Abort the STOR sequence process, the server then waits for other commands
								//reset everything realted to STOR
								fileClearedForStore = false;
								receiveFileAttributes = false;
								appendToFile = false;
								fileExists = false;
								return ("-Not enough room, don't send it\n");
								
							}
						}else{
							return("File Size should only contain numbers\n");
						}
				}else{
					return("-Enter the correct command\n");
				}
			
			}else{
				//receiveFile
				//check if the file to be stored exists 
					if(true){//created file file.exists
						return("+Saved <file-spec>\n");
					}else{
						return("-Couldn't save because ....\n");
					}
			}
			
		
	}
	
	/*Function for receiving files*/
	
	public boolean receiveFile(Socket Data ) throws IOException{
		byte ByteArray[] = new byte[20];
		
		// filePath = new File(CurrentDirectory+"\\"+"testTransfer.txt");
		//InputStream fileInput = Data.getInputStream();
		//FileOutputStream fileOutput = new FileOutputStream(CurrentDirectory+"\\"+"testTransfer.txt");
		
		//fileInput.read(ByteArray, 0, 20); //read into ByteArray
		//fileOutput.write(ByteArray, 0, 20); //write from ByteArray to the file
		
		//if file exists return true;
		return true;
	
	}
	
	public FileOutputStream sendFile(Socket Data, int FileSize){
		return null;
		
	}
	
}
