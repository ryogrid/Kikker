package jp.ryo.informationPump.server.data;

public class UserSearchResult {
    public UserProfile users[];
    public double similarities[];//results�̊e�v�f�ɑ΂��Ă̗ގ��x

    public UserSearchResult(UserProfile[] users, double[] similarities) {
        this.users = users;
        this.similarities = similarities;
    }
}
