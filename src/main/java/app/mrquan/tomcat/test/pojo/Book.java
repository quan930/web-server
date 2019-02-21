package app.mrquan.tomcat.test.pojo;

public class Book {
    private String name;
    private Integer id;

    public Book(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
