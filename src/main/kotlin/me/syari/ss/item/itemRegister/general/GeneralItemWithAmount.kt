package me.syari.ss.item.itemRegister.general

data class GeneralItemWithAmount(
    val data: GeneralItem,
    var amount: Int
) {
    companion object {
        fun from(list: Map<GeneralItem, Int>): Map<GeneralItem, GeneralItemWithAmount> {
            return list.map { it.key to GeneralItemWithAmount(it.key, it.value) }.toMap()
        }
    }
}