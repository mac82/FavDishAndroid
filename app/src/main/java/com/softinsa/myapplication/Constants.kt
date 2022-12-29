package com.softinsa.myapplication

object Constants {
    const val TAG = "UDEMY"

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"

    const val EXTRA_DISH_DETAILS: String = "ExtraDishDetails"

    const val ALL_ITEMS = "All"
    const val FILTER_SELECTION = "FilterSelection"

    const val API_ENDPOINT = "recipes/random"

    const val API_KEY_QUERY = "apiKey"
    const val LIMIT_LICENSE: String = "limitLicense"
    const val TAGS: String = "tags"
    const val NUMBER: String = "number"

    const val LIMIT_LICENSE_VALUE: Boolean = true
    const val TAGS_VALUE: String = "vegetarian, dessert"
    const val NUMBER_VALUE: Int = 1

    // NOTIFICATIONS
    const val NOTIFICATION_ID = "FavDish_notification_id"
    const val NOTIFICATION_NAME = "FavDish"
    const val NOTIFICATION_CHANNEL = "FavDish_channel_01"

    fun dishTypes(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("others")

        return list
    }


    fun dishCategories(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("Cafe")
        list.add("Bakery")
        list.add("Burger")
        list.add("Steak")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Sandwich")
        list.add("Hot Dogs")
        list.add("Juices")
        list.add("Water")
        list.add("Wraps")
        list.add("Other")

        return list
    }

    fun dishCookTime(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("10")
        list.add("20")
        list.add("30")
        list.add("40")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")

        return list
    }
}