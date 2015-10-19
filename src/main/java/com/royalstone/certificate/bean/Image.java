package com.royalstone.certificate.bean;

public class Image {

	public Image(){
	}
	public Image(String sheetid, int seqno, int imgseqno, String imgFileName) {
		super();
		this.sheetid = sheetid;
		this.seqno = seqno;
		this.imgseqno = imgseqno;
		this.imgFileName = imgFileName;
	}
	private String sheetid;
	private int seqno;
	private int imgseqno;
	private String imgFileName;
	public String getSheetid() {
		return sheetid;
	}
	public void setSheetid(String sheetid) {
		this.sheetid = sheetid;
	}
	public int getSeqno() {
		return seqno;
	}
	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}
	public int getImgseqno() {
		return imgseqno;
	}
	public void setImgseqno(int imgseqno) {
		this.imgseqno = imgseqno;
	}
	public String getImgFileName() {
		return imgFileName;
	}
	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}
}
