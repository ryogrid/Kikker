package jp.ryo.informationPump.server.debug.profile;

import java.lang.management.ThreadInfo;

public class ProfileTabData {
    public Long id;
    public Long cpu_time;
    public ThreadInfo info;

    public ProfileTabData(Long id, Long cpu_time, ThreadInfo info) {
        super();
        this.id = id;
        this.cpu_time = cpu_time;
        this.info = info;
    }
}
