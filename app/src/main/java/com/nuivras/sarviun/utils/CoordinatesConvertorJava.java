package com.nuivras.sarviun.utils;

public class CoordinatesConvertorJava
{
    /***
     * Musi byt pozitivni krovak, data prichazi v negativnim
     * @param X
     * @param Y
     * @param H vyska
     * @return
     */
    public static double[] JTSKtoWGS(double X, double Y, double H)
    {
        double[] coord = new double[2];

        /* Přepočet vstupích údajů - vychazi z nejakeho skriptu, ktery jsem nasel na Internetu - nejsem autorem prepoctu. */

        /*Vypocet zemepisnych souradnic z rovinnych souradnic*/
        double e=0.081696831215303;
        double n=0.97992470462083;
        double konst_u_ro=12310230.12797036;
        double sinUQ=0.863499969506341;
        double cosUQ=0.504348889819882;
        double sinVQ=0.420215144586493;
        double cosVQ=0.907424504992097;
        double alfa=1.000597498371542;
        double k=1.003419163966575;
        double ro=Math.sqrt(X*X+Y*Y);
        double epsilon=2*Math.atan(Y/(ro+X));
        double D=epsilon/n;
        double S=2*Math.atan(Math.exp(1/n*Math.log(konst_u_ro/ro)))-Math.PI/2;
        double sinS=Math.sin(S);
        double cosS=Math.cos(S);
        double sinU=sinUQ*sinS-cosUQ*cosS*Math.cos(D);
        double cosU=Math.sqrt(1-sinU*sinU);
        double sinDV=Math.sin(D)*cosS/cosU;
        double cosDV=Math.sqrt(1-sinDV*sinDV);
        double sinV=sinVQ*cosDV-cosVQ*sinDV;
        double cosV=cosVQ*cosDV+sinVQ*sinDV;
        double Ljtsk=2*Math.atan(sinV/(1+cosV))/alfa;
        double t=Math.exp(2/alfa*Math.log((1+sinU)/cosU/k));
        double pom=(t-1)/(t+1);
        double sinB = 0;
        do {
            sinB=pom;
            pom=t*Math.exp(e*Math.log((1+e*sinB)/(1-e*sinB)));
            pom=(pom-1)/(pom+1);
        } while (Math.abs(pom-sinB)>1e-15);

        double Bjtsk=Math.atan(pom/Math.sqrt(1-pom*pom));


        /* Pravoúhlé souřadnice ve S-JTSK */
        double a=6377397.15508;
        double f_1=299.152812853;
        double e2=1-(1-1/f_1)*(1-1/f_1);
        ro=a/Math.sqrt(1-e2*Math.sin(Bjtsk)*Math.sin(Bjtsk));
        double x=(ro+H)*Math.cos(Bjtsk)*Math.cos(Ljtsk);
        double y=(ro+H)*Math.cos(Bjtsk)*Math.sin(Ljtsk);
        double z=((1-e2)*ro+H)*Math.sin(Bjtsk);

        /* Pravoúhlé souřadnice v WGS-84*/
        double dx=570.69;
        double dy=85.69;
        double dz=462.84;
        double wz=-5.2611/3600*Math.PI/180;
        double wy=-1.58676/3600*Math.PI/180;
        double wx=-4.99821/3600*Math.PI/180;
        double m=3.543e-6;
        double xn=dx+(1+m)*(x+wz*y-wy*z);
        double yn=dy+(1+m)*(-wz*x+y+wx*z);
        double zn=dz+(1+m)*(wy*x-wx*y+z);

        /* Geodetické souřadnice v systému WGS-84*/
        a=6378137.0; f_1=298.257223563;
        double a_b=f_1/(f_1-1);
        double p=Math.sqrt(xn*xn+yn*yn);
        e2=1-(1-1/f_1)*(1-1/f_1);
        double theta=Math.atan(zn*a_b/p);
        double st=Math.sin(theta);
        double ct=Math.cos(theta);
        t=(zn+e2*a_b*a*st*st*st)/(p-e2*a*ct*ct*ct);
        double B=Math.atan(t);
        double L=2*Math.atan(yn/(p+xn));

        /* Formát výstupních hodnot */
        B=B/Math.PI*180;
        coord[0] = B;

        L=L/Math.PI*180;
        coord[1] = L;

        return coord;
    }

}