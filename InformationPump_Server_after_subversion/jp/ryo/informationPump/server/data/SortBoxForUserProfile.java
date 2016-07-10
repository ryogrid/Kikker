package jp.ryo.informationPump.server.data;

public class SortBoxForUserProfile {
    public UserProfile profile;
    public Double simirality;
    
    public SortBoxForUserProfile(UserProfile user,Double simirality) {
        this.profile = user;
        this.simirality = simirality;
    }
}
