package sugar.bean;
/*
 *@auther suger
 *2019
 *11:43
 */

public class testUser {

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "testUser{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
