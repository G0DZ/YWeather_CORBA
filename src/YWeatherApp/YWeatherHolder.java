package YWeatherApp;

/**
* YWeatherApp/YWeatherHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from YWeather.idl
* 13 ������ 2015 �. 16:18:31 MSK
*/

public final class YWeatherHolder implements org.omg.CORBA.portable.Streamable
{
  public YWeatherApp.YWeather value = null;

  public YWeatherHolder ()
  {
  }

  public YWeatherHolder (YWeatherApp.YWeather initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = YWeatherApp.YWeatherHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    YWeatherApp.YWeatherHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return YWeatherApp.YWeatherHelper.type ();
  }

}
