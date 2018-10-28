package leavesc.hello.retrofit2_rxjava2;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import leavesc.hello.retrofit2_rxjava2.viewmodel.BaseViewModel;

/**
 * 作者：叶应是叶
 * 时间：2018/10/27 21:14
 * 描述：
 */
public class WeatherViewModel extends BaseViewModel {

    private MutableLiveData<Weather> weatherLiveData;

    private WeatherRepo weatherRepo;

    public WeatherViewModel() {
        weatherLiveData = new MutableLiveData<>();
        weatherRepo = new WeatherRepo(new WeatherDataSource(this), this);
    }

    public void queryWeather(String cityName, String key) {
        weatherRepo.queryWeather(cityName, key).observe(lifecycleOwner, new Observer<Weather>() {
            @Override
            public void onChanged(@Nullable Weather weather) {
                weatherLiveData.setValue(weather);
            }
        });
    }

    public MutableLiveData<Weather> getWeatherLiveData() {
        return weatherLiveData;
    }
}
