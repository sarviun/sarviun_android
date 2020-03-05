package com.nuivras.sarviun.network.identify


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attributes(
    @Json(name = "objectid")
    val objectid: String,
    @Json(name = "Kód stavebního objektu")
    val kodStavebnihoObjektu: String,
    @Json(name = "Příznak nesprávnosti")
    val priznakNespravnosti: String,
    @Json(name = "Seznam čísel domovních")
    val seznamCiselDomovnich: String,
    @Json(name = "Identifikační parcela")
    val identifikacniParcela: String,
    @Json(name = "Typ stavebního objektu")
    val typStavebnihoObjektu: String,
    @Json(name = "Způsob využití")
    val zpusobVyuziti: String,
    @Json(name = "Nadřazená část obce")
    val nadrazenaCastObce: String,
    @Json(name = "Nadřazený MOMC")
    val nadrazenyMOMC: String,
    @Json(name = "Začátek platnosti")
    val zacatekPlatnosti: String,
    @Json(name = "Konec platnosti")
    val konecPlatnosti: String,
    @Json(name = "ID transakce v RÚIAN")
    val iDTransakceVRUIAN: String,
    @Json(name = "ID návrhu změny v ISÚI")
    val iDNavrhuZmenyVISUI: String,
    @Json(name = "ID budovy v ISKN")
    val iDBudovyVISKN: String,
    @Json(name = "Datum dokončení stavebního objektu")
    val datumDokonceniStavebnihoObjektu: String,
    @Json(name = "Druh svislé nosné konstrukce")
    val druhSvisleNosneKonstrukce: String,
    @Json(name = "Obestavěný prostor v m3")
    val obestavenyProstorVM3: String,
    @Json(name = "Počet bytů u stavebního objektu s byty")
    val pocetBytuUStavebnihoObjektuSByty: String,
    @Json(name = "Počet nadzemních a podzemních podlaží")
    val pocetNadzemnichAPodzemnichPodlazi: String,
    @Json(name = "Podlahová plocha v m2")
    val podlahovaPlochaVM2: String,
    @Json(name = "Připojení na kanalizační síť")
    val pripojeniNaKanalizacniSit: String,
    @Json(name = "Připojení na rozvod plynu")
    val pripojeniNaRozvodPlynu: String,
    @Json(name = "Připojení na vodovod")
    val pripojeniNaVodovod: String,
    @Json(name = "Vybavení výtahem")
    val vybaveniVytahem: String,
    @Json(name = "Zastavěná plocha v m2")
    val zastavenaPlochaVM2: String,
    @Json(name = "Způsob vytápění")
    val zpusobVytapeni: String,
    @Json(name = "Zdroj geometrie")
    val zdrojGeometrie: String,
    @Json(name = "shape")
    val shape: String,
    @Json(name = "st_area(shape)")
    val stAreashape: String,
    @Json(name = "st_length(shape)")
    val stLengthshape: String
)