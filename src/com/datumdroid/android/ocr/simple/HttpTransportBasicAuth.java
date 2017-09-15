package com.datumdroid.android.ocr.simple;

import java.io.IOException;
import java.net.Proxy;
import org.kobjects.base64.Base64;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
//class to handle authentication in a proxy based environment

public class HttpTransportBasicAuth extends HttpTransportSE {
//username and password for accessing a proxy based network
        private String username;
        private String password;
  public HttpTransportBasicAuth(String url, String username, String password) {
    super(url);
    this.username = username;
    this.password = password;
  }

  @Override
  public ServiceConnection getServiceConnection() throws IOException {
    ServiceConnection serviceConnection = super.getServiceConnection();
    addBasicAuthentication(serviceConnection);
    return serviceConnection;
  }

  protected void addBasicAuthentication(ServiceConnection serviceConnection) 
      throws IOException {

    if (username != null && password != null) {
  	  String proxy = "192.168.0.254:8080";
      StringBuffer buffer = new StringBuffer(username);
      buffer.append(':').append(password);
  	  buffer.append(':').append(proxy);
      byte[] bytes = buffer.toString().getBytes();
      buffer.setLength(0);
      buffer.append("Basic ");
      Base64.encode(bytes, 0, bytes.length, buffer);
      serviceConnection.setRequestProperty
        ("Authorization", buffer.toString());
    }
  }
}
