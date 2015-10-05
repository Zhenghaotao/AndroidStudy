package com.example.guessmusic.model;

public class Song {
	// 歌曲的名称
	private String mSongName;

	// 歌曲的文件名
	private String mSongFileName;

	// 歌曲的名字长度
	private int mNameLength;

	public char[] getNameCharacters() {
		return mSongName.toCharArray();
	}

	public String getmSongName() {
		return mSongName;
	}

	public void setmSongName(String songName) {
		this.mSongName = songName;
		mNameLength = songName.length();
	}

	public String getmSongFileName() {
		return mSongFileName;
	}

	public void setmSongFileName(String songFileName) {
		this.mSongFileName = songFileName;
	}

	public int getmNameLength() {
		return mNameLength;
	}

}
