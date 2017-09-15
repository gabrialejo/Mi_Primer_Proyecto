package com.datumdroid.android.ocr.simple;

/**
 * 
 * @author danielme.com
 *
 */
public class Row
{
	private String title;

	private String subtitle;
	
	private String estimate;
	
	private String json;

	private boolean checked;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getSubtitle()
	{
		return subtitle;
	}

	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}
	
	public String getEstimateId()
	{
		return estimate;
	}

	public void setEstimateId(String estimate)
	{
		this.estimate = estimate;
	}
	
	public String getJson()
	{
		return json;
	}

	public void setJson(String json)
	{
		this.json = json;
	}	

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

}
