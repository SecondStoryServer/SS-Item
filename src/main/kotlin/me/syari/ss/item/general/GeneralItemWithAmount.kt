package me.syari.ss.item.general

data class GeneralItemWithAmount(
    val data: GeneralItem, var amount: Int
) {
    companion object {
        fun from(list: Map<GeneralItem, Int>): List<GeneralItemWithAmount> {
            return list.map { GeneralItemWithAmount(it.key, it.value) }
        }
    }
}