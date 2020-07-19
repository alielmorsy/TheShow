package aie.amg.theshow.models;

public class Subtitle {
    public String name;
    public int id;
    public int type;
    public static final int SLAVE=1;
    public static final int Track=0;

    public Subtitle(String name, int id, int type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public Subtitle() {

    }

    public String nameFromUrl() {
        int l = name.lastIndexOf('\\');
        return name.replace(name.substring(l), "");

    }
}
