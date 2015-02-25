package ndk.asteroids.prohor.collage_maker.collage_maker;

/**
 * Created by prohor on 25/02/15.
 */
public class CollageMaker {
    private static final String TAG = "CollageMaker";
    private static final int DEF_COLLAGE_PXL_SIZE = 1024;

    private static CollageMaker instance;

    public static synchronized CollageMaker getInstance() {
        if (instance == null) {
            instance = new CollageMaker();
        }
        return instance;
    }

    // TODO: change interface? + implement
    public static void generateCollage() {

    }
}
