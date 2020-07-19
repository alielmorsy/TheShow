package aie.amg.theshow.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

import aie.amg.theshow.models.DownloadFile;

public class DownloadDatabaseUtil {
    public Context context;
    private DownloadDoa doa;

    public DownloadDatabaseUtil(Context context) {
        doa = DownloadRoom.getINSTANCE(context).getDoa();
        this.context = context;
    }

    public LiveData<List<DownloadFile>> getAll() {
        return doa.getAll();
    }

    public DownloadFile getDownloadFile(int id) {
        return doa.get(id);
    }

    public void update(DownloadFile update) {
        doa.updateDownloaded(update);

    }

    public long add(DownloadFile add) {
        return doa.add(add);


    }

    public DownloadFile getDownloadFile(String fileName, String extra) {
        return doa.get(fileName);
    }

    public void addOrUpdate(DownloadFile file) {

        DownloadFile search = getDownloadFile(file.getShowName(), file.getExtraData());
        if (search == null) {

            add(file);
        } else {

            file.setId(search.getId());
            update(file);
        }

        DownloadFile f = getDownloadFile(file.getShowName(), file.getExtraData());
    }

    public void delete(DownloadFile file) {
        doa.delete(file);
    }

    public void deleteALL() {
        Executors.newFixedThreadPool(4).execute(doa::deleteAll);
    }

    public LiveData<List<DownloadFile>> getType(boolean downloaded) {
        return doa.getType(downloaded);
    }
}
