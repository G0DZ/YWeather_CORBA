// Стандартный класс обработки погоды
// Хранит все данные о погоде
// Он есть и у клиента, и у сервера
package Service;

public class Weather {
    public String city;
    public String country;
    public String time;
    public String temperature;
    public String weatherType;
    public String windDirection;
    public String windSpeed;
    public String humidity;
    public String pressure;
    public String imageName;

    public String toString() {
        return  "city=" + city
                + ";country=" + country
                + ";time=" + time
                + ";temperature=" + temperature
                + ";weatherType=" + weatherType
                + ";windDirection=" + windDirection
                + ";windSpeed=" + windSpeed
                + ";humidity=" + humidity
                + ";pressure=" + pressure
                +";imageName=" + imageName;
    }
}

