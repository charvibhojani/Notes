package nemosofts.notes.app.entities;

public class NotificationModel {

    String title;
    String subtitle;
    long createdTime;
    int id;

    public NotificationModel(String title, String subtitle, long createdTime, int id) {
        this.title = title;
        this.subtitle = subtitle;
        this.createdTime = createdTime;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
