package cn.snow.mvvmdemojava.study;

public class SingleTon {


    public static class SingleTonEhan {//饿汉模式

        private static final SingleTonEhan INSTANCE = new SingleTonEhan();

        private SingleTonEhan() {

        }


        public static SingleTonEhan getInstance() {
            return INSTANCE;
        }


    }

    public static class SingleTonLhan {//懒汉模式 DCL double check lock模式

        private static volatile SingleTonLhan mInstance; //volatile jdk1.5之前cpu指令重排会有dcl失效问题 jdk1.5之后加入这个关键字禁止指令重排

        private SingleTonLhan() {

        }

        public static SingleTonLhan getInstance() {
            if (mInstance == null) {
                synchronized (SingleTonLhan.class) {
                    if (mInstance == null) {
                        mInstance = new SingleTonLhan();
                    }
                }
            }
            return mInstance;
        }
    }

    public enum SingleTonEnum {
        INSTANCE;

        final String string = "QaQ";

        public void doSomething() {
            System.out.println(string);
        }
    }

}
