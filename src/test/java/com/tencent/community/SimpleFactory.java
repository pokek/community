package com.tencent.community;

public class SimpleFactory {
    public static void main(String[] args) {
       Phone phone = PhoneFactory.run("Iphone");
       phone.run();
    }
}


interface Phone{
    void run();
}

class HuaWeiPhone implements Phone{
    @Override
    public void run() {
        System.out.println("生成华为手机");
    }
}

class IphonePhone implements Phone{
    @Override
    public void run() {
        System.out.println("生成苹果手机");
    }
}

class PhoneFactory{
    public static Phone run(String type){
        Phone phone = null;
        if("HuaWeiPhone".equals(type)){
            phone = new HuaWeiPhone();
        }else if("Iphone".equals(type)){
            phone = new IphonePhone();
        }
        return phone;
    }
}


