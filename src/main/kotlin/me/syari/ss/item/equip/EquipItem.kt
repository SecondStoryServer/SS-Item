package me.syari.ss.item.equip

interface EquipItem {
    companion object {
        private val idToList = mutableMapOf<String, EquipItem>()

        fun clear(){
            idToList.clear()
        }

        fun register(id: String, item: EquipItem){
            idToList[id] = item
        }

        fun getById(id: String): EquipItem? {
            return idToList[id]
        }
    }
}