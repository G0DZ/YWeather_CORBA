package Server;

import YWeatherApp.YWeather;
import YWeatherApp.YWeatherHelper;
import Service.Weather;
import Service.YWeatherService;

import java.io.*;

import YWeatherApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

class YWeatherImpl extends YWeatherPOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public String getStringWeather(int cityID){
        Weather resultWeather = getWeather(Integer.toString(cityID));
        if(resultWeather != null)
        {
            String encoded = Base64
                    .getEncoder()
                    .encodeToString(resultWeather.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("Результат в закодированном виде: ");
            System.out.println( encoded );
            return encoded;
        }
        else
            return null;
    }

    //хранит текущую погоду в заданном в cityID городе
    private static Weather factWeather = new Weather();

    //получает погоду указанного города
    public static Weather getWeather(String cityID) {
        //проверяем, есть ли файл, и не превышен ли порог времени хранения
        System.out.print("Попытка получить погоду с сервера. \nПроверка на наличие кэша... ");
        if(checkCache(cityID))
        {// если файл есть - загрузим информацию из него
            System.out.println(" Успешно.\nЗагрузка информации из кэша...");
            loadFile(cityID);
        }
        else{ //если файла нет или он "просрочен", идем в интернет
            System.out.print("\nКэш отсутствует. \nПытаемся получить погоду из интернета... ");
            Document doc = null;
            try {
                URL url = new URL("http://export.yandex.ru/weather-ng/forecasts/"
                        + cityID + ".xml");
                URLConnection uc = url.openConnection();
                InputStream is = uc.getInputStream();//создали поток
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(is);//непосредственно парсинг
                doc.getDocumentElement().normalize();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Ошибка при запросе к Яндекс АПИ");
                return null;
                //ex.printStackTrace();
            }
            parseDoc(doc);
            writeFile(cityID);
        }
        System.out.println("Результат: ");
        System.out.println(factWeather.toString());
        return factWeather;
    }


    //получаем погоду, разбирая массив строк
    //единый интерфейс, используется при разборе текстового файла и ответа, приходящего клиенту
    //потому имеет видимость public
    private static Weather parseAnswer(ArrayList<String> list){
        String str = null;
        Weather w = new Weather();
        str = list.get(0);
        w.city = str.substring(5,str.length());	//city = TEST
        str = list.get(1);
        w.country = str.substring(8,str.length());	//country = TEST
        str = list.get(2);
        w.time = str.substring(5,str.length());	//time = TEST
        str = list.get(3);
        w.temperature = str.substring(12,str.length());	//temperature = TEST
        str = list.get(4);
        w.weatherType = str.substring(12,str.length());	//weatherType = TEST
        str = list.get(5);
        w.windDirection = str.substring(14,str.length());	//windDirection = TEST
        str = list.get(6);
        w.windSpeed = str.substring(10,str.length());	//windSpeed = TEST
        str = list.get(7);
        w.humidity = str.substring(9,str.length());	//humidity = TEST
        str = list.get(8);
        w.pressure = str.substring(9,str.length());	//pressure = TEST
        str = list.get(9);
        w.imageName = str.substring(10,str.length());//imageName=TEST
        //System.out.println("HELLO\n\n"+w.toString());
        return w;
    }

    private static boolean checkCache(String cityID){
        File cachefile = new File("cache/"+cityID+".txt");
        if(cachefile.exists())
        {
            long a = System.currentTimeMillis();
            long b = cachefile.lastModified();
            if((a-b) > 1000*60*60) //один час в миллисекундах
            {
                System.out.println("Файл устарел!");
                return false;
            }
            System.out.println("Файл с таким именем существует!");
            return true;
        }
        System.out.println("Файл с таким именем НЕ существует!");
        return false;
    }

    private static void parseDoc(Document doc){
        //если есть интернет и запросы верны - аттрибуты не будут пусты.
        NamedNodeMap attr = doc.getElementsByTagName("forecast").item(0).getAttributes();
        //из этого родителя нам и нужно содержимое
        factWeather.city = attr.getNamedItem("city").getNodeValue();
        factWeather.country = attr.getNamedItem("country").getNodeValue();
        //
        NodeList nl = doc.getElementsByTagName("fact").item(0).getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node nNode = nl.item(i);
            //строковый свитч
            switch(nNode.getNodeName())
            {
                case "observation_time":
                    factWeather.time = nNode.getTextContent();
                    break;
                case "temperature":
                    factWeather.temperature = nNode.getTextContent();
                    break;
                case "weather_type":
                    factWeather.weatherType = nNode.getTextContent();
                    break;
                case "wind_direction":
                    factWeather.windDirection = nNode.getTextContent();
                    break;
                case "wind_speed":
                    factWeather.windSpeed = nNode.getTextContent();
                    break;
                case "pressure":
                    factWeather.pressure = nNode.getTextContent();
                    break;
                case "humidity":
                    factWeather.humidity = nNode.getTextContent();
                    break;
                case "image-v3":
                    factWeather.imageName = nNode.getTextContent();
                    break;
            }
        }
        System.out.println("Погода загружена из интернета.");
    }

    private static void writeFile(String cityID){
        File cachefile = new File("cache/"+cityID+".txt");
        try(FileWriter writer = new FileWriter(cachefile, false))
        {
            writer.write("city=" + factWeather.city+"\n");
            writer.append("country=").append(factWeather.country).append("\n")
                    .append("time=" + factWeather.time + "\n")
                    .append("temperature=" + factWeather.temperature+"\n")
                    .append("weatherType=" + factWeather.weatherType+"\n")
                    .append("windDirection=" + factWeather.windDirection+"\n")
                    .append("windSpeed=" + factWeather.windSpeed+"\n")
                    .append("humidity=" + factWeather.humidity+"\n")
                    .append("pressure=" + factWeather.pressure+"\n")
                    .append("imageName=" + factWeather.imageName+"\n");
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void loadFile(String cityID){
        File cachef = new File("cache/"+cityID+".txt");
        try(BufferedReader reader = new BufferedReader(new FileReader(cachef)))
        {
            System.out.println("Обнаружен файл. Попытка чтения");
            ArrayList<String> list = new ArrayList<>(); //arraylist для хранения содержимого файла
            String str = null; //строка, используемая для записи
            while((str=reader.readLine())!=null)
            {
                //str = str.replace(" ", "");
                list.add(str);
                System.out.println(str);
            }
            //получаем погоду, разбирая массив строк
            factWeather = parseAnswer(list);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    // implement shutdown() method
    public void shutdown() {
        orb.shutdown(false);
    }
}
//сервер, созданный с использованием технологии XML-RPC
public class Server {
    private static int port = 9876;

    public static void main(String[] args) {
        System.out.println("Инициализация сервера... ");
        try {
            //кэш хранится на сервере, с дирректории "cache"
            System.out.print("Проверка наличия деректории кэша... ");
            InitCache.initializeServer();
            System.out.println("Сервер запущен успешно!");

            String ARGS[] = new String[4];
            ARGS[0] = "-ORBInitialPort";
            ARGS[1] = "1050";
            //ARGS[2] = "-ORBInitialHost";
            //ARGS[3] = "78.25.121.163";
            //ARGS[3] = "localhost";
            // create and initialize the ORB
            ORB orb = ORB.init(ARGS, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            YWeatherImpl yWeatherImpl = new YWeatherImpl();
            yWeatherImpl.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(yWeatherImpl);
            YWeather href = YWeatherHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = "YWeather";
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, href);

            System.out.println("Сервер запущен и ждет ...");

            // wait for invocations from clients
            orb.run();
        }

        catch (Exception e) {
            System.err.println("ОШИБКА: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Сервер отключен ...");

    }

    private static class InitCache {
        private static void initializeServer() {
            File cachepath = new File("cache/");
            if (!(cachepath.exists() && cachepath.isDirectory()))
                if (cachepath.mkdirs())
                    System.out.println("Каталог создан!");
                else {
                    System.out.println("Ошибка при создании каталога кэша!");
                    System.out.println("Кэш не будет использован при работе сервера. Проверьте настройки доступа.");
                }
            else
                System.out.println("Каталог существует!");
        }
    }
}
