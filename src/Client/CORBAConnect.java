package Client;


import YWeatherApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CORBAConnect implements Runnable {
    static String serverIP = "127.0.0.1";
    static int serverPort = 9876;
    static int cityID = 0;
    private ClientFrameEngine parent;
    private Thread t;

    static YWeather yWeatherImpl;
    static String ARGS[] = new String[4];

    public CORBAConnect(String cityID, ClientFrameEngine parent) {
        this.cityID = Integer.parseInt(cityID);
        this.parent = parent;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        System.out.println("Инициализация клиента... ");
        try {

            // create and initialize the ORB
            ORB orb = ORB.init(CORBAConnect.ARGS, null);

            // get the root naming context
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "YWeather";
            yWeatherImpl = YWeatherHelper.narrow(ncRef.resolve_str(name));

            System.out.println("Obtained a handle on server object: " + yWeatherImpl);
            //System.out.println(helloImpl.sayHello());

            String result = yWeatherImpl.getStringWeather(cityID);

            //yWeatherImpl.shutdown();

            /*XmlRpcClient service = new XmlRpcClient(serverIP, serverPort);

            Vector params = new Vector();
            params.addElement(cityID);
            Object res = service.execute("YWeather.getStringWeather", params);
            String result = (String) res;*/
            //System.out.println(result);
            // Разбор ответа с сервера

            String decoded = new String(
                    Base64.getDecoder().decode( result ),
                    StandardCharsets.UTF_8 );
            System.out.println( decoded );
            parent.analyzeAnswer(decoded);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}