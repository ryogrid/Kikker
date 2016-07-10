package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.*;
import jp.ryo.informationPump.server.data.UserProfile;
import jp.ryo.informationPump.server.persistent.PersistentManager;

public class UserDataExport {

    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();
        
        UserAdmin f_u_admin = (UserAdmin)p_manager.readObjectFromFile(p_manager.USER_DATA_PATH + p_manager.USER_ADMIN_FILE);
        FileUserAdmin.setInstanceFromFile(f_u_admin);
        
        f_u_admin = (FileUserAdmin)FileUserAdmin.getInstance();
        
        DBUserAdmin db_u_admin = (DBUserAdmin)DBUserAdmin.getInstance();
        
        UserProfile profiles[] =  f_u_admin.getAllUser();
        
        int len = profiles.length;;
        for(int i=0;i<len;i++){
            db_u_admin.createNewUser(profiles[i]);
        }
    }

}