package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class CityReader {
    public static TreeMap<String, Integer> getCities(){
        File cachef = new File("res/_cities.txt");
        try(BufferedReader reader = new BufferedReader(new FileReader(cachef)))
        {
            System.out.println("Обнаружен файл. Попытка чтения");
            TreeMap<String, Integer> tm = new TreeMap<>(); //HashMap для хранения содержимого файла
            String str = null; //строка, используемая для записи
            while((str=reader.readLine())!=null)
            {
                StringTokenizer stk = new StringTokenizer(str,"="); //логические отрезки разделены запятыми
                int z = stk.countTokens(); //количество разделителей в строке
                Integer intValue = Integer.parseInt(stk.nextToken());
                String keyCity = stk.nextToken();
                tm.put(keyCity, intValue);
            }
            return tm;
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
