package sugar.bean;
/*
 *@auther suger
 *2019
 *9:46
 */

import java.util.Date;

public class Childcontent {
    private Integer id;

    private Integer tasksid;

    private String template;

    private Date ddl;

    private String people;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTasksid() {
        return tasksid;
    }

    public void setTasksid(Integer tasksid) {
        this.tasksid = tasksid;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template == null ? null : template.trim();
    }

    public Date getDdl() {
        return ddl;
    }

    public void setDdl(Date ddl) {
        this.ddl = ddl;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people == null ? null : people.trim();
    }

    @Override
    public String toString() {
        return "Childcontent{" +
                "id=" + id +
                ", tasksid=" + tasksid +
                ", template='" + template + '\'' +
                ", ddl=" + ddl +
                ", people='" + people + '\'' +
                '}';
    }
}
