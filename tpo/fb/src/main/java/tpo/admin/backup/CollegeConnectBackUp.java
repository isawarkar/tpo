/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openfaces.component.command.CommandLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tpo.beans.Parent;
import tpo.beans.UIBackingBean;
import tpo.dao.CommonDBBean;
import tpo.email.EmailUtil;
import tpo.util.AES;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Component("collegeConnectBackUp")
public class CollegeConnectBackUp extends Parent {
	
	@Value("${hostname:}")
	private String hostnameValue;

	public static String hostname;

	@PostConstruct
	public void init() {
	    hostname = hostnameValue;
	}
	
	public static final String unixBackupPath = "/DB_Backup/";
	
	@Autowired
	private CommonDBBean commonDBBean;

	private String dateAndTime;
	private List<String> fileList;

	private static Logger logger = LoggerFactory.getLogger(CollegeConnectBackUp.class.getName());

	/**
	 * 
	 * @param in
	 * @return String
	 * @throws IOException
	 */
	private static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		in = new BufferedInputStream(in);
		StringBuffer buffer = new StringBuffer();
		while ((ptr = in.read()) != -1) {
			buffer.append((char) ptr);
		}
		return buffer.toString();
	}

	public Boolean doBackup(String flag) {
		Boolean status = false;
		try {

			Process runtimeProcess;
			EmailUtil emailUtil = getEmailInstance();
			String mysql;
			String sqldum;
			// Set MYSQL variable path
			if (emailUtil != null) {
				String dName = commonDBBean.getCommonData("DriveName").get(0);
				String path = null;
				if (TpoUtil.isUnix()) {
					path = unixBackupPath;
					sqldum = "mysqldump";
					mysql = "mysql";
				} else {
					path = dName != "home" ? dName + backupPath : backupPath;
					String mysqlPath = commonDBBean.getCommonData("MYSQL").get(0);
					sqldum = dName != "home" ? "\"" + mysqlPath + "/mysqldump\"" : "mysqldump";
					mysql = dName != "home" ? "\"" + mysqlPath + "/mysql\"" : "mysql";

				}
				if ("b".equals(flag)) {
					dateAndTime = null;
				}
				String databaseName = AES.symmetricDecrypt(jdbc_SchemaName,
						TpoUtil.getKeyInfo());
				String userName = AES.symmetricDecrypt(jdbc_Username, TpoUtil.getKeyInfo());
				String password = AES.symmetricDecrypt(jdbc_Password, TpoUtil.getKeyInfo());
				String bacupkFileName = TpoUtil.getDateToStringYYYYMMdd(Calendar.getInstance().getTime())
						+ "-latestFB.sql";
				runtimeProcess = doBackupNow(bacupkFileName, flag, databaseName, userName, password, mysql, sqldum, path);
				if (runtimeProcess.waitFor() == 0) {
					sendBackupEmail(emailUtil, FbMessageUtil.getLabel("Backup_was_successful_at"),
							FbMessageUtil.getLabel("Success_Backup_Email"),"<br>Folder Location is "+path+ "<br><br>Latest file is "+bacupkFileName);
					if (TpoUtil.isUnix()) {
					String s3CopyCommand = "aws s3 cp "+unixBackupPath + "" + bacupkFileName+"  s3://"+bucketName;
					Process runtimeProcess1= Runtime.getRuntime().exec(s3CopyCommand);
					if (runtimeProcess1.waitFor() == 0) {
						logger.error("S3 Command############ Successfully" +s3CopyCommand);
						logger.error("Successfully");
					}else {
						logger.error("S3 Command############ Failed" +s3CopyCommand);
					}
					}
					status = true;
				} else {
					String message = loadStream(runtimeProcess.getErrorStream());
					logger.error("Error::"+message);
					sendBackupEmail(emailUtil, FbMessageUtil.getLabel("Backup_was_not_successful_at"),
							FbMessageUtil.getLabel("Error_Backup_Email"),"<br>Error::"+message);
					status = false;
				}
			}
			// deleteJarFile();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());

		}

		return status;
	}

	
	private void sendBackupEmail(EmailUtil emailUtil, String body, String subject,String m) throws MessagingException {
		StringBuffer message = new StringBuffer();
		List<String> address = new ArrayList<String>(1);
		address.add(superUserEmail);
		message.append(body);
		message.append(new Date().toString());
		message.append(m);
		message.append(TpoUtil.getMesageString());
		emailUtil.postMail(address, subject, message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
	}

	private static Process doBackupNow(String bacupkFileName,String flag, String databaseName, String userName, String password, String mysql,
			String sqldum, String path) throws IOException, Exception {
		Process runtimeProcess;
		String cmd;
		if (flag.equals("b")) {
			
			if (TpoUtil.isUnix()) {
				String host = AES.symmetricDecrypt(hostname, TpoUtil.getKeyInfo());
				cmd = "" + sqldum + " -h " + host + " -u " + userName + " -p" + password + " " + databaseName + " -r "
						+ unixBackupPath + "" + bacupkFileName + " --set-gtid-purged=off";
			} else {
				cmd = "" + sqldum + " -u " + userName + " -p" + password + " --add-drop-database " + databaseName
						+ " -r " + path + "" + bacupkFileName;
			}
			runtimeProcess = Runtime.getRuntime().exec(cmd);
			logger.error("Command############" +cmd);
		} else {
			String[] executeCmd = new String[] { "" + mysql + "", databaseName, "--user=" + userName,
					"--password=" + password, "-e", " source " + path + "latestBack.sql" };
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			logger.error("Command############"+executeCmd);
		}
		return runtimeProcess;
	}

	public boolean doBackup() {

		Boolean status = false;
		try {
			status = doBackup("b");
			//checkHeapSize();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return status;
	}

	public void sendFeePayReminder() {
		if (commonDBBean != null) {
			commonDBBean.sendFeeReminder();
			commonDBBean.freeSessionData();
		}
	}
	
	public void checkUserLicence() {
		if (commonDBBean != null) {
			commonDBBean.checkUserLicence();
		}
	}
	
	public void sendBirthDayEmail() {
		if (commonDBBean != null) {
			commonDBBean.sendBirthDayEmail();
		}
	}
	
	

	public void checkHeapSize() {
		try {
			long heapMaxSize = (Runtime.getRuntime().maxMemory() / 1024) / 1024;
			long heapFreeSize = (Runtime.getRuntime().freeMemory() / 1024) / 1024;
			StringBuffer message = new StringBuffer();
			message.append(FbMessageUtil.getLabel("Max_Heap") + heapMaxSize + "MB<br>")
					.append(FbMessageUtil.getLabel("Free_Heap") + heapFreeSize + FbMessageUtil.getLabel("MB"));
			EmailUtil emailUtill = getEmailInstance();
			if (heapFreeSize < 5) {
				Runtime.getRuntime().gc();
				heapMaxSize = (Runtime.getRuntime().maxMemory() / 1024) / 1024;
				heapFreeSize = (Runtime.getRuntime().freeMemory() / 1024) / 1024;
				message.append(FbMessageUtil.getLabel("After_GC_Max_Heap") + heapMaxSize
						+ FbMessageUtil.getLabel("MB").concat("<br>"))
						.append(FbMessageUtil.getLabel("After_GC_Free_Heap") + heapFreeSize
								+ FbMessageUtil.getLabel("MB"));
				if (emailUtill != null) {
					List<String> address = new ArrayList<String>(1);
					address.add(superUserEmail);
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(address, FbMessageUtil.getLabel("Heap_size_alert"), message.toString(),
							TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
				}
			}

		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public CommonDBBean getCommonDBBean() {
		return commonDBBean;
	}

	public void setCommonDBBean(CommonDBBean commonDBBean) {
		this.commonDBBean = commonDBBean;
	}

	public String readBackupDate(String filePath) {
		String dateAndTime = null;
		try {
			File file = getTheNewestFile(filePath, "sql");
			if(file != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");	
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			dateAndTime = FbMessageUtil.getLabel("DB_Backup_Date_and_Time") + sdf.format(file.lastModified());
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return dateAndTime;

	}

	public String getDateAndTime() {

	if(dateAndTime == null) {
			String dname = commonDBBean.getCommonData("DriveName").get(0);
			String path = null;
			if (TpoUtil.isUnix()) {
				path = unixBackupPath;
			}else {
				path = dname != "home" ? dname +  backupPath : backupPath;
				path = path.replaceAll("/", "\\\\");
				
			}
			dateAndTime = readBackupDate(path);
			if(dateAndTime == null || "".equals(dateAndTime)) {
				fileList = null;
			}
	}
		return dateAndTime;
	}
	public List<String> getFileList() {
		return fileList;
	}
	
	/* Get the newest file for a specific extension */
	public File getTheNewestFile(String filePath, String ext) {
	    File theNewestFile = null;
	    File dir = new File(filePath);
	    FileFilter fileFilter = new WildcardFileFilter("*." + ext);
	    File[] files = dir.listFiles(fileFilter);

	    if (files !=null && files.length > 0) {
	    	fileList = new ArrayList<String>(files.length);
	        /** The newest file comes first **/
	        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	        theNewestFile = files[0];
	        for(File file : files) {
	        	fileList.add(file.getName());
	        }
	        files = null;
	    }
	    return theNewestFile;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
	
	public void deleteFile(ActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					String fileName = (String) parameter.getValue();
					String path = null;
					if (TpoUtil.isUnix()) {
						path = unixBackupPath;
						path = "rm "  + path + fileName;
					} else {
						String dName = commonDBBean.getCommonData("DriveName").get(0);
						path = dName != "home" ? dName + backupPath : backupPath;
						path = "del "  + "\"" + path + fileName + "\"";
						path = "cmd /c " + path.replace("/", "\\") + " /f /s /q";
					}
					
					Process runtimeProcess = Runtime.getRuntime().exec(path);
					if (runtimeProcess.waitFor() == 0) {
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
						dateAndTime = null;
					}else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error_Please_try_after_some_time"));
						logger.error("+++++++++++++++++++++" + path);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("+++++++Errror++++++++++++++" + e);
			e.printStackTrace();
		}
	}
	
	public void copyFile(ActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					String fileName = (String) parameter.getValue();
					String path = null;
					if (TpoUtil.isUnix()) {
						path = unixBackupPath;
						path = "aws s3 cp "+ path + fileName+" s3://"+bucketName+"";
					} else {
						String dName = commonDBBean.getCommonData("DriveName").get(0);
						path = dName != "home" ? dName + backupPath : backupPath;
						path = "aws s3 cp "+ path + fileName;
						path = path.replace("/", "\\") + " s3://"+bucketName+"";
					}
					
					Process runtimeProcess = Runtime.getRuntime().exec(path);
					if (runtimeProcess.waitFor() == 0) {
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("copyS3Message"));
						dateAndTime = null;
					}else {
						logger.error("+++++++++++++++++++++" + path);
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error_Please_try_after_some_time"));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("+++++++Errror++++++++++++++" + e);
			e.printStackTrace();
		}
	}

}
