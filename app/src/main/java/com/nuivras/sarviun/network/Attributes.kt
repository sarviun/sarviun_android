package com.nuivras.sarviun.network


import android.os.Parcelable
import android.view.View
import com.nuivras.sarviun.R
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Attributes(
    @Json(name = "Addr_type")
    val addrType: String,
    @Json(name = "Loc_name")
    val locName: String,
    @Json(name = "Type")
    val type: String,
    @Json(name = "City")
    val city: String,
    @Json(name = "Country")
    val country: String,
    @Json(name = "Xmin")
    val xmin: Double,
    @Json(name = "Xmax")
    val xmax: Double,
    @Json(name = "Ymin")
    val ymin: Double,
    @Json(name = "Ymax")
    val ymax: Double,
    @Json(name = "Match_addr")
    val matchAddr: String,
    @Json(name = "Score")
    val score: Int
) : Parcelable {

    val typeTranslated = when (type) {
        "ParcelaDefinicniBod" -> Type.PARCELA_DEFINICNI_BOD
        "AdresniMisto" -> Type.ADRESNI_MISTO
        "Ulice" -> Type.ULICE
        "ZakladniSidelniJednotka" -> Type.ZAKLADNI_SIDELNI_JEDNOTKA
        "KatastralniUzemi" -> Type.KATASTRALNI_UZEMI
        "MestskyObvodMestskaCastVeStatutarnimMesteAHlavnimMestePraze" -> Type.MESTSKY_OBVOD_MESTSKA_CAST
        "SpravniObvodVHlavnimMestePraze" -> Type.SPRAVNI_OBVOD_PRAHA
        "CastObce" -> Type.CAST_OBCE
        "Obec" -> Type.OBEC
        "ObecSPoverenymObecnimUradem" -> Type.OBEC_SPOU
        "ObecSRozsirenouPusobnosti" -> Type.OBEC_SROP
        "Okres" -> Type.OKRES
        "VyssiUzemneSamospravnyCelek" -> Type.VYSSI_CELEK

        else -> Type.NEZNAMY
    }

    val isPoint = typeTranslated == Type.ADRESNI_MISTO || typeTranslated == Type.PARCELA_DEFINICNI_BOD
    val katastrRedirectionVisibility = if (isPoint) View.VISIBLE else View.GONE

}

enum class Type (val resourceString: Int, val layerId: Int) {

    PARCELA_DEFINICNI_BOD(R.string.type_parcela_definicni_bod, 0),
    ADRESNI_MISTO(R.string.type_adresni_misto, 1),
    STAVEBNI_OBJEKT(R.string.stavebni_objekt, 3),
    ULICE(R.string.type_ulice, 4),
    PARCELA(R.string.parcela, 5),
    ZAKLADNI_SIDELNI_JEDNOTKA(R.string.type_zakladni_sidelni_jednotka, 6),
    KATASTRALNI_UZEMI(R.string.type_katastralni_uzemi, 7),
    MESTSKY_OBVOD_MESTSKA_CAST(R.string.type_mo_praha, 8),
    SPRAVNI_OBVOD_PRAHA(R.string.type_so_praha, 10),
    CAST_OBCE(R.string.type_cast_obce, 11),
    OBEC(R.string.type_obec, 12),
    OBEC_SPOU(R.string.type_obec_po, 13),
    OBEC_SROP(R.string.type_obec_rp, 14),
    OKRES(R.string.type_okres, 15),
    VYSSI_CELEK(R.string.type_vyssi_celek, 17),
    NEZNAMY(R.string.type_unknown, -1)
}

