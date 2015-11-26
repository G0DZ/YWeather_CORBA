package Client;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.TreeMap;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

public class ClientFrame extends JFrame {
    TreeMap<String, Integer> A;
    private static final long serialVersionUID = 5506524279338381258L;
    //будем описывать используемые элементы сверху-вниз
    JComboBox comboBox;
    JLabel helloTextLabel;
    JLabel cityName;
    JLabel todayText;
    JLabel nowLabel;
    JLabel timeLabel;
    JLabel weatherImage;
    JLabel temperatureLabel;
    JLabel weatherTypeText;
    JLabel windText;
    JPanel weatherSquare;
    JPanel p1, p2, p3, p4, p5, p6, p7;
    JTextField textField;
    JTextArea TEST_WeatherText; //вывод погоды в текстовом виде

    static JButton button_start;	//кнопка обновления погоды
    static String button_start_txt = "Обновить";

    public ClientFrame(){
        //поехали
        try { //стиль оформления
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //инициализация переменных
        helloTextLabel = new JLabel("Погода в городе ");
        cityName = new JLabel();
        nowLabel = new JLabel("");
        timeLabel = new JLabel();
        weatherSquare = new JPanel();
        weatherImage = new JLabel();
        temperatureLabel = new JLabel();
        weatherTypeText = new JLabel();
        windText = new JLabel();
        todayText = new JLabel("Сегодня ");

        button_start = new JButton(); //создаем кнопку.
        button_start.setText(button_start_txt);
        button_start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        textField = new JTextField("26063");

        TEST_WeatherText = new JTextArea("");
        TEST_WeatherText.setEnabled(false);
        TEST_WeatherText.setFont(new Font("Calibri", Font.PLAIN, 16));

        //получение информации для комбобокса
        A = CityReader.getCities();
        String[] comboStr = new String[A.size()];
        int i = 0;
        for (String key : A.keySet()) {
            comboStr[i++]= key;
        }
        //подключение нового комбобокса с фукнцией поиска
        //http://www.orbital-computer.de/JComboBox/
        comboBox = new JComboBox(comboStr);
        comboBox.setEditable(true);
        // get the combo boxes editor component
        JTextComponent editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
        // change the editor's document
        editor.setDocument(new JComboPopUp(comboBox));

        //устанавливаем размер элементов
        helloTextLabel.setBounds(10, 0, 145, 20);
        cityName.setBounds(147, 0, 403, 20);
        windText.setBounds(147, 0, 403, 20);
        temperatureLabel.setBounds(650, 110, 100, 30);
        nowLabel.setBounds(10, 25, 65, 20);
        timeLabel.setBounds(68, 27, 50, 18);
        weatherSquare.setBounds(10, 50, 280, 90);
        weatherImage.setBounds(20, 60, 48, 48);
        weatherTypeText.setBounds(80, 65, 200, 30);
        TEST_WeatherText.setBounds(0, 90, 300, 250);
        //textField.setBounds(50, 325, 200, 20);
        comboBox.setBounds(50, 325, 200, 20);
        button_start.setBounds(50, 350, 200, 30);


        //настраиваем действия на кнопки
        ClientFrameEngine cEngine = new ClientFrameEngine(this);
        button_start.addActionListener(cEngine);

        button_start.registerKeyboardAction(button_start.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        button_start.registerKeyboardAction(button_start.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);

        //дополнительная настройка элементов (Шрифт и прочее)
        helloTextLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
        cityName.setFont(new Font("Verdana", Font.BOLD, 14));
        nowLabel.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        timeLabel.setFont(new Font("TimesRoman", Font.BOLD, 12));
        weatherSquare.setBackground(new Color(255,248,205));
        temperatureLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        weatherTypeText.setFont(new Font("Verdana", Font.PLAIN, 12));

        //создаем 5 панелек
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        p6 = new JPanel();
        p7 = new JPanel();
        // Задаем layout для каждой из трех панелей
        BorderLayout bl1= new BorderLayout();
        FlowLayout fl2 = new FlowLayout();
        FlowLayout fl3 = new FlowLayout();
        FlowLayout fl4 = new FlowLayout();
        BorderLayout bl5= new BorderLayout();
        FlowLayout fl6 = new FlowLayout();
        FlowLayout fl7 = new FlowLayout();
        p1.setLayout(bl1);
        p2.setLayout(fl2);
        p3.setLayout(fl3);
        p4.setLayout(fl4);
        p5.setLayout(bl5);
        p6.setLayout(fl6);
        p7.setLayout(fl7);

        p2.add(helloTextLabel);
        p2.add(cityName);
        p2.add(temperatureLabel);

        p3.add(todayText);
        p3.add(weatherTypeText);
        p3.add(windText);

        p4.add(comboBox);
        //p4.add(textField);
        p4.add(button_start);


        p6.add(weatherImage);
        p7.add(nowLabel);
        p7.add(timeLabel);

        p5.add(p3, BorderLayout.NORTH);
        p5.add(p6, BorderLayout.CENTER);
        p5.add(p7, BorderLayout.SOUTH);

        p1.add(p2, BorderLayout.NORTH);
        p1.add(p5, BorderLayout.CENTER);
        p1.add(p4, BorderLayout.SOUTH);

        getContentPane().add(p1);
        setPreferredSize(new Dimension(450, 220));

        this.pack();
        this.setTitle("YWeather");									//Заголовок окна
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 	    //действие при закрытии
        this.setResizable(false);            						// Запрет на изменение размера экрана
        //Точка размещения экрана
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        int w = this.getWidth();
        int h = this.getHeight();
        center.x = center.x - w/2;
        center.y = center.y - h/2;
        this.setLocation(center);
        this.setVisible(true);										// Отображаю окно
    }

    public static void main(String[] args) { //эта функция может быть и в другом классе
        /*int ii = 0;
        while (args.length > ii) {
            CORBAConnect.ARGS[ii] = args[ii];
            ii++;
        }*/
        CORBAConnect.ARGS[0] = "-ORBInitialPort";
        CORBAConnect.ARGS[1] = "1050";
        CORBAConnect.ARGS[2] = "-ORBInitialHost";
        CORBAConnect.ARGS[3] = "169.254.90.197";

        new ClientFrame(); //Создаем экземпляр нашего приложения
    }
}