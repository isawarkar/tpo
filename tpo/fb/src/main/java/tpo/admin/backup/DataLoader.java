/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.backup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.excel.ExcelSheetReader;
import tpo.beans.Parent;
import tpo.dao.CommonDBBean;
import tpo.email.EmailUtil;
import tpo.hibernate.Registration;
import tpo.util.AES;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("dataLoader")
@Transactional(readOnly = true)
@Scope("request")
public class DataLoader extends Parent {

	@Autowired
	private CommonDBBean commonDBBean;

	@Autowired
	private ExcelSheetReader excelSheetReader;
	
	@Autowired
	private SessionFactory sessionFactory;
	

	private static Logger logger = LoggerFactory.getLogger(DataLoader.class.getName());

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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Boolean loadData() {
		Boolean status = false;
		try {

			EmailUtil emailUtil = getEmailInstance();
			// Set MYSQL variable path
			if (emailUtil != null) {
				String drivaName = commonDBBean.getCommonData("DriveName").get(0);
					Session session = sessionFactory.getCurrentSession();
					String path = drivaName != "home" ? drivaName + TpoUtil.dataLoader : TpoUtil.dataLoader;
					String backupPath = drivaName != "home" ? drivaName + TpoUtil.backupPath : TpoUtil.backupPath;
					Path directory = Paths.get(path);
					File settingFile= new File(path+"setting.txt");
					List<String> fileContentsList=readSettingFile(settingFile);
					DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory);
					if (dirStream != null && fileContentsList !=null && fileContentsList.size() > 0) {
						Iterator<Path> pathI = dirStream.iterator();
						while (pathI.hasNext()) {
							File file = pathI.next().toFile();
							if (file.isFile() && file.getName().endsWith(".sql") && !fileContentsList.contains(".sql")) {
								status = importSqlFile(status, emailUtil, drivaName, file.getAbsolutePath());
								if (status) {
									TpoUtil.moveTheFile(backupPath, file, TpoUtil.getDateToStringYYYYMMdd(new Date()));
								}
							} else if (file.isFile() && file.getName().endsWith(".xls") && !fileContentsList.contains(".xls")) {
								String userName = null;
								if("Y".equalsIgnoreCase(fileContentsList.get(0).split("=")[1])){
									userName =fileContentsList.get(1).split("=")[1];
								}
								
								List<Registration> registrationsList = excelSheetReader
										.readExcelFile(new FileInputStream(file),userName,path,file.getName());
								if (registrationsList != null && registrationsList.size() > 0) {
									for (Registration registration : registrationsList) {
										session.saveOrUpdate(registration);
										session.saveOrUpdate(registration.getPersonalinfo());
										session.saveOrUpdate(registration
												.getPercentageinfo());
										session.saveOrUpdate(registration.getBackdetails());
										session.saveOrUpdate(registration.getContactinfo());
										session.saveOrUpdate(registration.getAchivements());
									}
									TpoUtil.moveTheFile(backupPath, file, null);
								}
							}

						}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());

		}

		return status;
	}

	private List<String> readSettingFile(File settingFile) {
		List<String> fileContent = new ArrayList<String>();
		if(settingFile != null && settingFile.exists()){
			BufferedReader br = null;
			FileReader fr = null;
			try {
				fr = new FileReader(settingFile);
				br = new BufferedReader(fr);
				String sCurrentLine;
				br = new BufferedReader(new FileReader(settingFile));
				while ((sCurrentLine = br.readLine()) != null) {
					fileContent.add(sCurrentLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex) {
					ex.printStackTrace();

				}
			}
		}
		return fileContent;
	}



	

	private Boolean importSqlFile(Boolean status, EmailUtil emailUtil, String drivaName, String fileName)
			throws IOException, InterruptedException, MessagingException {
		Process runtimeProcess;
		String mysql;
		String mysqlPath = commonDBBean.getCommonData("MYSQL").get(0);
		mysql = drivaName != "home" ? "\"" + mysqlPath + "/mysql\"" : "mysql";
		if (("LAN".equals(emailUtil.getEnv()) || "LOCAL".equals(emailUtil.getEnv()))) {
			String databaseName = AES.symmetricDecrypt(jdbc_SchemaName, TpoUtil.getKeyInfo());
			String userName = AES.symmetricDecrypt(jdbc_Username, TpoUtil.getKeyInfo());
			String password = AES.symmetricDecrypt(jdbc_Password, TpoUtil.getKeyInfo());
			runtimeProcess = null;
			String[] executeCmd = new String[] { "" + mysql + "", databaseName, "--user=" + userName,
					"--password=" + password, "-e", " source " + fileName };
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			if (runtimeProcess.waitFor() == 0) {
				status = true;
			} else {
				status = false;
			}
		} else {
			String databaseName = AES.symmetricDecrypt(jdbc_SchemaName, TpoUtil.getKeyInfo());
			String userName = AES.symmetricDecrypt(jdbc_Username, TpoUtil.getKeyInfo());
			String password = AES.symmetricDecrypt(jdbc_Password, TpoUtil.getKeyInfo());
			runtimeProcess = null;
			List<String> address = new ArrayList<String>(1);
			address.add(superUserEmail);
			StringBuffer message = new StringBuffer();
			String[] executeCmd = new String[] { "" + mysql + "", databaseName, "--user=" + userName,
					"--password=" + password, "-e", " source " + fileName + "latestBack.sql" };
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			if (runtimeProcess != null) {

				if (runtimeProcess.waitFor() == 0) {
					if (emailUtil != null) {
						message.append(FbMessageUtil.getLabel("Backup_was_successful_at"));
						message.append(new Date().toString());
						message.append(TpoUtil.getMesageString());
						emailUtil.postMail(address, FbMessageUtil.getLabel("Success_Backup_Email"), message.toString(),
								TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
					}
					status = true;
				} else {
					if (emailUtil != null) {
						message.append(FbMessageUtil.getLabel("Backup_was_not_successful_at"));
						message.append(new Date().toString());
						message.append(loadStream(runtimeProcess.getErrorStream()));
						message.append(TpoUtil.getMesageString());
						emailUtil.postMail(address, FbMessageUtil.getLabel("Error_Backup_Email"), message.toString(),
								TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
					}
					status = false;
				}
			}
		}
		return status;
	}

	public CommonDBBean getCommonDBBean() {
		return commonDBBean;
	}

	public void setCommonDBBean(CommonDBBean commonDBBean) {
		this.commonDBBean = commonDBBean;
	}

	public String readBackupDate(String filePath) {
		String dateAndTime = null;
		filePath = filePath.replaceAll("/", "\\\\");
		try {
			File file = new File(filePath);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			dateAndTime = FbMessageUtil.getLabel("DB_Backup_Date_and_Time") + sdf.format(file.lastModified());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateAndTime;

	}

	public ExcelSheetReader getExcelSheetReader() {
		return excelSheetReader;
	}

	public void setExcelSheetReader(ExcelSheetReader excelSheetReader) {
		this.excelSheetReader = excelSheetReader;
	}

}
