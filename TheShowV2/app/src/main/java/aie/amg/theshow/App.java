package aie.amg.theshow;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Collections;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("1AE9A0E962BAE89D4CC2B672D6409DB3")).build());

        MobileAds.initialize(this, "ca-app-pub-3924327175857175~1339730461");

    }

}
