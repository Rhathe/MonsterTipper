package com.rhathe.monstertipper.services


class CurrentService {
	companion object {
		val map = HashMap<Class<*>, Any>()

		fun setAsCurrent(obj: Any) {
			map[obj.javaClass] = obj
		}

		fun getCurrent(cls: Class<*>): Any? {
			return map[cls]
		}

		fun reset() {
			map.clear()
		}
	}
}