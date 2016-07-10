package youtube;
import java.util.ArrayList;

public class APITester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            YoutubeHelper.getFeaturedVideoList();
            YoutubeHelper.getFavoriteVideos("BaratsAndBereta");
            YoutubeHelper.getUploadedVideosByUser("BaratsAndBereta");
            YoutubeHelper.getVideoListByTag("Mother",-1,-1);
            YoutubeHelper.getProfile("BaratsAndBereta");
            YoutubeHelper.getVideoDetail("LAPJ0tPHiPw");
            YoutubeHelper.getFriends("BaratsAndBereta");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
