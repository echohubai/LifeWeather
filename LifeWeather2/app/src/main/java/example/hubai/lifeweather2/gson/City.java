package example.hubai.lifeweather2.gson;

/**
 * Created by len_titude on 2017/5/14.
 */

public class City {

    public Basic basic;

    public String status;

    public class Basic{
        public String city;
        public String cnty;
        public String lat;
        public String lon;
        public String prov;
    }
}
