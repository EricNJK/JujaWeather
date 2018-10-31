package com.c15.jujaweather;


public class WeatherData {
    public Coordinates coord;
    public int id, cod, dt, visibility;
    public String name;        //default Juja
    public Main main;
    public Weather[] weather;
    public Wind wind;
    public Clouds clouds;
    public Meta sys;

    WeatherData(){
    }

    class Main{
        public float temp, temp_min, temp_max;
        public int pressure, humidity;
        Main(){}
    }

    class Coordinates {
        public float lon, lat;
        Coordinates(){}
    }

    class Weather {
        public int id;
        String description, main;
        public String icon;     //****
        Weather(){}
    }

    class Wind {
        public float speed;
        public int deg; //direction
        Wind(){}
    }

    class Clouds{
        public int all;        //percentage
        Clouds(){}
    }

    class Meta {
        public int type, id;
        public String country, message;
        public int sunrise, sunset;     //OpenWeatherMap date is in seconds not millis...
        Meta(){}

    }
}

