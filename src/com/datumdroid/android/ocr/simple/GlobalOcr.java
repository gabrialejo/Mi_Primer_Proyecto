package com.datumdroid.android.ocr.simple;

import android.widget.Button;

// Desarrollado por José A. Azpurua - Junio 2014 - Última modificación: 16 Dic. 2014 
// clase tipo variables globales utilizadas para la parte nativa principal
public class GlobalOcr {
    public static int ivar1=0,ivar2;
    public static int cuentafotos=0;
    public static String svar1 = "NO",svar2;
    public static String sPlate ="";
    public static String Salida="";
    public static String ajuste="nuevo";
    public static String Perito="";   
    public static Button b2 = null;
    //public static int[] myarray1=new int[10];
    //public static String estimateId="";	// Candidata a eliminarse por no aplicar en este alcance
    //public static String mismoFolder="";	// Candidata a eliminarse por no aplicar en este alcance
   // public static String rutaAjusteG="";	// Candidata a eliminarse por no aplicar en este alcance
    public static String rutaFotos="";
    public static String sFOLDER_VEHIDNUM = "";
    public static String eObject = "";
    public static String sFecha = "";
    public static String sMarca = "";
    public static String sAño = "";
    public static String sModelo = "";
    public static String sPlaca = "";
    public static String[] Estimates=new String[10];		// Guarda los Esimates Id del Ajuste seleccionado
    public static String[] Jsons=new String[10];			// Guarda los jsons de cada ajuste
    public static String[] CheckEstimates=new String[10];	// Guarda los indices de los registros seleccionados para ser sincronizados
    public static String[] RutaAjusteG=new String[10];		// Guarda las Ruta donde estan almacenadas las fotos tomadas del ajuste
}