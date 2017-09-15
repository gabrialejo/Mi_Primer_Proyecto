package ve.com.inma.mobility_4_1.api;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import ve.com.inma.mobility_4_1.json.AutoInmaJson;
import ve.com.inma.mobility_4_1.json.EObjectJson;
import ve.com.inma.mobility_4_1.json.JsonContainer;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.serialization.SoapObject;
import android.widget.Toast;

public class WebService extends Activity {
    //private static final String SOAP_ACTION = "http://inma.com.ve/inmaEst_estimateProcess";
    private static final String SOAP_ACTION = "http://192.168.0.45/inmaEst_estimateProcess";

    private static final String METHOD_NAME = "inmaEst_estimateProcess";

    //private static final String NAMESPACE = "http://inma.com.ve/";
    private static final String NAMESPACE = "http://192.168.0.45/";
    //private static final String URL = "http://192.168.0.74/InmaEstServiceMobile/Service.asmx";
    //private static final String URL = "http://192.168.0.39:1580/InmaEstServiceMobile/service.asmx";
    private static final String URL = "http://192.168.0.45/servicesMercantil/service.asmx";	  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_service);
		String json = getIntent().getExtras().getString("eObject");
		Toast.makeText(this, "*** onCreate( en WEBService ***", Toast.LENGTH_LONG).show();
		call(json);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

    public void call(String eObject)
    {

    	//final TextView textRestult = (TextView) findViewById(R.id.result);
        try {
        	Toast.makeText(this, "** ¡Respuesta! **"+eObject.toString(), Toast.LENGTH_LONG).show();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //request.addProperty("eObject", eObject);
            request.addProperty("FileName", eObject);
            request.addProperty("ToEmail", "jazpurua@inma.com.ve");
            request.addProperty("vNombre", "Jose Azpurua");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);

            Object result = (Object)envelope.getResponse();
            Toast.makeText(this, "** ¡Respuesta! **"+result.toString(), Toast.LENGTH_LONG).show();
            //textRestult.append(result + "\n");
        } catch (Exception e) {
            //textRestult.append(e.toString() + "\n");
        	
            }
    }	
	
}
