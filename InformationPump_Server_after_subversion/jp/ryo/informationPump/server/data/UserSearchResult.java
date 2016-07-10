package jp.ryo.informationPump.server.data;

public class UserSearchResult {
    public UserProfile users[];
    public double similarities[];//results‚ÌŠe—v‘f‚É‘Î‚µ‚Ä‚Ì—ÞŽ—“x

    public UserSearchResult(UserProfile[] users, double[] similarities) {
        this.users = users;
        this.similarities = similarities;
    }
}
