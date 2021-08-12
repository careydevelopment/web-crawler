package us.careydevelopment.util.bot.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class ConnectionHelper {

    private String url;
        
    static {
        try { 
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { 
                public boolean verify(String hostname, SSLSession session) { 
                    return true; 
                }
            }); 
            
            SSLContext context = SSLContext.getInstance("TLS"); 
            context.init(null, new X509TrustManager[]{new X509TrustManager(){

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                                                    throws java.security.cert.CertificateException {}
    
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                                                    throws java.security.cert.CertificateException {}
    
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                            return new java.security.cert.X509Certificate[0];
                } 
            }
            }, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory()); 
        } catch (Exception e) {
            e.printStackTrace(); 
        } 
    }
        
    public ConnectionHelper(String url) {
        this.url = url;
    }
        
    public URLConnection getBasicConnection() throws MalformedURLException, IOException {
        URL urlObj = new URL(url);
        URLConnection conn = urlObj.openConnection();
        conn.setRequestProperty("User-Agent", WebConstants.USER_AGENT);

        return conn;
    }
        
    public HttpsURLConnection getSecureConnection() throws MalformedURLException, IOException {
        URL urlObj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection)urlObj.openConnection();
        con.setRequestProperty("User-Agent", WebConstants.USER_AGENT);
        return con;
    }
}
