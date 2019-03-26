package com.mood;

public class OverrideTest {
    public static void main(String args[]){
        Father son=new Son();
        Number  price= son.actionPrice(40);
        System.out.println(price);
    }
}

class Father{
    public static void sayHello(){
        System.out.println("Father hello");
    }
    public Number actionPrice(double price) {
        return price * 0.8;
    }
}
class Son extends Father{
    @Override
    public Double actionPrice(double price) {
        return 0.9 * price;
    }
    public Double createDayMoney(Double money,int day) {
        return money*day;
    }
    public Double createDayMoney(Double money,int day,int ...num) {
        Double result= money*day;
        for (int i:num){
            result=result*i;
        }
        return result;
    }
}