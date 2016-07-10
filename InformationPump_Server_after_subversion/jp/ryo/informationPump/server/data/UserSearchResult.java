package jp.ryo.informationPump.server.data;

public class UserSearchResult {
    public UserProfile users[];
    public double similarities[];//resultsの各要素に対しての類似度

    public UserSearchResult(UserProfile[] users, double[] similarities) {
        this.users = users;
        this.similarities = similarities;
    }
}
