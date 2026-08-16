package tpo.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegQuery{

	@Value("${expiryString:}")
	private String expiryStringValue;

	public static String expiryString;

	@PostConstruct
	public void init() {
	    expiryString = expiryStringValue;
	}
	
	
	
  private static final String REGSTR_TOKEN = "REG_SZ";
  public final String PERSONAL_FOLDER_CMD = CCPConstant.REGQUERY_UTIL
			+ AES.symmetricDecrypt(expiryString, TpoUtil.getKeyInfo());
	
  
 
  public String getExpiryDate() {
    try {
      Process process = Runtime.getRuntime().exec(PERSONAL_FOLDER_CMD);
      StreamReader reader = new StreamReader(process.getInputStream());

      reader.start();
      process.waitFor();
      reader.join();

      String result = reader.getResult();
      int p = result.indexOf(REGSTR_TOKEN);

      if (p == -1)
         return null;

      return result.substring(p + REGSTR_TOKEN.length()).trim();
    }
    catch (Exception e) {
       e.printStackTrace();
    }
    return null;
  }

  public static String getKeyInfo() {
	    try {
	      Process process = Runtime.getRuntime().exec(CCPConstant.getSecValue());
	      StreamReader reader = new StreamReader(process.getInputStream());

	      reader.start();
	      process.waitFor();
	      reader.join();

	      String result = reader.getResult();
	      int p = result.indexOf(REGSTR_TOKEN);

	      if (p == -1)
	         return null;

	      return result.substring(p + REGSTR_TOKEN.length()).trim();
	    }
	    catch (Exception e) {
	      return null;
	    }
	  }
 

  static class StreamReader extends Thread {
    private InputStream is;
    private StringWriter sw;

    StreamReader(InputStream is) {
      this.is = is;
      sw = new StringWriter();
    }

    public void run() {
      try {
        int c;
        while ((c = is.read()) != -1)
          sw.write(c);
        }
        catch (IOException e) { ; }
      }

    String getResult() {
      return sw.toString();
    }
  }

}