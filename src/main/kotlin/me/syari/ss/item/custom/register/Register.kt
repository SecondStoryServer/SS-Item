package me.syari.ss.item.custom.register

interface Register {
    fun register()

    companion object {
        private val list = mutableSetOf<Register>()

        fun registerAll(){
            list.forEach {
                it.register()
            }
        }

        fun add(vararg register: Register){
            list.addAll(register)
        }
    }
}