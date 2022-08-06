package com.tencent.community;

public class FactoryMethod {
    public static void main(String[] args) {
        HuaWeiFactory huaWeiFactory = new HuaWeiFactory();
        HuaWeiPhone_1 run = huaWeiFactory.run();
        run.run();
    }
}


abstract class Phone_1{
    public abstract void run();
}

class HuaWeiPhone_1 extends Phone_1{
    @Override
    public void run() {
        System.out.println("生成华为手机");
    }
}

class Iphone_1 extends Phone_1{
    @Override
    public void run() {
        System.out.println("生成苹果手机");
    }
}

abstract class FatherFactory{
    public abstract Phone_1 run();
}

class HuaWeiFactory extends FatherFactory{
    @Override
    public HuaWeiPhone_1 run() {
        return new HuaWeiPhone_1();
    }
}

class IphoneFactory extends FatherFactory{
    @Override
    public Iphone_1 run() {
        return new Iphone_1();
    }
}
