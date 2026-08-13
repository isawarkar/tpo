package tpo.datasource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import tpo.util.Encryption;

public class SecureDataSource extends DriverManagerDataSource {

	 @Override 
     public String getPassword() { 
             String password = super.getPassword(); 
             return Encryption.getDecryptPassword(password); 
     } 
	 
	@Override
	public String getUsername() {
		 String userName = super.getUsername();
         return Encryption.getDecryptPassword(userName); 
	}
	
	@Override
	public String getUrl() {
		 String url = super.getUrl();
		 String decUrl = Encryption.getDecryptPassword(url) + "?UseUnicode=true&characterEncoding=utf8";
		 return decUrl; 
	}
	
}
