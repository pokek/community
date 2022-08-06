package com.tencent.community;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.FactoryBean;
// 抽象工厂方式，对具体类的增加是符合开闭原则的，如增加联想手机，联想电脑，只需要增加具体产品类和具体超级子工厂的工厂类
// 而对于如若增加 一种新的产品 如 冰箱  则不符合开闭原则，需要改产品抽象类代码和超级工厂代码
// 即从继承图的上下关系来看，上层扩展不符合开闭原则，下层扩展是符合开闭原则的
public class AbstractFactoryPattern {
    public static void main(String[] args) {
        HuaWeiFactory_2 huaWeiFactory_2 = new HuaWeiFactory_2();
        HuaWeiPhone_2 phoneFactory = huaWeiFactory_2.createPhoneFactory();
        phoneFactory.run();
    }
}

abstract class SuperFactory{
    public abstract Phone_2 createPhoneFactory();

    public abstract Computer createComputerFactory();
}

class HuaWeiFactory_2 extends SuperFactory{
    @Override
    public HuaWeiPhone_2 createPhoneFactory() {
        return new HuaWeiPhone_2();
    }

    @Override
    public HuaWeiComputer createComputerFactory() {
        return new HuaWeiComputer();
    }
}


class Iphone_Factory extends SuperFactory{
    @Override
    public Iphone_Phone_2 createPhoneFactory() {
        return new Iphone_Phone_2();
    }

    @Override
    public Iphone_Comupter createComputerFactory() {
        return new Iphone_Comupter();
    }
}

abstract class ElectronincProducts{
    public abstract void run();
}

abstract class Phone_2 extends ElectronincProducts{

}

abstract class Computer extends ElectronincProducts{

}

class HuaWeiPhone_2 extends Phone_2{
    @Override
    public void run() {
        System.out.println("生成华为手机");
    }
}

class HuaWeiComputer extends Computer{
    @Override
    public void run() {
        System.out.println("生成华为电脑");
    }
}

class Iphone_Phone_2 extends Phone_2{
    @Override
    public void run() {
        System.out.println("生成苹果手机");
    }
}

class Iphone_Comupter extends Computer{
    @Override
    public void run() {
        System.out.println("生成苹果电脑");
    }
}







