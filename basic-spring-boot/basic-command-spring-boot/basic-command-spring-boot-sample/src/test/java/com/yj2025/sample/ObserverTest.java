package com.yj2025.sample;

import com.yj2025.basic.support.DeepObserverAware;
import lombok.Data;
import org.junit.jupiter.api.Test;

public class ObserverTest {

    @Test
    public void test() {
        A a = new A();
        System.out.println("自上而下 subscribeOutGoing");
        a.subscribeOutGoing(ClassNamePrinter.class, classNamePrinter -> {
            classNamePrinter.print();
        });
        System.out.println("自下而上 subscribeIncoming");
        a.subscribeIncoming(ClassNamePrinter.class, classNamePrinter -> {
            classNamePrinter.print();
        });
    }


    @Data
    public static class A implements DeepObserverAware, ClassNamePrinter {
        private String label = "A";
        private B[] b = {new B(), new B()};
    }

    @Data
    public static class B implements DeepObserverAware, ClassNamePrinter {
        private String name = "B";
        private C c = new C();
        private D d = new D();
    }

    @Data
    public static class C implements DeepObserverAware, ClassNamePrinter {
        private String name = "C";
    }

    @Data
    public static class D implements DeepObserverAware, ClassNamePrinter {
        private String name = "D";
    }

    public interface ClassNamePrinter {
        default void print() {
            System.out.println(this.getClass().getName());
        }
    }
}
