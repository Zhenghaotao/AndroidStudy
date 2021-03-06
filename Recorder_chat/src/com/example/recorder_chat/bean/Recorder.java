package com.example.recorder_chat.bean;

public class Recorder {
	private float time;
	private String filePath;
	
	public Recorder(float time, String filePath) {
		super();
		this.time = time;
		this.filePath = filePath;
	}
	public float getTime() {
		return time;
	}
	public void setTime(float time) {
		this.time = time;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public String toString() {
		return "Recorder [time=" + time + ", filePath=" + filePath + "]";
	}
	
}
