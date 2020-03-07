package com.nuivras.sarviun.utils

object CoordinatesConvertor {
    /***
     * Musi byt pozitivni krovak, data prichazi v negativnim
     * @param X
     * @param Y
     * @param H vyska
     * @return
     */
    fun JTSKtoWGS(X: Double, Y: Double, H: Double): DoubleArray {

        /* Přepočet vstupích údajů - vychazi z nejakeho skriptu, ktery jsem nasel na Internetu - nejsem autorem prepoctu. */ /*Vypocet zemepisnych souradnic z rovinnych souradnic*/
        val e = 0.081696831215303
        val n = 0.97992470462083
        val konst_u_ro = 12310230.12797036
        val sinUQ = 0.863499969506341
        val cosUQ = 0.504348889819882
        val sinVQ = 0.420215144586493
        val cosVQ = 0.907424504992097
        val alfa = 1.000597498371542
        val k = 1.003419163966575
        var ro = Math.sqrt(X * X + Y * Y)
        val epsilon = 2 * Math.atan(Y / (ro + X))
        val D = epsilon / n
        val S =
            2 * Math.atan(Math.exp(1 / n * Math.log(konst_u_ro / ro))) - Math.PI / 2
        val sinS = Math.sin(S)
        val cosS = Math.cos(S)
        val sinU = sinUQ * sinS - cosUQ * cosS * Math.cos(D)
        val cosU = Math.sqrt(1 - sinU * sinU)
        val sinDV = Math.sin(D) * cosS / cosU
        val cosDV = Math.sqrt(1 - sinDV * sinDV)
        val sinV = sinVQ * cosDV - cosVQ * sinDV
        val cosV = cosVQ * cosDV + sinVQ * sinDV
        val Ljtsk = 2 * Math.atan(sinV / (1 + cosV)) / alfa
        var t =
            Math.exp(2 / alfa * Math.log((1 + sinU) / cosU / k))
        var pom = (t - 1) / (t + 1)
        var sinB = 0.0
        do {
            sinB = pom
            pom = t * Math.exp(e * Math.log((1 + e * sinB) / (1 - e * sinB)))
            pom = (pom - 1) / (pom + 1)
        } while (Math.abs(pom - sinB) > 1e-15)
        val Bjtsk = Math.atan(pom / Math.sqrt(1 - pom * pom))
        /* Pravoúhlé souřadnice ve S-JTSK */
        var a = 6377397.15508
        var f_1 = 299.152812853
        var e2 = 1 - (1 - 1 / f_1) * (1 - 1 / f_1)
        ro = a / Math.sqrt(1 - e2 * Math.sin(Bjtsk) * Math.sin(Bjtsk))
        val x = (ro + H) * Math.cos(Bjtsk) * Math.cos(Ljtsk)
        val y = (ro + H) * Math.cos(Bjtsk) * Math.sin(Ljtsk)
        val z = ((1 - e2) * ro + H) * Math.sin(Bjtsk)
        /* Pravoúhlé souřadnice v WGS-84*/
        val dx = 570.69
        val dy = 85.69
        val dz = 462.84
        val wz = -5.2611 / 3600 * Math.PI / 180
        val wy = -1.58676 / 3600 * Math.PI / 180
        val wx = -4.99821 / 3600 * Math.PI / 180
        val m = 3.543e-6
        val xn = dx + (1 + m) * (x + wz * y - wy * z)
        val yn = dy + (1 + m) * (-wz * x + y + wx * z)
        val zn = dz + (1 + m) * (wy * x - wx * y + z)
        /* Geodetické souřadnice v systému WGS-84*/a = 6378137.0
        f_1 = 298.257223563
        val a_b = f_1 / (f_1 - 1)
        val p = Math.sqrt(xn * xn + yn * yn)
        e2 = 1 - (1 - 1 / f_1) * (1 - 1 / f_1)
        val theta = Math.atan(zn * a_b / p)
        val st = Math.sin(theta)
        val ct = Math.cos(theta)
        t = (zn + e2 * a_b * a * st * st * st) / (p - e2 * a * ct * ct * ct)
        var B = Math.atan(t)
        var L = 2 * Math.atan(yn / (p + xn))
        /* Formát výstupních hodnot */B = B / Math.PI * 180
        L = L / Math.PI * 180
        return doubleArrayOf(B,L)
    }
}