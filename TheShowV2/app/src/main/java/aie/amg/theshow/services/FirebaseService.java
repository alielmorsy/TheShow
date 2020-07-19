package aie.amg.theshow.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FirebaseService extends Service {
    public void onMessageReceived(@NonNull Math remoteMessage) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
