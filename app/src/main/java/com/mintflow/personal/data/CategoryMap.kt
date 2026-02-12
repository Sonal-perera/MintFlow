package com.mintflow.personal.data

import com.mintflow.personal.R

class CategoryMap {
    fun getIconColorMap(): HashMap<String, Int> {
        val iconColorMap = HashMap<String, Int>()
        iconColorMap["Food"] = R.color.food_icon
        iconColorMap["Transport"] = R.color.transport_icon
        iconColorMap["Bills"] = R.color.bills_icon
        iconColorMap["Entertainment"] = R.color.enter_icon
        iconColorMap["Housing"] = R.color.house_icon
        iconColorMap["Shopping"] = R.color.shopping_icon
        iconColorMap["Health"] = R.color.health_icon
        iconColorMap["Education"] = R.color.education_icon
        iconColorMap["Personal Care"] = R.color.personal_icon
        iconColorMap["Salary"] = R.color.work_icon
        iconColorMap["Savings"] = R.color.savings_icon
        iconColorMap["Travel"] = R.color.travel_icon
        iconColorMap["Gifts"] = R.color.gift_icon
        iconColorMap["Pets"] = R.color.pets_icon
        iconColorMap["Debt Payment"] = R.color.debt_icon
        iconColorMap["Subscription"] = R.color.subscription_icon

        return iconColorMap
    }

    fun getBgColorMap(): HashMap<String, Int> {
        val bgColorMap = HashMap<String, Int>()
        bgColorMap["Food"] = R.color.food_bg
        bgColorMap["Transport"] = R.color.transport_bg
        bgColorMap["Bills"] = R.color.bills_bg
        bgColorMap["Entertainment"] = R.color.enter_bg
        bgColorMap["Housing"] = R.color.house_bg
        bgColorMap["Shopping"] = R.color.shopping_bg
        bgColorMap["Health"] = R.color.health_bg
        bgColorMap["Education"] = R.color.education_bg
        bgColorMap["Personal Care"] = R.color.personal_bg
        bgColorMap["Salary"] = R.color.work_bg
        bgColorMap["Savings"] = R.color.savings_bg
        bgColorMap["Travel"] = R.color.travel_bg
        bgColorMap["Gifts"] = R.color.gift_bg
        bgColorMap["Pets"] = R.color.pets_bg
        bgColorMap["Debt Payment"] = R.color.debt_bg
        bgColorMap["Subscription"] = R.color.subscription_bg
        return bgColorMap
    }

    fun getIconMap(): HashMap<String, Int> {
        val iconMap = HashMap<String, Int>()
        iconMap["Food"] = R.drawable.food
        iconMap["Transport"] = R.drawable.transport
        iconMap["Bills"] = R.drawable.bills
        iconMap["Entertainment"] = R.drawable.entertainment
        iconMap["Housing"] = R.drawable.housing
        iconMap["Shopping"] = R.drawable.shopping
        iconMap["Health"] = R.drawable.personal_care
        iconMap["Education"] = R.drawable.education
        iconMap["Personal Care"] = R.drawable.health
        iconMap["Salary"] = R.drawable.sallary
        iconMap["Savings"] = R.drawable.savings
        iconMap["Travel"] = R.drawable.travel
        iconMap["Gifts"] = R.drawable.gifts
        iconMap["Pets"] = R.drawable.pets
        iconMap["Debt Payment"] = R.drawable.debt
        iconMap["Subscription"] = R.drawable.subscription
        return iconMap
    }
}