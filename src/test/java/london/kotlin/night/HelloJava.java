package london.kotlin.night;

public class HelloJava {
    private String name;

    public HelloJava(String name) {
        this.name = name;
    }

    public String sayHello() {
        return "hello " + name;
    }

    public String getName() {
        return name;
    }
}