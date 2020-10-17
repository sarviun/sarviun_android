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

    /**
     * převádí WGS-84 do S-JTSK
     *
     */

    public static double[] WGStoJTSK(double X, double Y, double h) {
        final double altitude = h;

        double d2r = Math.PI / 180;
        double a = 6378137.0;
        double f1 = 298.257223563;
        double dx = -570.69;
        double dy = -85.69;
        double dz = -462.84;
        double wx = 4.99821 / 3600 * Math.PI / 180;
        double wy = 1.58676 / 3600 * Math.PI / 180;
        double wz = 5.2611 / 3600 * Math.PI / 180;
        double m = -3.543e-6;

        double latitude = X;
        double longtitude = Y;
        double B = latitude * d2r;
        double L = longtitude * d2r;
        double H = altitude;

        double e2 = 1 - Math.pow(1 - 1 / f1, 2);
        double rho = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(B), 2));
        double x1 = (rho + H) * Math.cos(B) * Math.cos(L);
        double y1 = (rho + H) * Math.cos(B) * Math.sin(L);
        double z1 = ((1 - e2) * rho + H) * Math.sin(B);

        double x2 = dx + (1 + m) * (x1 + wz * y1 - wy * z1);
        double y2 = dy + (1 + m) * (-wz * x1 + y1 + wx * z1);
        double z2 = dz + (1 + m) * (wy * x1 - wx * y1 + z1);

        a = 6377397.15508;
        f1 = 299.152812853;
        double ab = f1 / (f1 - 1);
        double p = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
        e2 = 1 - Math.pow(1 - 1 / f1, 2);
        double th = Math.atan(z2 * ab / p);
        double st = Math.sin(th);
        double ct = Math.cos(th);
        double t = (z2 + e2 * ab * a * (st * st * st)) / (p - e2 * a * (ct * ct * ct));

        B = Math.atan(t);
        H = Math.sqrt(1 + t * t) * (p - a / Math.sqrt(1 + (1 - e2) * t * t));
        L = 2 * Math.atan(y2 / (p + x2));

        a = 6377397.15508;
        double e = 0.081696831215303;
        double n = 0.97992470462083;
        double rho0 = 12310230.12797036;
        double sinUQ = 0.863499969506341;
        double cosUQ = 0.504348889819882;
        double sinVQ = 0.420215144586493;
        double cosVQ = 0.907424504992097;
        double alpha = 1.000597498371542;
        double k2 = 1.00685001861538;

        double sinB = Math.sin(B);
        t = (1 - e * sinB) / (1 + e * sinB);
        t = Math.pow(1 + sinB, 2) / (1 - Math.pow(sinB, 2))	* Math.exp(e * Math.log(t));
        t = k2 * Math.exp(alpha * Math.log(t));

        double sinU = (t - 1) / (t + 1);
        double cosU = Math.sqrt(1 - sinU * sinU);
        double V = alpha * L;
        double sinV = Math.sin(V);
        double cosV = Math.cos(V);
        double cosDV = cosVQ * cosV + sinVQ * sinV;
        double sinDV = sinVQ * cosV - cosVQ * sinV;
        double sinS = sinUQ * sinU + cosUQ * cosU * cosDV;
        double cosS = Math.sqrt(1 - sinS * sinS);
        double sinD = sinDV * cosU / cosS;
        double cosD = Math.sqrt(1 - sinD * sinD);

        double eps = n * Math.atan(sinD / cosD);
        rho = rho0 * Math.exp(-n * Math.log((1 + sinS) / cosS));

        double CX = rho * Math.sin(eps);
        double CY = rho * Math.cos(eps);

        return new double[]{-CX, -CY};
    }


    /**
     * Prevadi Spherical Pseudo-Mercator to WGS84
     */

    public static final double RADIUS = 6378137.0; /* in meters on the equator */
    /* These functions take their length parameter in meters and return an angle in degrees */

    public static double y2lat(double aY) {
        return Math.toDegrees(Math.atan(Math.exp(aY / RADIUS)) * 2 - Math.PI/2);
    }
    public static double x2lon(double aX) {
        return Math.toDegrees(aX / RADIUS);
    }
}