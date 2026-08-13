package tpo.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class RegQuery {

 
  private static final String REGSTR_TOKEN = "REG_SZ";
  private static final String REGDWORD_TOKEN = "REG_DWORD";
  public static final String PERSONAL_FOLDER_CMD = CCPConstant.REGQUERY_UTIL
			+ AES.symmetricDecrypt(SystemUtil.getLabel("expiryString"), TpoUtil.geyKeyInfo());
	
  
 
  public static String getExpiryDate() {
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