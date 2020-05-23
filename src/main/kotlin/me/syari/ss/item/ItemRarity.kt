package me.syari.ss.item

enum class ItemRarity(val shortName: String, val fullName: String) {
    Common("C", "コモン"),
    Uncommon("UC", "アンコモン"),
    Rare("R", "レア"),
    HighRare("HR", "ハイレア"),
    SuperRare("SR", "スーパーレア"),
    UltraRare("UR", "ウルトラレア")
}