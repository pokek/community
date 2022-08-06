package com.tencent.community.service;

import com.tencent.community.util.Dog;

public class YellowDog extends Dog {

    @Override
    protected void start() {
        super.start();
    }
}

class test{

    public static void main(String[] args) {
        Dog dog = new YellowDog();
//        dog.start();        protect修饰的方法，只能在同包下使用，即使被其它包的继承，但是其它包的类可以重写，且重写后的方法仍只能在其自己的所在包下使用   所以不同包之间 protectd方法不存在多态  但是同包下是具有多态性的

        YellowDog yellowDog = new YellowDog();
        yellowDog.start();

    }
}
