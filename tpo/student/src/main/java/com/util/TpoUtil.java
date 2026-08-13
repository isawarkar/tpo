package com.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.core.ApplicationPart;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.beans.UIBackingBean;

import images.R;



@Component("TpoUtil")
public class TpoUtil {

	static Logger logger = LoggerFactory.getLogger(TpoUtil.class);

	public static String ADMIN_EMAIL = "fresherbuddy.yourtruefriend@gmail.com";

	public static String HOSTNAME = "www.fresherbuddy.in";

	public static final String backupPath = FbResourceUtil.getLabel("backupPath");

	public static final String dataLoader = FbResourceUtil.getLabel("dataLoader");

	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss a";
	
	public static final String SMS_FLAG_FB = SmsUtil.getLabel("enableSMSApiForFB");
	

	// 500 kb
	public static final Integer IMAGE_SIZE = 525000;
	
	public static String key = null;

	public static List<String> imageTypes = new ArrayList<String>();

	static {

		imageTypes.add("image/bmp");
		imageTypes.add("image/pjpeg");
		imageTypes.add("image/jpeg");
		imageTypes.add("image/x-png");
		imageTypes.add("image/png");
		imageTypes.add("image/gif");
	}

	public static String getDateToString(Date date) {
		DateFormat formatter;
		formatter = new SimpleDateFormat(DATE_FORMAT);
		String s = formatter.format(date);
		return s;
	}

	public static String getDateToStringYYYYMMdd(Date date) {
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		String s = formatter.format(date);
		return s;
	}

	public static String getDateToStringYYYYMMddHHMMss(Date date) {
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = formatter.format(date);
		return s;
	}

	public static String getDateToStringYYYYMMddHHmmss(Date date) {
		DateFormat formatter;
		formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String s = formatter.format(date);
		return s;
	}

	public static String getDateToStringInddmmyyyy(Date date) {
		String s = null;
		if (date != null) {
			DateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			s = formatter.format(date);
		}
		return s;
	}

	public static String getDateToStringInddmmyyyyHHmmSS(Date date) {
		String s = null;
		if (date != null) {
			DateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
			s = formatter.format(date);
		}
		return s;
	}

	/**
	 * Gives the current instance of the managed bean with the supplied name.
	 * 
	 * 
	 * @param class.getSimpleName() String
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public static Object getManagedBean(String beanName) {

		Object bean = null;

		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			if (ctx != null) {
				bean = ctx.getApplication().getVariableResolver().resolveVariable(ctx, beanName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return bean;
	}

	
	/**
	 * Gives the current instance of the managed bean with the supplied name.
	 * 
	 * 
	 * @param class.getSimpleName() String
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	public static Object getManagedBean(String beanName, FacesContext ctx) {

		Object bean = null;

		try {
			if (ctx != null) {
				bean = ctx.getApplication().getVariableResolver().resolveVariable(ctx, beanName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return bean;
	}

	public static Integer getRandomNumber() {
		Random ra = new Random();
		Integer rendonNumber = ra.nextInt(100000000);
		while (rendonNumber <= 10000000) {
			rendonNumber = ra.nextInt(100000000);
		}
		return Math.abs(rendonNumber);
	}

	public static Integer get6DigitRandomNumber() {
		Random ra = new Random();
		Integer rendonNumber = ra.nextInt(1000000);
		while (rendonNumber <= 100000) {
			rendonNumber = ra.nextInt(1000000);
		}
		return Math.abs(rendonNumber);
	}

	public static HttpSession getHttpSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		return request.getSession();

	}

	public static String getBasePath(HttpServletRequest request) {
		if (request == null) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		}

		StringBuffer basePath = new StringBuffer();
		if (request.getServerPort() == 80) {
			basePath.append(request.getScheme()).append("s://").append(request.getServerName())
					.append(request.getContextPath()).append("/");
		} else {
			basePath.append(request.getScheme()).append("s://").append(request.getServerName()).append(":")
					.append(request.getServerPort()).append(request.getContextPath()).append("/");
		}

		return basePath.toString();
	}

	public static String getDateToString(String format, Date date) {
		DateFormat formatter;
		formatter = new SimpleDateFormat(format);
		String s = formatter.format(date);
		return s;
	}

	public static FacesContext getFacesContext() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx;
	}

	public static HttpServletRequest getRequest() {
		FacesContext ctx = getFacesContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		return request;
	}

	public static HttpServletResponse getRespose() {
		FacesContext ctx = getFacesContext();
		HttpServletResponse request = (HttpServletResponse) ctx.getExternalContext().getResponse();
		return request;
	}

	public static HttpSession getSession() {
		HttpServletRequest request = getRequest();
		return request.getSession();
	}

	public static List<Integer> getRandomNumbers(int length, int startNumber, int endNumber) {
		Random random = new Random();
		List<Integer> questionList = new ArrayList<Integer>(length);
		Integer number = null;
		while (questionList.size() < length) {
			number = new Integer(random.nextInt(endNumber));
			if (number >= 1)
				if (!questionList.contains(number)) {
					questionList.add(number);
				}
		}
		return questionList;
	}

	/**
	 * Gives the current instance of the managed bean with the supplied name.
	 * 
	 * 
	 * @param class.getSimpleName() String
	 * @return Object
	 */
	public static void replaceManagedBean(String beanName, Object object) {

		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			if (ctx != null) {
				ctx.getExternalContext().getSessionMap().put(beanName, object);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static StringBuffer getMesageString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(FbMessageUtil.getLabel("regards"));
		buffer.append("<br>" + FbMessageUtil.getLabel("Please_visit") + " http://" + TpoUtil.HOSTNAME);
		buffer.append("<br><font color=red size=3>" + FbMessageUtil.getLabel("Please_do_not_reply") + "</font>");
		return buffer;
	}

	public static Date getFormatedDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(date);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getFormatedDate(Date date) {

		String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(strDate);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getFormatedDateInddMMyyyy(Date date) {
		String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(strDate);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getFormatedDateInddMMyyyyHHMM(Date date) {
		String strDate = TpoUtil.getDateToStringYYYYMMddHHmmss(date);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(strDate);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getFormatedDateInyyyyMMddHHMMss(Date date) {
		String strDate = TpoUtil.getDateToStringYYYYMMddHHMMss(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(strDate);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getFormatedDateInddMMyyyy(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(date);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getFormatedDateInMMddyyyy(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(date);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return formatedDate;
	}

	public static Date getWeekStartDate() {
		Calendar cal = Calendar.getInstance();
		// "calculate" the start date of the week
		Calendar first = (Calendar) cal.clone();
		first.add(Calendar.DAY_OF_WEEK, first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
		return first.getTime();
	}

	public static Date getWeekEndDate() {
		Calendar cal = Calendar.getInstance();
		// "calculate" the start date of the week
		Calendar first = (Calendar) cal.clone();
		first.add(Calendar.DAY_OF_WEEK, first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));

		// and add six days to the end date
		Calendar last = (Calendar) first.clone();
		last.add(Calendar.DAY_OF_YEAR, 6);
		return last.getTime();
	}

	public static void readBackDate() {
		try {
			Process proc = Runtime.getRuntime()
					.exec("cmd /c dir G:\\home\\indwaar\\xml-config\\tpo\\backup\\latestBack.sql /tc");

			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String data = "";

			// it's quite stupid but work
			for (int i = 0; i < 6; i++) {
				data = br.readLine();
			}

			// split by space
			StringTokenizer st = new StringTokenizer(data);
			String date = st.nextToken();// Get date
			String time = st.nextToken();// Get time
			String dateAndTime = "Creation Date and Time : " + date + " " + time;
			System.out.println(dateAndTime);

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public static SessionFactory createSessionFactory() {
		Configuration configuration = new Configuration();
		configuration.configure("");
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}

	public static boolean isApplicationExpired() {
		/*
		 * String expiryDate = RegQuery.getExpiryDate(); if (expiryDate == null ||
		 * "".equals(expiryDate)) {
		 * UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("licence_expired"));
		 * return true; } else { Date date =
		 * TpoUtil.getFormatedDateInMMddyyyy(expiryDate); if (date != null) { if (new
		 * Date().after(date)) {
		 * UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("licence_expired"));
		 * return true; } } }
		 */
		return false;
	}

	public static String geyKeyInfo() {
		if (key == null || key.isEmpty()) {
			try {
				key = SystemUtil.getLabel("key");
			} catch (MissingResourceException e) {
				key = "";
			}
			if (key.isEmpty()) {
				key = RegQuery.getKeyInfo();
			}
		}
		return key;
	}

	public static void moveTheFile(String backupPath, File file, String date) {
		String newFileName = null;
		File newFile = null;
		try {
			if (date != null) {
				newFileName = backupPath + file.getName() + "_" + date;
			} else {
				newFileName = backupPath + file.getName();
			}
			newFile = new File(newFileName);
			if (newFile.exists()) {
				newFile.delete();
			}
			Thread.sleep(5000);
			if (!file.renameTo(new File(newFileName))) {
				logger.debug("###################Problem to move the file#############################" + newFileName);
			}
		} catch (InterruptedException e) {

			e.printStackTrace();
		} finally {
			file = null;
			newFile = null;
		}
	}

	
	

	public static String getComaSeprateValue(List<String> list) {
		StringBuffer strValue = null;
		if (list != null && list.size() > 0) {
			for (String str : list) {
				if (strValue == null) {
					strValue = new StringBuffer();
					strValue.append("'").append(str).append("'");
				} else {
					strValue.append(",").append("'").append(str).append("'");
				}
			}
			return strValue.toString();
		}
		return "";
	}

	public static String getComaSeprateValue(String[] list) {
		StringBuffer strValue = null;
		if (list != null && list.length > 0) {
			for (String str : list) {
				if (strValue == null) {
					strValue = new StringBuffer();
					strValue.append("'").append(str).append("'");
				} else {
					strValue.append(",").append("'").append(str).append("'");
				}
			}
			return strValue.toString();
		}
		return "";
	}

	public static String getComaSeprateValueWithOutQuotation(List<String> list) {
		StringBuffer strValue = null;
		if (list != null && list.size() > 0) {
			for (String str : list) {
				if (strValue == null) {
					strValue = new StringBuffer();
					strValue.append(str);
				} else {
					strValue.append(",").append(str);
				}
			}
			return strValue.toString();
		}
		return "";
	}

	public static String getComaSeprateValueWithOutQuotationInteger(List<Integer> list) {
		StringBuffer strValue = null;
		if (list != null && list.size() > 0) {
			for (Integer inte : list) {
				if (strValue == null) {
					strValue = new StringBuffer();
					strValue.append(inte);
				} else {
					strValue.append(",").append(inte);
				}
			}
			return strValue.toString();
		}
		return "";
	}

	public static void renderPDFFile(byte[] bytes, String fileNameWithOutExt) {
		InputStream in = null;
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		try {
			if (bytes.length != 0) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				servletOutputStream = response.getOutputStream();
				response.setContentType("application/pdf");
				if (fileNameWithOutExt != null && fileNameWithOutExt.contains(".pdf")) {
					response.setHeader("Content-Disposition", "attachment; filename=" + fileNameWithOutExt);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=" + fileNameWithOutExt + ".pdf");
				}
				servletOutputStream.write(bytes);
				facesContext.responseComplete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					servletOutputStream.flush();
					servletOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void renderWordFile(byte[] bytes, String fileNameWithOutExt) {
		InputStream in = null;
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		try {
			if (bytes.length != 0) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				servletOutputStream = response.getOutputStream();
				response.setContentType("application/msword");
				if (fileNameWithOutExt != null
						&& (fileNameWithOutExt.contains(".docx") || fileNameWithOutExt.contains(".doc"))) {
					response.setHeader("Content-Disposition", "attachment; filename=" + fileNameWithOutExt);
				} else {
					response.setHeader("Content-Disposition", "attachment; filename=" + fileNameWithOutExt + ".docx");
				}

				servletOutputStream.write(bytes);
				facesContext.responseComplete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					servletOutputStream.flush();
					servletOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void renderEXcelFile(byte[] bytes, String fileNameWithOutExt) {
		InputStream in = null;
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		try {
			if (bytes.length != 0) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				servletOutputStream = response.getOutputStream();
				response.setContentType("application/xls");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileNameWithOutExt + ".xls");

				servletOutputStream.write(bytes);
				facesContext.responseComplete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					servletOutputStream.flush();
					servletOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isUnix() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public static String getImageExt(String contentType) {
		String iType = null;
		if ("image/bmp".equalsIgnoreCase(contentType))
			iType = ".bmp";
		else if ("image/pjpeg".equalsIgnoreCase(contentType))
			iType = ".jpeg";
		else if ("image/jpeg".equalsIgnoreCase(contentType))
			iType = ".jpeg";
		else if ("image/x-png".equalsIgnoreCase(contentType))
			iType = ".png";
		else if ("image/png".equalsIgnoreCase(contentType))
			iType = ".png";
		else if ("image/gif".equalsIgnoreCase(contentType))
			iType = ".gif";
		return iType;
	}

	public static String getFileExt(String contentType) {
		String iType = null;
		if ("application/pdf".equalsIgnoreCase(contentType))
			iType = "pdf";
		else if ("application/msword".equalsIgnoreCase(contentType))
			iType = "doc";

		return iType;
	}
	
	public static byte[] convertInputStreamToBytesArray(InputStream inputStream) {
		ByteArrayOutputStream arrayOutputStream = null;
		byte[] buf = null;
		try {
			if (inputStream != null) {
				int length = inputStream.available();
				buf = new byte[length];
				arrayOutputStream = new ByteArrayOutputStream();
				int bytesRead;
				do {
					bytesRead = inputStream.read(buf);
					if (bytesRead != -1)
						arrayOutputStream.write(buf, 0, bytesRead);
				} while (bytesRead != -1);
				arrayOutputStream.close();
				return arrayOutputStream.toByteArray();
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			buf = null;
			arrayOutputStream = null;
		}
		return null;
	}
	
	public static byte[] getFBFileLogo() {
		try {
			String fileName = R.class.getResource("FreshersBuddy.jpg").getFile();
			fileName = fileName.replaceAll("%20", " ");
			File file = new File(fileName);
			if (file != null) {
				return Files.readAllBytes(file.toPath());
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	public static void sendTextSmsThroughMsg91(String number, String message)
			throws MalformedURLException, IOException {

		// Prepare Url
		URLConnection myURLConnection = null;
		URL myURL = null;
		BufferedReader reader = null;

		// encoding message
		String encoded_message = URLEncoder.encode(message, "UTF-8");

		// Prepare parameter string
		StringBuilder sbPostData = new StringBuilder(SmsUtil.getLabel("msg91MainUrl"));
		sbPostData.append(SmsUtil.getLabel("msg91param1Name") + "=" + SmsUtil.getLabel("msg91param1Value"));
		sbPostData.append("&mobiles=" + number);
		sbPostData.append("&message=" + encoded_message);
		sbPostData.append("&").append(SmsUtil.getLabel("msg91param2Name")).append("=")
				.append(SmsUtil.getLabel("msg91param2Value"));
		sbPostData.append("&").append(SmsUtil.getLabel("msg91param3Name")).append("=")
				.append(SmsUtil.getLabel("msg91param3Value"));

		try {
			// prepare connection
			myURL = new URL(sbPostData.toString());
			myURLConnection = myURL.openConnection();
			myURLConnection.connect();
			reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			// reading response
			String response;
			while ((response = reader.readLine()) != null)
				// print response
				System.out.println(response);
			// finally close connection
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
	}

	public static void sendTextSmsThroughBulkSmsGateway(String number, String message)
			throws MalformedURLException, IOException {
		BufferedReader rd = null;
		OutputStreamWriter wr = null;
		try {
			StringBuilder sbPostData = new StringBuilder();
			// Construct data
			sbPostData.append(SmsUtil.getLabel("bulkSmsParam1Name")).append("=")
					.append(URLEncoder.encode(SmsUtil.getLabel("bulkSmsParam1Value"), "UTF-8"));
			sbPostData.append("&").append(SmsUtil.getLabel("bulkSmsParam2Name")).append("=").append(URLEncoder.encode(
					AES.symmetricDecrypt(SmsUtil.getLabel("bulkSmsParam2Value"), TpoUtil.geyKeyInfo()), "UTF-8"));
			sbPostData.append("&message=").append(URLEncoder.encode(message, "UTF-8"));
			sbPostData.append("&").append(SmsUtil.getLabel("bulkSmsParam3Name")).append("=")
					.append(URLEncoder.encode(SmsUtil.getLabel("bulkSmsParam3Value"), "UTF-8"));
			sbPostData.append("&mobile=").append(URLEncoder.encode(number, "UTF-8"));
			sbPostData.append("&").append(SmsUtil.getLabel("bulkSmsParam4Name")).append("=")
					.append(URLEncoder.encode(SmsUtil.getLabel("bulkSmsParam4Value"), "UTF-8"));

			URL url = new URL(SmsUtil.getLabel("bulkSmsMainUrl") + sbPostData.toString());
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(sbPostData.toString());
			wr.flush();
			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			String sResult1 = "";
			while ((line = rd.readLine()) != null) {
				// Process line...
				sResult1 = sResult1 + line + " ";
			}
			wr.close();
			rd.close();
		} catch (Exception e) {
			System.out.println("Error SMS " + e);
		} finally {
			if (wr != null)
				wr.close();
			if (rd != null)
				rd.close();
		}
	}
	
	public static FileInputStream getNAFile() {
		try {
			String fileName = R.class.getResource("NA.jpg").getFile();
			fileName = fileName.replaceAll("%20", " ");
			File na = new File(fileName);
			if (na != null)
				return new FileInputStream(na);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] getNABytes() {
		try {
			String fileName = R.class.getResource("NA.jpg").getFile();
			fileName = fileName.replaceAll("%20", " ");
			File na = new File(fileName);
			if (na != null)
				return Files.readAllBytes(na.toPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Boolean doImageUploadValidation(ApplicationPart file) {
		Boolean status = true;
		try {
			if (file != null) {
				if (file.getSize() > 0 && file.getSize() < IMAGE_SIZE) {
					if (TpoUtil.imageTypes.contains(file.getContentType())) {
						status = true;
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Only_Image_Type_can_be_uploaded"));
						status = false;
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_small_size_image_Photo"));
					status = false;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}
	
	public static Boolean doUploadResumeValidation(ApplicationPart file) {
		Boolean status = true;
		try {
			if (file != null) {
				if (file.getSize() < 224000) {
					if ("application/msword".equals(file.getContentType())
							|| "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
									.equals(file.getContentType())
							|| "application/pdf".equals(file.getContentType())) {
						status = true;
					} else {
						UIBackingBean
								.setErrorMessage(FbMessageUtil.getLabel("Only_word_and_pdf_document_can_be_uploaded"));
						status = false;
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_smaller_size_resume"));
					status = false;
				}
			} else {
				status = false;

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}
}
