package com.halaat.halaat.Activity;

public class DownloadingAudioList {
    String audiotitle;
    String username;

    public String getAudioname() {
        return audioname;
    }

    public void setAudioname(String audioname) {
        this.audioname = audioname;
    }

    String audioname;

    public DownloadingAudioList() {

    }    public DownloadingAudioList(String audiotitle, String username,String audioname) {
        this.audiotitle = audiotitle;
        this.username = username;
        this.audioname = audioname;
    }

    public String getAudiotitle() {
        return audiotitle;
    }

    public void setAudiotitle(String audiotitle) {
        this.audiotitle = audiotitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsrname(String username) {
        this.username = username;
    }

}
