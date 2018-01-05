package com.techstartegies.model;

/**
 * Created by Waseem on 25-Nov-15.
 */
public class Doctor {

    private String doctorName, thumbnailUrl;
    private String doctorSpecialization;
    private String loginType;

    private String username;
    private int doctorStatus;

    public Doctor() {
    }

    public Doctor(String doctorName, String thumbnailUrl,String specialization,
                 int status) {
        this.doctorName = doctorName;
        this.thumbnailUrl = thumbnailUrl;
        this.doctorSpecialization = specialization;
        this.doctorStatus = status;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String name) {
        this.doctorName = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public int getDoctorStatus() {
        return doctorStatus;
    }

    public void setDoctorStatus(int doctorStatus) {
        this.doctorStatus = doctorStatus;
    }
}
