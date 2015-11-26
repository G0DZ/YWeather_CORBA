package Service;

//интерфейс для получения погоды
public interface YWeather
{
    /**
     * Получаем погоду указанного в cityID городе
     * @param cityID    номер города
     */
    String getStringWeather(int cityID);
}