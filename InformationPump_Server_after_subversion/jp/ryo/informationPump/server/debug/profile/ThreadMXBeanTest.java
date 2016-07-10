package jp.ryo.informationPump.server.debug.profile;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;


public class ThreadMXBeanTest {
    private ThreadMXBean mbean;

    private JTabbedPane tabbedPane;
    private HashMap past_cpu_times = new HashMap();

    public ThreadMXBeanTest() {
        initView();

        mbean = ManagementFactory.getThreadMXBean();
        mbean.setThreadContentionMonitoringEnabled(true);
        updateInfo();
    }

    private void initView() {
        JFrame frame = new JFrame("Thread Infomation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        tabbedPane = new JTabbedPane();
        frame.getContentPane().add(tabbedPane);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    clearInfoTab();
                    updateInfo();
                }
            });
        Timer update_timer = new Timer();
        update_timer.schedule(new UpdateTask(this),10000l,10000l);
        
        frame.getContentPane().add(updateButton, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    public void updateInfo() {
        long[] ids = mbean.getAllThreadIds();
        ArrayList tmp_infos = new ArrayList();
        
        for(int i=0;i<ids.length;i++){
//        for (long id: ids) {
          long cputime = mbean.getThreadCpuTime(ids[i]);
          ThreadInfo info = mbean.getThreadInfo(ids[i], 5);
        
          tmp_infos.add(new ProfileTabData(new Long(ids[i]),new Long(cputime),info));
//            if(past_cpu_times.get(new Long(ids[i]))!=null){
//                Long past_time = (Long)past_cpu_times.get(new Long(ids[i]));
//                addInfoTab(cputime - past_time.longValue(), info);
//            }else{
//                addInfoTab(cputime, info);    
//            }
            
          if(past_cpu_times.get(new Long(ids[i]))!=null){
              Long past_time = (Long)past_cpu_times.get(new Long(ids[i]));
              tmp_infos.add(new ProfileTabData(new Long(ids[i]),new Long(cputime - past_time.longValue()),info));
          }else{
              tmp_infos.add(new ProfileTabData(new Long(ids[i]),new Long(cputime),info));
          }
          past_cpu_times.put(new Long(ids[i]),new Long(cputime));
        }
        Collections.sort(tmp_infos,new ProfileDataComp());
        
        int len = tmp_infos.size();
        for(int i=0;i<len;i++){
            ProfileTabData data = (ProfileTabData)tmp_infos.get(i);
            addInfoTab(data.cpu_time.longValue(), data.info);    
        }
    }

    public void clearInfoTab() {
        tabbedPane.removeAll();
    }

    private void addInfoTab(long cputime, ThreadInfo info) {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new CompactGridLayout(0, 2, 2, 20));

        updateInfoTab(panel, cputime, info);

        tabbedPane.add(info.getThreadId() + ":" + info.getThreadName() , new JScrollPane(panel));
    }

    private void updateInfoTab(JPanel panel, long cputime, ThreadInfo info) {
        long id = -1;
        String name = "";
        Thread.State state = null;
        boolean isSuspended = false;
        boolean isInNative = false;
        long blockedTime = -1;
        long blockedCount = -1;
        long waitedTime = -1;
        long waitedCount = -1;
        String lockName = "";
        long lockOwnerId = -1;
        String lockOwnerName = "";
        String stackTrace = "";

        synchronized (info) {
            id = info.getThreadId();
            name = info.getThreadName();
            state = info.getThreadState();
            isSuspended = info.isSuspended();
            isInNative = info.isInNative();
            blockedTime = info.getBlockedTime();
            blockedCount = info.getBlockedCount();
            waitedTime = info.getWaitedTime();
            waitedCount = info.getWaitedCount();
            lockName = info.getLockName();
            lockOwnerId = info.getLockOwnerId();
            lockOwnerName = info.getLockOwnerName();
            
            StringBuilder buffer = new StringBuilder();
            buffer.append("<html><body>");
            StackTraceElement trace_elements[] = info.getStackTrace(); 
            for (int i=0;i<trace_elements.length;i++) {
                buffer.append(trace_elements[i].toString());
                buffer.append("<br>");
            }
            buffer.append("</body></html>");
            stackTrace = buffer.toString();
        }

        panel.add(new JLabel("ID"));
        panel.add(new JLabel(Long.toString(id)));
        panel.add(new JLabel("Name"));
        panel.add(new JLabel(name));
        panel.add(new JLabel("State"));
        panel.add(new JLabel(state.toString()));
            
        panel.add(new JLabel("CPU Time"));
        panel.add(new JLabel((cputime / 1000.0) + "ms"));
        
        panel.add(new JLabel("Suspended"));
        panel.add(new JLabel(Boolean.toString(isSuspended)));
        
        panel.add(new JLabel("Thread in JNI"));
        panel.add(new JLabel(Boolean.toString(isInNative)));
        
        panel.add(new JLabel("Blocked Time"));
        panel.add(new JLabel(Long.toString(blockedTime)));
        panel.add(new JLabel("Blocked Count"));
        panel.add(new JLabel(Long.toString(blockedCount)));
        
        panel.add(new JLabel("Waited Time"));
        panel.add(new JLabel(Long.toString(waitedTime)));
        panel.add(new JLabel("Waited Count"));
        panel.add(new JLabel(Long.toString(waitedCount)));
            
        panel.add(new JLabel("Lock Name"));
        panel.add(new JLabel(lockName));
        panel.add(new JLabel("Lock Owner ID"));
        panel.add(new JLabel(Long.toString(lockOwnerId)));
        panel.add(new JLabel("Lock Owner Name"));
        panel.add(new JLabel(lockOwnerName));
            
        panel.add(new JLabel("Stack Trace"));
        panel.add(new JLabel(stackTrace));
    }

    public static void main(String[] args) {
        new ThreadMXBeanTest();
    }
}
