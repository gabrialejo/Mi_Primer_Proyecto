package com.datumdroid.android.ocr.simple;

/*
 * Clase "bean" que define al objeto paquete de foto, el cual incluye a la foto y sus atributos:
 *  EstimateId, SupNumber (0), consecutivo (#item), caption (nombre de la foto), filename (<foto1.jpg>), data (map de bytes, data cruda de la imagen)
 * @author José A. Azpurua - 2014 - Última modificación: 16 Dic. 2014 - José A. Azpurua
 */
public class FotoPackage {
	private String estimateId;
	private String supNumber;
	private int consecutivo;
	private String caption;
	private String filename;
	private byte[] data;

	/**
	 * @return the estimateId
	 */
	public String getEstimateId() {
		return estimateId;
	}
	/**
	 * @param estimateId the estimateId to set
	 */
	public void setEstimateId(String estimateId) {
		this.estimateId = estimateId;
	}
	/**
	 * @return the Suplement Number
	 */
	public String getSupNumber() {
		return supNumber;
	}
	/**
	 * @param Suplement Number the Suplement Number to set
	 */
	public void setSupNumber(String supNumber) {
		this.supNumber = supNumber;
	}
	/**
	 * @return the consecutivo
	 */
	public int getConsecutivo() {
		return consecutivo;
	}
	/**
	 * @param consecutivo the consecutivo to set
	 */
	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}
	/**
	 * @return the Caption (foto name)
	 */
	public String getCaption() {
		return caption;
	}
	/**
	 * @param caption the caption to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the Caption (foto name)
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}	
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	/**
	 * @param marca the marca to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

}
