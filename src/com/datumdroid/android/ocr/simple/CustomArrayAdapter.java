package com.datumdroid.android.ocr.simple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.*;
import ve.com.inma.mobility_4_1.api.R;
//import com.danielme.blog.demo.listviewcheckbox.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Custom adapter - "View Holder Pattern".
 * 
 * @author danielme.com - Modificación: 16 Dic. 2014 - José A. Azpurua
 * Modificación para versión de La Previsora: 20 Nov. 2015 - José A. Azpurua
 * Última modificación para adaptar a los cambios hechos en la versión Internacional: 09 Sep. 2016 - José A. Azpurua
 * Clase que maneja el adaptador de la view que define la lista y sus acciones
 */
public class CustomArrayAdapter extends ArrayAdapter<Row>
{
	private LayoutInflater layoutInflater;
	private String[] Estimates;
	public String versionLog = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = ""; 
	private String sPrimaryStorage = "/mnt/sdcard/";
	private String sSecondaryStorage = "/mnt/sdcard/";

	public CustomArrayAdapter(Context context, List<Row> objects)
	{
		super(context, 0, objects);
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		// holder pattern
		Holder holder = null;
		if (convertView == null)
		{
			holder = new Holder();

			convertView = layoutInflater.inflate(R.layout.listview_row, null);
			holder.setTextViewTitle((TextView) convertView.findViewById(R.id.textViewTitle));
			holder.setTextViewSubtitle((TextView) convertView.findViewById(R.id.textViewSubtitle));
			holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkBox));
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		final Row row = getItem(position);
		holder.getTextViewTitle().setText(row.getTitle());
		holder.getTextViewSubtitle().setText(row.getSubtitle());
		
		holder.getCheckBox().setTag(row.getTitle());
		holder.getCheckBox().setChecked(row.isChecked());	
		final View fila= convertView;
		changeBackground(getContext(), fila, row.isChecked());		
		holder.getCheckBox().setOnCheckedChangeListener(new OnCheckedChangeListener()
		{ 
			@Override
			public void onCheckedChanged(CompoundButton view, boolean isChecked)
			{
				Context context2 = getContext();
				//asegura que se modifica la Row originalmente asociado a este checkbox
				//para evitar que al reciclar la vista se reinicie el row que antes se mostraba en esta
				//fila. Es imprescindible tagear el Row antes de establecer el valor del checkbox

				if (row.getTitle().equals(view.getTag().toString()))
				{
					row.setChecked(isChecked);
					changeBackground(CustomArrayAdapter.this.getContext(), fila, isChecked);
					//GlobalOcr.Chequeado = 1;
					//Toast.makeText(context2, "onCheckedChanged isChecked: "+isChecked, Toast.LENGTH_LONG).show();
					GlobalOcr.b2.setEnabled(true);
					if(GlobalOcr.CheckEstimates.length==0){
						return;
					}									
					GlobalOcr.CheckEstimates[position+1] = String.valueOf(position+1);
					GlobalOcr.Jsons[position+1] = row.getJson();
					GlobalOcr.Estimates[position+1] = row.getEstimateId();
//					   Toast toast = new Toast(context2);
//					   Toast.makeText(context2, "onCheckedChanged: ", Toast.LENGTH_LONG).show();
						//Toast.makeText(context2, "getCount(): "+getCount(), Toast.LENGTH_LONG).show();
//					   toast.show();
					//desmarca todas los demÃ¡s (si sÃ³lo se permite un item marcado a la vez).
					int cont = getCount();
					appendLog(" class: CustomArrayAdapter.java - onCheckedChanged - cont (getCount) : " + String.valueOf(cont));
												
					if(cont > 0){
						Row row = null;
						for(int i=0 ; i<cont ; i++)
						{						
							if (i == position && isChecked)   
							{
								row = (Row) getItem(i);
								GlobalOcr.Estimates[i] = row.getEstimateId();
								if(cont > 0){
									GlobalOcr.CheckEstimates[i] = String.valueOf(i);
								}else{
									 i=0;
								}
								GlobalOcr.Jsons[i] = row.getJson();
								appendLog("Custom Array Adapter CheckEstimates[i] : " + String.valueOf(GlobalOcr.CheckEstimates[i]));
								appendLog("Custom Array Adapter Estimates[i] : " + i+" * " + String.valueOf(GlobalOcr.Estimates[i]));
								appendLog("Custom Array Adapter GlobalOcr.Jsons[i] : " + i+" * " + String.valueOf(GlobalOcr.Jsons[i]));
								//appendLog("Custom Array Adapter GlobalOcr.RutaAjusteG[i] : " + i+" * " + String.valueOf(GlobalOcr.RutaAjusteG[i]));
	//						   Toast toast = new Toast(context2);
	//						   Toast.makeText(context2, "i: "+String.valueOf(i), Toast.LENGTH_LONG).show();
	//						   toast.show();
	//						   Toast.makeText(context2, "Estimates[i]: "+String.valueOf(GlobalOcr.Estimates[i]), Toast.LENGTH_LONG).show();
	//						   toast.show();
								//row.setChecked(false);								
							}
						}
					}
					notifyDataSetChanged();
				}
			}
		});
		
		return convertView;
		
	}
	
	public void appendLog(String text)
	{      
//		   Context context3 = getContext();
		   TomaFechaSistema();
//		   Toast toast = new Toast(context3);
//		   Toast.makeText(context3, "Dentro de ApendLog", Toast.LENGTH_LONG).show();
//		   toast.show();		   
		   versionLog = "" + fechaLogD;		
		   File logFile = new File(sPrimaryStorage+"sdcard/Download/logINMAEstCustomArrayAdapter_"+versionLog+".file");
		   if (!logFile.exists())
		   {
			  try
			  {
				 logFile.createNewFile();
			  } 
			  catch (IOException e)
			  {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			  }
		   }
		   try
		   {
			  //BufferedWriter for performance, true to set append to file flag
			  BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true),10 * 1024); 
			  //buf = new char[8192];  8 * 1024
			  buf.append(fechaHoraLogD+"-"+text);
			  //buf.append(text);
			  buf.newLine();
			  buf.close();
		   }
		   catch (IOException e)
		   {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		   }
		}	
		
	public void TomaFechaSistema() {
	
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
			   Date date = new Date();
			   //System.out.println(dateFormat.format(date));
		fechaLogD = ""+dateFormat.format(date);
		//Toast.makeText(this, "appendLogF - fechaLogD: "+fechaLogD, Toast.LENGTH_LONG).show();
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
		
		fechaHoraLogD  = ""+dateFormat2.format(date);
	}
	
	/**
	 * Set the background of a row based on the value of its checkbox value. Checkbox has its own style.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void changeBackground(Context context, View row, boolean checked)
	{
		if (row != null)
		{
			Drawable drawable = context.getResources().getDrawable(R.drawable.listview_selector_checked);
			if (checked)
			{
				drawable = context.getResources().getDrawable(R.drawable.listview_selector_checked);
			}
			else
			{
				drawable = context.getResources().getDrawable(R.drawable.listview_selector);
			}
			int sdk = android.os.Build.VERSION.SDK_INT;
			if(sdk < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			    row.setBackgroundDrawable(drawable);
			} else {
			    row.setBackgroundDrawable(drawable);
			}
		}
	}	

}

class Holder
{
	TextView textViewTitle;
	TextView textViewSubtitle;
	CheckBox checkBox;

	public TextView getTextViewTitle()
	{
		return textViewTitle;
	}

	public void setTextViewTitle(TextView textViewTitle)
	{
		this.textViewTitle = textViewTitle;
	}

	public TextView getTextViewSubtitle()
	{
		return textViewSubtitle;
	}

	public void setTextViewSubtitle(TextView textViewSubtitle)
	{
		this.textViewSubtitle = textViewSubtitle;
	}

	public CheckBox getCheckBox()
	{
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox)
	{
		this.checkBox = checkBox;
	}	

}
