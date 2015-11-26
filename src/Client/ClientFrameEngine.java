package Client;

import Service.Weather;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientFrameEngine implements ActionListener{
    private ClientFrame parent;
    private String cityID;

    public ClientFrameEngine(ClientFrame parent) {
        this.parent = parent;
    }
    @Override
    public void actionPerformed(ActionEvent arg0) {
        JButton clickedButton =  (JButton) arg0.getSource();
        // Если это кнопка "Ввод исходных данных"
        String actioncommand = clickedButton.getActionCommand();
        if (actioncommand.equals(ClientFrame.button_start.getText())){
            String city = parent.comboBox.getSelectedItem().toString();
            if (city != null) {
                cityID = parent.A.get(city).toString();
                if (cityID != null && !cityID.equals("")) {
                    synchronized (new CORBAConnect(cityID, this)) {
                    } //синхронизированный поток для расчета информации
                }
                else{
                    JOptionPane.showMessageDialog(null, "Город не найден!\nПовторите ввод.");
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Строка ввода города пуста!\nПовторите ввод.");
            }

        }
    }


    public void analyzeAnswer(String result){
        System.out.println("Ответ сервера получен, обработка.");
        if(result != null) { //строка пуста, например, если нет интернета.
            //в таком случае, сообщение об ошибке уже было, а значит нужно просто обработать
            //успешный случай.
            //System.out.println(result);
            ArrayList<String> list = splitAnswer(result);
            Weather clientWeather = parseAnswer(list);

            if (clientWeather != null) {
                //присвоение значений элементов форм
                //согласно полученным полям
                parent.cityName.setText(clientWeather.city + ", " + clientWeather.country);
                parent.timeLabel.setText(new Time(System.currentTimeMillis()).toString());
                parent.nowLabel.setText("Время обновления: ");
                ImageIcon A = new ImageIcon(getClass().getClassLoader().getResource("images/" +
                        clientWeather.imageName + ".png"));
                parent.weatherImage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/" +
                        clientWeather.imageName + ".png")));
                if (Integer.parseInt(clientWeather.temperature) > 0)
                    parent.temperatureLabel.setText("+" + clientWeather.temperature + " \u00b0" + "C");
                else
                    parent.temperatureLabel.setText(clientWeather.temperature + " \u00b0" + "C");
                parent.weatherTypeText.setText(clientWeather.weatherType);
                parent.windText.setText(", ветер " + clientWeather.windSpeed + " м/с");
            }
        }
    }

    private static ArrayList<String> splitAnswer(String str){
        //str = str.replace("","");
        StringTokenizer stk = new StringTokenizer(str,";"); //логические отрезки разделены запятыми
        int z = stk.countTokens(); //количество разделителей в строке
        ArrayList<String> list = new ArrayList<>();
        //System.out.println("Тестирование метода parseAnswer\n");
        for(int i = 0; i<z; i++)
        {
            list.add(stk.nextToken());
            //System.out.println(list.get(i));
        }
        return list;
    }

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
}