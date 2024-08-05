package com.newmview.wifi.other;

public class AgentDetailsModel {

    String filename = " ", filesize, file_path = null, tcp_ip_eth, tcp_ip_wifi, tcp_port, file_checksum,
            ssdt = " ", sedt = " ", noofiteration = " ", period = null, agent_type = "", classname = null,
            m_name = null, jar_name = null, jar_size = null, status, agent_version = null,
            currentiteration = "", frequnecy = null, running_state = null, thread_name, upload_status = " ";
    String task_type;
    String scheduled_status;
    String priority;
    String lastScheduledTime;
    String data;

    public String getData() {
        System.out.println(" the data is here"+data);
        return data;

    }

    public void setData(String data) {
        this.data = data;
    }

    public String getScheduledStatus() {
        return scheduled_status;
    }

    public void setScheduledStatus(String scheduled_status) {
        this.scheduled_status = scheduled_status;
    }

    public String getLastScheduledTime() {
        return lastScheduledTime;
    }

    public void setLastScheduledTime(String lastScheduledTime) {
        this.lastScheduledTime = lastScheduledTime;
    }

    public String getTaskType() {
        return task_type;
    }

    public void setTaskType(String task_type) {
        this.task_type = task_type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFileName() {
        return filename;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getTcp_ip_eth() {
        return tcp_ip_eth;
    }

    public void setTcp_ip_eth(String tcp_ip_eth) {
        this.tcp_ip_eth = tcp_ip_eth;
    }

    public String getTcp_ip_wifi() {
        return tcp_ip_wifi;
    }

    public void setTcp_ip_wifi(String tcp_ip_wifi) {
        this.tcp_ip_wifi = tcp_ip_wifi;
    }

    public String getTcp_port() {
        return tcp_port;
    }

    public void setTcp_port(String tcp_port) {
        this.tcp_port = tcp_port;
    }

    public String getFile_checksum() {
        return file_checksum;
    }

    public void setFile_checksum(String file_checksum) {
        this.file_checksum = file_checksum;
    }

    public String getSsdt() {
        return ssdt;
    }

    public void setSsdt(String ssdt) {
        this.ssdt = ssdt;
    }

    public String getSedt() {
        return sedt;
    }

    public void setSedt(String sedt) {
        this.sedt = sedt;
    }

    public String getNoOfIteration() {
        return noofiteration;
    }

    public void setNoOfIteration(String noofiteration) {
        this.noofiteration = noofiteration;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getAgent_type() {
        return agent_type;
    }

    public void setAgent_type(String agent_type) {
        this.agent_type = agent_type;
    }

    public String getClassName() {
        return classname;
    }

    public void setClassName(String classname) {
        this.classname = classname;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getJar_name() {
        return jar_name;
    }

    public void setJar_name(String jar_name) {
        this.jar_name = jar_name;
    }

    public String getJar_size() {
        return jar_size;
    }

    public void setJar_size(String jar_size) {
        this.jar_size = jar_size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAgent_version() {
        return agent_version;
    }

    public void setAgent_version(String agent_version) {
        this.agent_version = agent_version;
    }

    public String getCurrentiteration() {
        return currentiteration;
    }

    public void setCurrentiteration(String currentiteration) {
        this.currentiteration = currentiteration;
    }

    public String getFrequnecy() {
        return frequnecy;
    }

    public void setFrequnecy(String frequnecy) {
        this.frequnecy = frequnecy;
    }

    public String getRunning_state() {
        return running_state;
    }

    public void setRunning_state(String running_state) {
        this.running_state = running_state;
    }

    public String getThread_name() {
        return thread_name;
    }

    public void setThread_name(String thread_name) {
        this.thread_name = thread_name;
    }

    public String getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(String upload_status) {
        this.upload_status = upload_status;
    }


}
