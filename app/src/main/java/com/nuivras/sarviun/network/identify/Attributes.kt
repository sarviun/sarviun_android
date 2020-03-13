package com.nuivras.sarviun.network.identify


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attributes(

    //SPOLECNE
    @Json(name = "objectid")
    val objectid: String?,
    @Json(name = "Zdroj geometrie")
    val zdrojGeometrie: String?,
    @Json(name = "shape")
    val shape: String?,
    @Json(name = "st_area(shape)")
    val stAreashape: String?,
    @Json(name = "st_length(shape)")
    val stLengthshape: String?,
    @Json(name = "Konec platnosti")
    val konecPlatnosti: String?,
    @Json(name = "Příznak nesprávnosti")
    val priznakNespravnosti: String?,
    @Json(name = "Začátek platnosti")
    val zacatekPlatnosti: String?,
    @Json(name = "ID transakce v RÚIAN")
    val iDTransakceVRUIAN: String?,


    //STAVEBNI OBJEKT

    @Json(name = "Kód stavebního objektu")
    val kodStavebnihoObjektu: String?,
    @Json(name = "Seznam čísel domovních")
    val seznamCiselDomovnich: String?,
    @Json(name = "Identifikační parcela")
    val identifikacniParcela: String?,
    @Json(name = "Typ stavebního objektu")
    val typStavebnihoObjektu: String?,
    @Json(name = "Způsob využití")
    val zpusobVyuziti: String?,
    @Json(name = "Nadřazená část obce")
    val nadrazenaCastObce: String?,
    @Json(name = "Nadřazený MOMC")
    val nadrazenyMOMC: String?,
    @Json(name = "ID návrhu změny v ISÚI")
    val iDNavrhuZmenyVISUI: String?,
    @Json(name = "ID budovy v ISKN")
    val iDBudovyVISKN: String?,
    @Json(name = "Datum dokončení stavebního objektu")
    val datumDokonceniStavebnihoObjektu: String?,
    @Json(name = "Druh svislé nosné konstrukce")
    val druhSvisleNosneKonstrukce: String?,
    @Json(name = "Obestavěný prostor v m3")
    val obestavenyProstorVM3: String?,
    @Json(name = "Počet bytů u stavebního objektu s byty")
    val pocetBytuUStavebnihoObjektuSByty: String?,
    @Json(name = "Počet nadzemních a podzemních podlaží")
    val pocetNadzemnichAPodzemnichPodlazi: String?,
    @Json(name = "Podlahová plocha v m2")
    val podlahovaPlochaVM2: String?,
    @Json(name = "Připojení na kanalizační síť")
    val pripojeniNaKanalizacniSit: String?,
    @Json(name = "Připojení na rozvod plynu")
    val pripojeniNaRozvodPlynu: String?,
    @Json(name = "Připojení na vodovod")
    val pripojeniNaVodovod: String?,
    @Json(name = "Vybavení výtahem")
    val vybaveniVytahem: String?,
    @Json(name = "Zastavěná plocha v m2")
    val zastavenaPlochaVM2: String?,
    @Json(name = "Způsob vytápění")
    val zpusobVytapeni: String?,



    //PARCELA
    @Json(name = "Jednoznačný identifikátor parcely")
    val jednoznacnyIdentifikatorParcely: String?,
    @Json(name = "Kmenové parcelní číslo")
    val kmenoveParcelniCislo: String?,
    @Json(name = "Poddělení čísla parcely")
    val poddeleniCislaParcely: String?,
    @Json(name = "Výměra parcely")
    val vymeraParcely: String?,
    @Json(name = "Způsob využití pozemku")
    val zpusobVyuzitiPozemku: String?,
    @Json(name = "Rozlišení druhu číslování parcely")
    val rozliseniDruhuCislovaniParcely: String?,
    @Json(name = "Kód druhu pozemku")
    val kodDruhuPozemku: String?,
    @Json(name = "Nadřazené katastrální území")
    val nadrazeneKatastralniUzemi: String?,
    @Json(name = "ID řízení v ISKN")
    val iDRizeniVISKN: String?,
    @Json(name = "Číslo parcely")
    val cisloParcely: String?,

    //ZSJ
    //+nadrazezene katastr. uzemi
    //+id navrhu zmeny v ISUI
    @Json(name = "Kód ZSJ")
    val kodZSJ: String?,
    @Json(name = "Název ZSJ")
    val nazevZSJ: String?,
    @Json(name = "Výměra ZSJ v m2")
    val vymeraZSJVM2: String?,
    @Json(name = "Převažující charakter využití ZSJ")
    val prevazujiciCharakterVyuzitiZSJ: String?,
    @Json(name = "Datum vzniku prvku")
    val datumVznikuPrvku: String?,

    //KATASTRALNI UZEMI
    //+ISKN,ISUI,datum vzniku prvku
    @Json(name = "Kód katastrálního území")
    val kodKatastralnihoUzemi: String?,
    @Json(name = "Název katastrálního území")
    val nazevKatastralnihoUzemi: String?,
    @Json(name = "Příznak existence digitální mapy")
    val priznakExistenceDigitalniMapy: String?,
    @Json(name = "Nadřazená obec")
    val nadrazenaObec: String?,

    //momc
    //nadrazena obec, isui, datum vzniku prvku
    @Json(name = "Kód MOMC")
    val kodMOMC: String?,
    @Json(name = "Název MOMC")
    val nazevMOMC: String?,
    @Json(name = "Nadřazený MOP")
    val nadrazenyMOP: String?,
    @Json(name = "Správní obvod")
    val spravniObvod: String?,

    //spravni obvod
    //+ picoviny
    @Json(name = "Kód správního obvodu")
    val kodSpravnihoObvodu: String?,
    @Json(name = "Název správního obvodu")
    val nazevSpravnihoObvodu: String?,
    @Json(name = "Kód správní MOMC")
    val kodSpravniMOMC: String?,

    //cast obce
    @Json(name = "Kód části obce")
    val kodCastiObce: String?,
    @Json(name = "Název části obce")
    val nazevCastiObce: String?,

    //obec
    @Json(name = "Kód obce")
    val kodObce: String?,
    @Json(name = "Název obce")
    val nazevObce: String?,
    @Json(name = "Status obce")
    val statusObce: String?,
    @Json(name = "Nadřazený okres")
    val nadrazenyOkres: String?,
    @Json(name = "Nadřazený POU")
    val nadrazenyPOU: String?,
    @Json(name = "Rozsah členění statutárního města na MOMC")
    val rozsahCleneniStatutarnihoMestaNaMOMC: String?,
    @Json(name = "Typ MOMC, na něž je statutární město rozčleněno")
    val typMOMCNaNezJeStatutarniMestoRozcleneno: String?,
    @Json(name = "Kód územního celku v NUTS / LAU")
    val kodUzemnihoCelkuVNUTSLAU: String?,

    //obec spou
    @Json(name = "Kód POU")
    val kodPOU: String?,
    @Json(name = "Název POU")
    val nazevPOU: String?,
    @Json(name = "Kód správní obce")
    val kodSpravniObce: String?,
    @Json(name = "Nadřazená ORP")
    val nadrazenaORP: String?,

    //obec srop
    @Json(name = "Kód ORP")
    val kodORP: String?,
    @Json(name = "Název ORP")
    val nazevORP: String?,
    @Json(name = "Nadřazený VÚSC")
    val nadrazenyVUSC: String?,

    //okres
    @Json(name = "Kód okresu")
    val kodOkresu: String?,
    @Json(name = "Název okresu")
    val nazevOkresu: String?,
    @Json(name = "Nadřazený kraj 1960")
    val nadrazenyKraj1960: String?,

    //vusc
    @Json(name = "Kód VÚSC")
    val kodVUSC: String?,
    @Json(name = "Název VÚSC")
    val nazevVUSC: String?,
    @Json(name = "Nadřazený region soudržnosti")
    val nadrazenyRegionSoudrznosti: String?



)